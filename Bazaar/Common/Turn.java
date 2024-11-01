package Common;

import java.util.List;
import java.util.Objects;

/**
 * A record of a complete turn a player can take. Meant to be used by the player.
 * @param stateBeforeTurn The TurnState before the player's turn begins
 * @param exchangeRequest The exchanges to be performed during this turn
 * @param cardPurchases The cards to purchase during this turn
 */
public record Turn(TurnState stateBeforeTurn, ExchangeRequest exchangeRequest, CardPurchaseSequence cardPurchases) {
    public Turn {
        Objects.requireNonNull(stateBeforeTurn);
        Objects.requireNonNull(exchangeRequest);
        Objects.requireNonNull(cardPurchases);
    }

    /**
     * Gets the list of Rules to use during this turn
     * @return The list of Rules
     */
    public List<ExchangeRule> getExchangeList() {
        if (exchangeRequest instanceof PebbleExchangeSequence) {
            return ((PebbleExchangeSequence) exchangeRequest).rules();
        }
        else {
            return List.of();
        }
    }

    public PebbleExchangeSequence pebbleExchangeSequence() {
        if (exchangeRequest instanceof PebbleExchangeSequence) {
            return ((PebbleExchangeSequence) exchangeRequest);
        }
        else {
            return new PebbleExchangeSequence(List.of());
        }
    }

    /**
     * Gets the list of Cards to purchase during this turn
     * @return The list of Cards
     */
    public List<Card> getPurchaseList() {
        return cardPurchases.cards();
    }
}
