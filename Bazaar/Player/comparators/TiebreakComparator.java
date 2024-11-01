package Player.comparators;

import Common.PebbleExchangeSequence;
import Common.RuleBook;
import Common.Turn;

import java.util.Objects;

/**
 * This comparator breaks ties between two Turns
 */
public class TiebreakComparator implements ITurnComparator {
    private RuleBook rulebook;
    public TiebreakComparator(RuleBook rulebook) {
        this.rulebook = rulebook;
    }

    @Override
    public int compare(Turn o1, Turn o2) {
        assertSetRuleBook();
        ITurnComparator comparator;
        if (o1.getExchangeList().isEmpty() &&
                o2.getExchangeList().isEmpty()) {
            comparator = new CardPurchaseTiebreakComparator();

        }
        else {
           comparator = new ExchangePurchaseTiebreakComparator();
        }
        comparator.setRuleBook(rulebook);
        return comparator.compare(o1, o2);
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
