package Player;

import Common.*;

/**
 * This class represents a Player implementation which uses a Strategy to choose moves
 */
public class Mechanism implements IPlayer {
    private final String name;
    private final IStrategy strategy;

    public Mechanism(String name, IStrategy strategy) {
        this.name = name;
        this.strategy = strategy;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public void setup(EquationTable e) {
        this.strategy.setRuleBook(new RuleBook(e));
    }

    @Override
    public ExchangeRequest requestPebbleOrTrades(TurnState turnState) {
        Turn bestTurn = strategy.getBestTurnCandidate(turnState);
        return bestTurn.exchangeRequest();
    }

    @Override
    public CardPurchaseSequence requestCards(TurnState turnState) {
        return strategy.getBestPurchase(turnState);
    }

    @Override
    public void win(boolean w) {}
}
