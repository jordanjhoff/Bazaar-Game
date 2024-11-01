package Runnables.actors;

import Common.CardPurchaseSequence;
import Common.TurnState;
import Player.IStrategy;
import Player.Mechanism;

/**
 * This IPlayer implementation throws an exception when the "requestCards" method is called
 */
public class CardExnActor extends Mechanism {
    public CardExnActor(String name, IStrategy strategy) {
        super(name, strategy);
    }

    @Override
    public CardPurchaseSequence requestCards(TurnState turnState) {
        throw new IllegalStateException("Actor throwing exception on setup");
    }
}