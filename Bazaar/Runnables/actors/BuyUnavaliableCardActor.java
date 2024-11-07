package Runnables.actors;

import Common.*;
import Player.IStrategy;
import Player.Mechanism;
import Referee.GameObjectGenerator;

/**
 * This IPlayer implementation cheats by trying to by a non-existent card.
 */
public class BuyUnavaliableCardActor extends Mechanism {
    public BuyUnavaliableCardActor(String name, IStrategy strategy) {
        super(name, strategy);
    }

    @Override
    public CardPurchaseSequence requestCards(TurnState turnState) {
        GameObjectGenerator generator = new GameObjectGenerator();
        Card randomCard;
        do {
            randomCard = generator.generateRandomCard();
        }
        while (turnState.visibleCards().contains(randomCard));
        return new CardPurchaseSequence(randomCard);
    }

}
