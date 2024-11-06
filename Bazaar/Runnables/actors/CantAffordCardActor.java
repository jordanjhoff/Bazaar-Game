package Runnables.actors;

import Common.Card;
import Common.CardPurchaseSequence;
import Common.TurnState;
import Player.IStrategy;
import Player.Mechanism;
import Referee.GameObjectGenerator;

public class CantAffordCardActor extends Mechanism {

    public CantAffordCardActor(String name, IStrategy strategy) {
        super(name, strategy);
    }

    @Override
    public CardPurchaseSequence requestCards(TurnState turnState) {
        for (Card card : turnState.visibleCards()) {
            if (!card.canBeAcquiredBy(turnState.getPlayerWallet())) {
                return new CardPurchaseSequence(card);
            }
        }
        return super.requestCards(turnState);
    }
}
