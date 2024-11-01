package Player;

import Common.*;

/**
 * This interface represents the interactions a player must support with the referee
 */
public interface IPlayer {
    /**
     * Gets the player's name
     * @return The player's name
     */
    String name();

    /**
     * Provides the player with the EquationTable for the game
     * @param e The EquationTable
     */
    void setup(EquationTable e);

    /**
     * Requests the player to choose their move for the exchanges phase of their turn
     * @param turnState The TurnState for this turn
     * @return The player's choice of ExchangeRequest
     */
    ExchangeRequest requestPebbleOrTrades(TurnState turnState);

    /**
     * Requests the player to choose their move for the purchase phase of their turn
     * @param turnState The TurnState after the player's ExchangeRequest
     * @return The player's choice of CardPurchaseSequence
     */
    CardPurchaseSequence requestCards(TurnState turnState);

    /**
     * Notifies the player if they have won the game
     * @param w True if the player won, else false
     */
    void win(boolean w);
}
