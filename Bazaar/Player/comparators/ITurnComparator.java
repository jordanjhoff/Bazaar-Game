package Player.comparators;

import Common.RuleBook;
import Common.Turn;

import java.util.Comparator;

/**
 * This interface represents turn comparators use by strategies to determine best moves
 */
public interface ITurnComparator extends Comparator<Turn> {

    /**
     * Compares two turns.
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @throws IllegalStateException if the new RuleBook is not initialized
     * @return a negative integer if o1 is better than o2, a positive integer if o1 is worse than o2, or 0 if tie
     */
    @Override
    int compare(Turn o1, Turn o2);

    /**
     * Sets the RuleBook that the strategy uses.
     * This method should be called to initialize the rules for which the strategy determines
     * the best move.
     * @param ruleBook the Rule Book
     */
    void setRuleBook(RuleBook ruleBook);
}
