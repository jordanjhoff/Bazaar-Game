package Player.comparators;

import Common.Card;
import Common.RuleBook;
import Common.Turn;

import java.util.List;
import java.util.Objects;

/**
 * A comparator to compare two Bazaar turn candidates. Used by turnCandidateSelector
 */
public class MaxCardsComparator implements ITurnComparator {
    private RuleBook rulebook;

    /**
     * Strategy to get the most cards
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return negative if o1 is better than o2
     */
    @Override
    public int compare(Turn o1, Turn o2) {
        assertSetRuleBook();
        List<Card> o1PurchasedCards = o1.cardPurchases().cards();
        List<Card> o2PurchasedCards= o2.cardPurchases().cards();
        int result = Integer.compare(o2PurchasedCards.size(), o1PurchasedCards.size());
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
