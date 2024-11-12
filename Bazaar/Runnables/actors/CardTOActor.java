package Runnables.actors;

import Common.CardPurchaseSequence;
import Common.TurnState;
import Player.IStrategy;
import Player.Mechanism;


/**
 * This IPlayer implementation times out when the "request-cards" method is called for the nth time
 */
public class CardTOActor extends Mechanism {
    private int count;
    public CardTOActor(String name, IStrategy strategy, int n) {
        super(name, strategy);
        this.count = n;
    }

    @Override
    public CardPurchaseSequence requestCards(TurnState turnState) {
        if (count <= 1) {
            while (true) {
                //loop forever
            }
        }
        count--;
        return super.requestCards(turnState);
    }
}
