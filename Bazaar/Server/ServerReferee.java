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


    /**
     * Provides all players with the equation table
     */
    @Override
    protected void notifyPlayersOfStart() {
        for (String name : players.keySet()) {
            IPlayer player = players.get(name);
            Callable<Boolean> setupTask = () -> {
                player.setup(this.ruleBook.equationTable());
                return true;
            };
            Optional<Boolean> result = timeout(setupTask, moveTimeoutMS);
            if (result.isEmpty()) {
                naughtyPlayers.add(player);
            }
        }
        setupListeners(ruleBook.equationTable());
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

    @Override
    protected List<IPlayer> notifyWinners(List<IPlayer> winners) {
        List<IPlayer> successfulWinners = new ArrayList<>(winners);
        for (IPlayer player : this.players.values()) {
            if (naughtyPlayers.contains(player)) {
                continue;
            }
            Callable<Boolean> winTask = () -> {
              player.win(winners.contains(player));
              return true;
            };
            Optional<Boolean> result = timeout(winTask, moveTimeoutMS);
            if (result.isEmpty()) {
                naughtyPlayers.add(player);
            }
        }
        notifyListeners(theOneTrueState);
        shutDownListeners();
        return successfulWinners;
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
