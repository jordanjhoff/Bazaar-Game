package Server;

import Common.PlayerInformation;
import Common.RuleBook;
import Player.IPlayer;
import Referee.GameObjectGenerator;
import Referee.GameState;
import Referee.ObservableReferee;
import Referee.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * Represents an ObservableReferee, with additional functionality to kick a player if a move is not received within a certain amount of time (movetimeoutms)
 */
public class ServerReferee extends ObservableReferee {
    // how long players have to return from a request, in milliseconds
    private final int moveTimeoutMS;

    /**
     * Dispatches other arguments to the superclass (Referee).
     * @param moveTimeoutMS How long players have to return from a move.
     */
    public ServerReferee(List<IPlayer> players, GameState intermediateState, RuleBook ruleBook, GameObjectGenerator randomizer, int moveTimeoutMS, Observer... observers) {
        super(players, intermediateState, ruleBook, randomizer, observers);
        this.moveTimeoutMS = moveTimeoutMS;
    }
    public ServerReferee(List<IPlayer> players, RuleBook ruleBook, int moveTimeoutMS, Observer... observers) {
        super(players, ruleBook, observers);
        this.moveTimeoutMS = moveTimeoutMS;
    }

    /**
     * Provides all players with the equation table. Timeout applies!
     */
    @Override
    protected void notifyPlayersOfStart() {
        for (PlayerInformation p : getGameState().players()) {
            IPlayer player = players.get(p.name().orElseThrow());
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

    /**
     * Requests the player's first move. Timeout Applies
     */
    @Override
    protected Optional<GameState> firstPlayerRequest() {
        Callable<Optional<GameState>> method = super::firstPlayerRequest;
        Optional<Optional<GameState>> result = CommunicationUtils.timeout(method, moveTimeoutMS);
        return result.orElseGet(Optional::empty);
    }

    /**
     * Requests the player's second move. Timeout applies.
     */
    @Override
        protected Optional<GameState> secondPlayerRequest(GameState stateAfterExchanges) {
        Callable<Optional<GameState>> method = () -> super.secondPlayerRequest(stateAfterExchanges);
        Optional<Optional<GameState>> result = CommunicationUtils.timeout(method, moveTimeoutMS);
        return result.orElseGet(Optional::empty);
    }

    /**
     * Notifies players of a win (or loss). Timeout applies.
     * NOTE: Players who have WON the game may be KICKED if they don't return in time!
     * Sportsmanship is important.
     */
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
