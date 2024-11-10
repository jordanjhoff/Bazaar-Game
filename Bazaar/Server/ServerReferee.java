package Server;

import Common.RuleBook;
import Player.IPlayer;
import Referee.GameObjectGenerator;
import Referee.GameState;
import Referee.ObservableReferee;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

public class ServerReferee extends ObservableReferee {

    public static int moveTimeoutMS = 10000;
    public ServerReferee(List<IPlayer> players, GameState intermediateState, RuleBook ruleBook, GameObjectGenerator randomizer) {
        super(players, intermediateState, ruleBook, randomizer);
    }

    public ServerReferee(List<IPlayer> players, RuleBook ruleBook) {
        super(players, ruleBook);
    }

    @Override
    protected Optional<GameState> firstPlayerRequest() {
        Callable<Optional<GameState>> method = super::firstPlayerRequest;
        Optional<Optional<GameState>> result = timeout(method, moveTimeoutMS);
        return result.orElseGet(Optional::empty);
    }
    @Override
    protected Optional<GameState> secondPlayerRequest(GameState stateAfterExchanges) {
        Callable<Optional<GameState>> method = () -> secondPlayerRequest(stateAfterExchanges);
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
            future.cancel(true);
        }
        return Optional.empty();
    }
}
