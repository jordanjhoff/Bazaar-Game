package Server;

import Common.RuleBook;
import Player.IPlayer;
import Referee.GameObjectGenerator;
import Referee.GameState;
import Referee.ObservableReferee;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * Represents an ObservableReferee, with additional functionality to time out if a move is not received within a certain amount of time.
 */
public class ServerReferee extends ObservableReferee {

    public static int moveTimeoutMS = 10000;
    public ServerReferee(List<IPlayer> players, GameState intermediateState, RuleBook ruleBook, GameObjectGenerator randomizer) {
        super(players, intermediateState, ruleBook, randomizer);
    }

    public ServerReferee(List<IPlayer> players, RuleBook ruleBook) {
        super(players, ruleBook);
    }

    @Override
    /**
     * Provides all players with the equation table
     */
    protected void notifyPlayersOfStart() {
        for (String name : players.keySet()) {
            IPlayer player = players.get(name);
            try {
                player.setup(this.ruleBook.equationTable());
            } catch (Exception e) {
                naughtyPlayers.add(player);
            }
        }
        theOneTrueState = kickNaughtyPlayers();
    }

    @Override
    protected Optional<GameState> firstPlayerRequest() {
        Callable<Optional<GameState>> method = super::firstPlayerRequest;
        Optional<Optional<GameState>> result = timeout(method, moveTimeoutMS);
        return result.orElseGet(Optional::empty);
    }
    @Override
        protected Optional<GameState> secondPlayerRequest(GameState stateAfterExchanges) {
        Callable<Optional<GameState>> method = () -> super.secondPlayerRequest(stateAfterExchanges);
        Optional<Optional<GameState>> result = timeout(method, moveTimeoutMS);
        return result.orElseGet(Optional::empty);
    }

    protected <T> Optional<T> timeout(Callable<T> task, int timeoutMs) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<T> future = executor.submit(task);
        try {
            T result = future.get(timeoutMs, TimeUnit.MILLISECONDS);
            return Optional.of(result);
        } catch (TimeoutException | ExecutionException | InterruptedException ex) {
            future.cancel(true);
        } finally {
            executor.shutdownNow();
        }
        return Optional.empty();
    }
}
