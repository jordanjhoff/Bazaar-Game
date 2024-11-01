package Player.comparators;

import Common.RuleBook;
import Common.Turn;

import java.util.Comparator;
import java.util.Objects;

/**
 * A comparator to compare two Bazaar turn candidates. Used by turnCandidateSelector
 */
public class MaxPointsComparator implements ITurnComparator {
    private RuleBook rulebook;

    /**
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative number if o1 is smaller (better) than o2
     */
    @Override
    public int compare(Turn o1, Turn o2) {
        assertSetRuleBook();
        //negative of o1 score is better (larger) than o2 score
        int result = Integer.compare(rulebook.getTurnScore(o2),rulebook.getTurnScore(o1));
        if (result == 0) {
            result = new TiebreakComparator(rulebook).compare(o1, o2);
        }
        return result;
    }

    /**
     * Asserts that the RuleBook has been set for this comparator
     */
    private void assertSetRuleBook() {
        if (Objects.isNull(rulebook)) {
            throw new IllegalStateException("Rulebook is not initialized");
        }
    }
    @Override
    public void setRuleBook(RuleBook ruleBook) {
        this.rulebook = ruleBook;
    }
}
