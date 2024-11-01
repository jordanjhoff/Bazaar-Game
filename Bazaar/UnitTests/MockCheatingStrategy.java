package UnitTests;

import Common.*;
import Player.IStrategy;

import java.util.List;

public class MockCheatingStrategy implements IStrategy {

    @Override
    public Turn getBestTurnCandidate(TurnState turnState) {
        ExchangeRule gimmeEveryThingRule1 = new ExchangeRule(new PebbleCollection(Pebble.RED), new PebbleCollection(Pebble.BLUE, Pebble.BLUE, Pebble.BLUE, Pebble.BLUE));
        ExchangeRule gimmeEveryThingRule2 = new ExchangeRule(new PebbleCollection(Pebble.RED), new PebbleCollection(Pebble.GREEN, Pebble.GREEN, Pebble.GREEN, Pebble.GREEN));
        return new Turn(turnState,
                new PebbleExchangeSequence(List.of(gimmeEveryThingRule1, gimmeEveryThingRule2)),
                        new CardPurchaseSequence(turnState.visibleCards()));
    }

    @Override
    public CardPurchaseSequence getBestPurchase(TurnState turnState) {
        return new CardPurchaseSequence(turnState.visibleCards());
    }

    @Override
    public void setRuleBook(RuleBook ruleBook) {
        //this strategy is a dirty little cheater
        //it doesn't care about the rules
    }
}
