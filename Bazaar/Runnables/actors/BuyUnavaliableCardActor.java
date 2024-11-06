package Runnables.actors;

import Common.*;
import Player.IStrategy;
import Player.Mechanism;
import Referee.GameObjectGenerator;

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
