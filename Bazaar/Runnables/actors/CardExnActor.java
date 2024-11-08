package Runnables.actors;

import Common.CardPurchaseSequence;
import Common.TurnState;
import Player.IStrategy;
import Player.Mechanism;

/**
 * This IPlayer implementation throws an exception when the "requestCards" method is called
 */
public class CardExnActor extends Mechanism {
    int count;
    public CardExnActor(String name, IStrategy strategy, int count) {
        super(name, strategy);
        this.count = count;
    }

    @Override
    public CardPurchaseSequence requestCards(TurnState turnState) {
        if (--count == 0)
            throw new IllegalStateException("Actor throwing exception on setup");
        return super.requestCards(turnState);
    }
}