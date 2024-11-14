package Runnables.actors;

import Common.*;
import Player.IStrategy;
import Player.Mechanism;
import Referee.GameObjectGenerator;

/**
 * This IPlayer implementation cheats by trying to by a non-existent card.
 */
public class BuyUnavailableCardActor extends Mechanism {
    public BuyUnavailableCardActor(String name, IStrategy strategy) {
        super(name, strategy);
    }

    @Override
    public CardPurchaseSequence requestCards(TurnState turnState) {
        GameObjectGenerator generator = new GameObjectGenerator();
        Card randomCard;
        // todo: fix. relying on random generation is bad per 11/13 codewalk
        do {
            randomCard = generator.generateRandomCard();
        }
        while (turnState.visibleCards().contains(randomCard));
        return new CardPurchaseSequence(randomCard);
    }

}
