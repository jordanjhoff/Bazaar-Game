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
import java.util.logging.Logger;

/**
 * Represents an ObservableReferee, with additional functionality to time out if a move is not received within a certain amount of time.
 */
public class ServerReferee extends ObservableReferee {
    private final int moveTimeoutMS;
    public ServerReferee(List<IPlayer> players, GameState intermediateState, RuleBook ruleBook, GameObjectGenerator randomizer, int moveTimeoutMS) {
        super(players, intermediateState, ruleBook, randomizer);
        this.moveTimeoutMS = moveTimeoutMS;
    }

    public ServerReferee(List<IPlayer> players, RuleBook ruleBook, int moveTimeoutMS) {
        super(players, ruleBook);
        this.moveTimeoutMS = moveTimeoutMS;
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
            Optional<Boolean> result = CommunicationUtils.timeout(setupTask, moveTimeoutMS);
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
        Optional<Optional<GameState>> result = CommunicationUtils.timeout(method, moveTimeoutMS);
        return result.orElseGet(Optional::empty);
    }
    @Override
        protected Optional<GameState> secondPlayerRequest(GameState stateAfterExchanges) {
        Callable<Optional<GameState>> method = () -> super.secondPlayerRequest(stateAfterExchanges);
        Optional<Optional<GameState>> result = CommunicationUtils.timeout(method, moveTimeoutMS);
        return result.orElseGet(Optional::empty);
    }

    @Override
    protected List<IPlayer> notifyWinners(List<IPlayer> winners) {
        List<IPlayer> successfulWinners = new ArrayList<>(winners);
        for (IPlayer player : this.players.values()) {
            if (!naughtyPlayers.contains(player)) {
                Callable<Boolean> winTask = () -> {
                    player.win(winners.contains(player));
                    return true;
                };
                Optional<Boolean> result = CommunicationUtils.timeout(winTask, moveTimeoutMS);
                if (result.isEmpty()) {
                    successfulWinners.remove(player);
                    naughtyPlayers.add(player);
                }
            }

        }
        notifyListeners(theOneTrueState);
        shutDownListeners();
        return successfulWinners;
    }
}
