package Player;

import Common.*;

/**
 * This interface represents strategies that Players use to select the best turn from a certain
 * TurnState.
 */
public interface IStrategy {

    /**
     * Returns the best Turn
     * @param turnState the state of the current players turn
     * @return the single best Turn
     */
    Turn getBestTurnCandidate(TurnState turnState);

    /**
     * Returns the best purchase
     * @param turnState the state of the current players turn
     * @return a single best CardPurchaseRequest
     */
    CardPurchaseSequence getBestPurchase(TurnState turnState);

    /**
     * Sets the RuleBook that the strategy uses.
     * This method should be called to initialize the rules for which the strategy determines
     * the best move.
     * @param ruleBook the Rule Book
     */
    void setRuleBook(RuleBook ruleBook);
}
