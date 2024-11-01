package Player.comparators;

import Common.*;

import java.util.*;

/**
 * This comparator will break ties between exchange and purchase requests
 */
public class ExchangePurchaseTiebreakComparator implements ITurnComparator {
    private RuleBook rulebook;

    /**
     * compares two turn candidates
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative number if o1 is smaller (better) than o2
     */
    @Override
    public int compare(Turn o1, Turn o2) {
        assertSetRuleBook();
        List<ExchangeRule> s1 = o1.getExchangeList();
        List<ExchangeRule> s2 = o2.getExchangeList();
        //#1: smallest number of trades (negative if o1 better than o2)
        int result = Integer.compare(s1.size(), s2.size());
        if (result == 0) {
            //#2: card purchase tiebreaker
            ITurnComparator comparator = new CardPurchaseTiebreakComparator();
            comparator.setRuleBook(rulebook);
            result = comparator.compare(o1, o2);
        }
        if (result == 0) {
            //#3: negative if o1 pebble exchange less than o2 pebble exchange
            result = comparePebbleExchanges(s1, s2);
        }
        return result;
    }


    @Override
    public void setRuleBook(RuleBook ruleBook) {
        this.rulebook = ruleBook;
    }

    /**
     * Asserts that the RuleBook has been set for this comparator
     */
    private void assertSetRuleBook() {
        if (Objects.isNull(rulebook)) {
            throw new IllegalStateException("Rulebook is not initialized");
        }
    }


    /**
     * Compares two lists of Rules representing a pebble exchange sequence
     * @param s1 The first sequence
     * @param s2 The second sequence
     * @return negative if s1 is better (smaller) than s2
     */
    private int comparePebbleExchanges(List<ExchangeRule> s1, List<ExchangeRule> s2) {
        int result =  s1.size() - s2.size();
        if (result == 0) {
            for (int i = 0; i < s1.size(); i++) {
                if (compareRules(s1.get(i), s2.get(i)) != 0) {
                    return compareRules(s1.get(i), s2.get(i));
                }
            }
        }
        return result;
    }

    /**
     * Pick the smallest pebble exchange
     * @param r1 The first Rule
     * @param r2 The second Rule
     * @return negative if r1 is better (smaller) than r2
     */
    private int compareRules(ExchangeRule r1, ExchangeRule r2) {
        int result = compareWallet(r1.getInputPebbles(), r2.getInputPebbles());
        if (result == 0) {
            result = compareWallet(r1.getOutputPebbles(), r2.getOutputPebbles());
        }
        return result;
    }

    /**
     * Compares two wallets when considered as strings
     * @param wallet1 The first wallet
     * @param wallet2 The second wallet
     * @return negative if wallet1 is better (smaller) than wallet2
     */
    private int compareWallet(PebbleCollection wallet1, PebbleCollection wallet2) {
        int result = wallet1.size() - wallet2.size();
        if (result == 0) {
            String wallet1str = pebbleCollectionToString(wallet1);
            String wallet2str = pebbleCollectionToString(wallet2);
            result = wallet1str.compareTo(wallet2str);
        }
        return result;
    }

    /**
     * Converts a pebble collection to a string
     * @param pebbleCollection The collection to convert
     * @return The converted string
     */
    private String pebbleCollectionToString(PebbleCollection pebbleCollection) {
        List<Pebble> pebbles = pebbleCollection.getPebblesAsList();
        List<String> pebbleChars = new ArrayList<>(pebbles.stream().map(pebbleColor -> pebbleColor.toString().substring(0, 1)).toList());
        Collections.sort(pebbleChars);
        return pebbleChars.stream().reduce("", (a, b) -> a + b);
    }
}
