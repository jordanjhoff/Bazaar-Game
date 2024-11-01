package UnitTests;

import Common.*;
import Player.IStrategy;

import java.util.ArrayList;
import java.util.List;

public class MockHalfCheatingStrategy implements IStrategy {


    @Override
    public Turn getBestTurnCandidate(TurnState turnState) {
        return new Turn(turnState,
                new PebbleDrawRequest(),
                new CardPurchaseSequence(new ArrayList<>()));
    }

    @Override
    public CardPurchaseSequence getBestPurchase(TurnState turnState) {
        //this strategy only cheats after the first move, which is legal
        return new CardPurchaseSequence(turnState.visibleCards());
    }

    @Override
    public void setRuleBook(RuleBook ruleBook) {
        //this strategy is a dirty little cheater
        //it doesn't care about the rules
    }
}
