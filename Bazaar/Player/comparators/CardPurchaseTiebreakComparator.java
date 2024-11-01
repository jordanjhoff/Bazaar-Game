package Player.comparators;

import Common.*;

import java.util.*;

/**
 * This comparator will break ties between card purchase requests
 */
public class CardPurchaseTiebreakComparator implements ITurnComparator {
    private RuleBook rulebook;

    /**
     * compares two turn candidates by CardPurchase tiebreak rules
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return negative if o1 is better than o2
     */
    @Override
    public int compare(Turn o1, Turn o2) {
        assertSetRuleBook();
        //#1: negative if o1 score is better (larger) than o2
        int result = Integer.compare(rulebook.getTurnScore(o2),rulebook.getTurnScore(o1));
        if (result == 0) {
            //#2: negative if o1 player wallet size is better (larger) than o2
            PebbleCollection wallet1AfterExchanges = executeTurn(o1).getPlayerWallet();
            PebbleCollection wallet2AfterExchanges = executeTurn(o2).getPlayerWallet();
            result = Integer.compare(wallet2AfterExchanges.size(), wallet1AfterExchanges.size());
        }
        if (result == 0) {
            //#3: negative if o1 smaller than o2
            result = compareWallet(candidateToWallet(o1), candidateToWallet(o2));
        }
        if (result == 0) {
            //#4: negative if o1 cardsequence is better (smaller) than o2
            result = compareCardSequences(o1.cardPurchases().cards(), o2.cardPurchases().cards());
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
     * Compares two card sequences
     * @param s1 The first sequence
     * @param s2 The second sequence
     * @return negative if s1 is better (smaller) than s2
     */
    private int compareCardSequences(List<Card> s1, List<Card> s2) {
        int result = s1.size() - s2.size();
        if (result == 0) {
            List<Card> cards1 = s1;
            List<Card> cards2 = s2;
            for(int i = 0; i < cards1.size(); ++i) {
                result = compareCards(cards1.get(i), cards2.get(i));
                if (!(result == 0)) {
                    return result;
                }
            }
        }
        return result;
    }

    /**
     * Compare two cards
     * @param c1 The first card
     * @param c2 The second card
     * @return negative if c1 is smaller
     */
    private int compareCards(Card c1, Card c2) {

        if (!c1.hasFace() && c2.hasFace()) {
            //c1 less than c2 if c2 has face
            return -1;
        }
        else if (c1.hasFace() && !c2.hasFace()) {
            //c2 smaller if c1 has face
            return 1;
        }
        return compareWallet(c1.pebbles(), c2.pebbles());
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
     * Converts a candidate Turn to a wallet
     * @param candidate The Turn
     * @return The resulting PebbleCollection
     */
    private PebbleCollection candidateToWallet(Turn candidate) {
        PebbleCollection result = new PebbleCollection(new ArrayList<>());
        for (Card purchasedCard : candidate.cardPurchases().cards()) {
            result = result.add(purchasedCard.pebbles());
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

    /**
     * Executes the given Turn
     *
     * @param turn The Turn to execute
     * @return The new TurnState if it is valid
     */
    private TurnState executeTurn(Turn turn) {
        TurnState intermediate = rulebook.validExchanges(turn.stateBeforeTurn(), turn.pebbleExchangeSequence()).orElseThrow();
        return rulebook.validPurchases(intermediate, turn.cardPurchases()).orElseThrow();
    }
}
