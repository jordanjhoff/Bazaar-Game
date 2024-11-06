package Referee;

import Common.EquationTable;
import Common.RuleBook;
import Player.IPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ObservableReferee extends Referee {

    protected final List<Observer> listeners = new ArrayList<>();
    public ObservableReferee(List<IPlayer> players, GameState intermediateState, RuleBook ruleBook, GameObjectGenerator randomizer) {
        super(players, intermediateState, ruleBook, randomizer);
    }

    public void addListener(Observer listener) {
        this.listeners.add(listener);
    }

    @Override
    protected void notifyPlayersOfStart() {
        super.notifyPlayersOfStart();
        setupListeners(this.ruleBook.equationTable());
        notifyListeners(theOneTrueState);
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
        for (Observer listener : listeners) {
            listener.notifyOfGameStateUpdate(gs);
        }
    }

    protected void setupListeners(EquationTable equationTable) {
        for (Observer listener : listeners) {
            listener.setup(equationTable, theOneTrueState);
        }
    }

    protected void shutDownListeners() {
        for (Observer listener : listeners) {
            listener.shutDown();
        }
    }


}
