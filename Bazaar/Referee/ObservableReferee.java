package Referee;

import java.util.*;

import Common.EquationTable;
import Common.RuleBook;
import Player.IPlayer;

/**
 * An extension of Referee, with additional functionality to support observers upon construction
 */
public class ObservableReferee extends Referee {

    protected final Set<Observer> observers = new HashSet<>();
    public ObservableReferee(List<IPlayer> players, GameState intermediateState, RuleBook ruleBook, GameObjectGenerator randomizer, Observer... observers) {
        super(players, intermediateState, ruleBook, randomizer);
        this.observers.addAll(Arrays.asList(observers));
    }

    public ObservableReferee(List<IPlayer> players, RuleBook ruleBook, Observer... observers) {
        super(players, ruleBook);
        this.observers.addAll(Arrays.asList(observers));
    }

    @Override
    protected void notifyPlayersOfStart() {
        super.notifyPlayersOfStart();
        setupListeners(this.ruleBook.equationTable());
    }

    @Override
    protected Optional<GameState> firstPlayerRequest() {
        Optional<GameState> result = super.firstPlayerRequest();
        result.ifPresent(this::notifyListeners);
        return result;
    }
    @Override
    protected Optional<GameState> secondPlayerRequest(GameState stateAfterExchanges) {
        Optional<GameState> result = super.secondPlayerRequest(stateAfterExchanges);
        result.ifPresent(this::notifyListeners);
        return result;
    }

    @Override
    protected GameState ejectActiveAndAdvanceTurn(GameState stateToAdvance) {
        GameState result = super.ejectActiveAndAdvanceTurn(stateToAdvance);
        notifyListeners(result);
        return result;
    }

    @Override
    protected GameState advancePlayers(GameState stateToAdvance) {
        GameState result = super.advancePlayers(stateToAdvance);
        notifyListeners(result);
        return result;
    }

    @Override
    protected List<IPlayer> notifyWinners(List<IPlayer> winners) {
        notifyListeners(theOneTrueState);
        shutDownListeners();
        return super.notifyWinners(winners);
    }

    protected void notifyListeners(GameState gs) {
        for (Observer listener : observers) {
            listener.notifyOfGameStateUpdate(gs);
        }
    }

    protected void setupListeners(EquationTable equationTable) {
        for (Observer listener : observers) {
            listener.setup(equationTable, theOneTrueState);
        }
    }

    protected void shutDownListeners() {
        for (Observer listener : observers) {
            listener.shutDown();
        }
    }


}
