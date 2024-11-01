package Player;

import Common.*;
import Player.comparators.ITurnComparator;

import java.util.*;

/**
 * A class that generates turn candidates.
 */
public class Strategy implements IStrategy {
    private RuleBook rulebook;
    public static final int DEPTH = 4;
    private final ITurnComparator candidateComparator;

    public Strategy(ITurnComparator candidateComparator) {
      this.candidateComparator = Objects.requireNonNull(candidateComparator);
    }

    @Override
    public void setRuleBook(RuleBook rulebook) {
        this.rulebook = rulebook;
        this.candidateComparator.setRuleBook(rulebook);
    }

    /**
     * Returns the best candidate using the given Comparator<TurnCandidate> upon construction
     * @return a single best turnCandidate
     */
    public Turn getBestTurnCandidate(TurnState turnState) {
        Set<ExchangeRule> usableRules = rulebook.equationTable().usableRulesForPlayer(turnState.getPlayerWallet(), turnState.bank());
        if (usableRules.isEmpty()) {
            return new Turn(turnState, new PebbleDrawRequest(), getBestPurchase(turnState));
        }
        return Collections.min(generateAllTurnCandidates(turnState), candidateComparator);
    }

    /**
     * Returns the best purchase using the given Comparator of Turns upon construction
     * @return a single best turnCandidate
     */
    public CardPurchaseSequence getBestPurchase(TurnState turnState) {
        List<Turn> withoutExchange = getAllCandidatesFromSequence(turnState, new PebbleExchangeSequence(new ArrayList<>()));
        Turn bestCandidateWithoutExchanges = Collections.min(withoutExchange, candidateComparator);
        return bestCandidateWithoutExchanges.cardPurchases();
    }

    /**
     * Return all the turn candidates, calling getAllCandidatesFromSequence
     * @return The list of all possible Turns
     */
    private List<Turn> generateAllTurnCandidates(TurnState turnState) {
        List<PebbleExchangeSequence> allExchanges = generateAllExchanges(turnState);
        List<Turn> candidates = new ArrayList<>();
        for (PebbleExchangeSequence exchange : allExchanges) {
            candidates.addAll(getAllCandidatesFromSequence(turnState, exchange));
        }
        return candidates;
    }

    /**
     * A helper base case function that calls a recursive function gets generates all the possible PebbleExchangeSequence
     *
     * @return all the possible exchanges, with length up to depth
     */
    private List<PebbleExchangeSequence> generateAllExchanges(TurnState turnState) {
        List<PebbleExchangeSequence> firstLevel = generateFirstLevelSequences(turnState);
        return generateExchanges(turnState, firstLevel, Strategy.DEPTH -1);
    }

    /**
     * Generates the first level of exchange sequences using a single Rule
     * @return The first level list of PebbleExchangeSequences
     */
    private List<PebbleExchangeSequence> generateFirstLevelSequences(TurnState turnState) {
        PebbleCollection playerWallet = turnState.getPlayerWallet();
        PebbleCollection bank = turnState.bank();
        Set<ExchangeRule> validRules = getEquationTable().usableRulesForPlayer(playerWallet, bank);
        List<PebbleExchangeSequence> sequences = new ArrayList<>();
        sequences.add(new PebbleExchangeSequence(new ArrayList<>()));
        for (ExchangeRule rule : validRules) {
            sequences.add(new PebbleExchangeSequence(new ArrayList<>(List.of(rule))));
        }
        return sequences;
    }

    /**
     * Recursive method that generates all exchange sequences
     * @param currentExchangeDepth The List of possible exchange sequences of length depth
     * @param depth The remaining depth for which to search
     * @return The list of possible exchange sequences for the next level of depth
     */
    private List<PebbleExchangeSequence> generateExchanges(TurnState turnState, List<PebbleExchangeSequence> currentExchangeDepth, int depth) {
        List<PebbleExchangeSequence> allSequences = new ArrayList<>(currentExchangeDepth);
        if (depth > 0) {
            for (PebbleExchangeSequence sequence : currentExchangeDepth) {
                List<PebbleExchangeSequence> nextSequences = generateNextDepthSequences(turnState, sequence);
                allSequences.addAll(generateExchanges(turnState, nextSequences, depth-1));
            }
        }
        return allSequences;
    }

    /**
     * Generates all the possible sequences one level further from the given sequence
     * @param currentSequence the sequence to begin at
     * @return all the sequences that are one longer than currentSequence
     */
    private List<PebbleExchangeSequence> generateNextDepthSequences(TurnState turnState, PebbleExchangeSequence currentSequence) {

      Optional<TurnState> potentialState = rulebook.validExchanges(turnState, currentSequence);
      TurnState currentState = potentialState.orElseThrow();
      PebbleCollection intermediateWallet = currentState.getPlayerWallet();
      PebbleCollection intermediateBank = currentState.bank();
      Set<ExchangeRule> validRules = getEquationTable().usableRulesForPlayer(intermediateWallet, intermediateBank);
      List<PebbleExchangeSequence> sequences = new ArrayList<>();
      for (ExchangeRule rule : validRules) {
          PebbleExchangeSequence newSequence = currentSequence.addExchange(rule);
          sequences.add(newSequence);
      }
      return sequences;
    }

    /**
     * Generates a list of all turn candidates from all the generated exchange sequences
     * @param exchangeSequence Generates all possible Turns if the given PebbleExchangeSequence is used
     * @return The list of possible Turns
     */
    private List<Turn> getAllCandidatesFromSequence(TurnState turnState, PebbleExchangeSequence exchangeSequence) {
        List<CardPurchaseSequence> purchases = generateAllPurchases(turnState, exchangeSequence);
        List<Turn> candidates = new ArrayList<>();
        for (CardPurchaseSequence purchaseSequence : purchases) {
            candidates.add(new Turn(turnState, exchangeSequence, purchaseSequence));
        }
        return candidates;
    }

    /**
     * Get all possible card purchase combinations assuming the given PebbleExchangeSequence is used
     * @param exchangeSequence the used PebbleExchangeSequence
     * @return the list of possible CardPurchaseExchanges
     */
    private List<CardPurchaseSequence> generateAllPurchases(TurnState turnState, PebbleExchangeSequence exchangeSequence) {
        Optional<TurnState> potentialState = rulebook.validExchanges(turnState, exchangeSequence);
        TurnState intermediate = potentialState.orElseThrow();
        List<Card> visibleCards = turnState.visibleCards();
        List<CardPurchaseSequence> allPurchases = new ArrayList<>();
        CardPurchaseSequence emptyPurchase = new CardPurchaseSequence(new ArrayList<>());
        allPurchases.add(emptyPurchase);
        for (Card card : visibleCards) {
            List<Card> otherCards = new ArrayList<>(visibleCards);
            //otherCards.remove(card);
            allPurchases.addAll(generateAllNextPurchases(emptyPurchase, card, otherCards, intermediate));
        }
        return allPurchases;
    }

    /**
     * Get all the possible following purchase sequences following a previous purchase
     * @param previousPurchases The previous purchase
     * @param cardToBuy The next card to be bought in the exchange
     * @param allVisible the remaining visible cards not in the purchase exchange so far
     * @return list of all possible CardPurchase
     */
    private List<CardPurchaseSequence> generateAllNextPurchases(CardPurchaseSequence previousPurchases, Card cardToBuy, List<Card> allVisible, TurnState intermediateState) {
        List<CardPurchaseSequence> allPurchases = new ArrayList<>();
        List<Card> visibleCards = new ArrayList<>(allVisible);
        Optional<TurnState> potentialState = rulebook.validPurchases(intermediateState, previousPurchases);
        TurnState stateAfterPreviousExchange = potentialState.orElseThrow();
        PebbleCollection walletBeforeBuy = stateAfterPreviousExchange.getPlayerWallet();
        if (cardToBuy.canBeAcquiredBy(walletBeforeBuy)) {
            CardPurchaseSequence exchangeMade = previousPurchases.addPurchase(cardToBuy);
            allPurchases.add(exchangeMade);
            visibleCards.remove(cardToBuy);
            for (Card otherCard : visibleCards) {
                allPurchases.addAll(generateAllNextPurchases(exchangeMade, otherCard, visibleCards, intermediateState));
            }
        }
        return allPurchases;
    }

    /**
     * Asserts that the strategy has properly set up the RuleBook
     */
    private void assertSetup() {
        if (Objects.isNull(this.rulebook)) {
            throw new IllegalStateException("Mechanism not given proper equation table");
        }
    }

    /**
     * Gets the EquationTable that this strategy is using
     * @return The EquationTable
     */
    private EquationTable getEquationTable() {
        assertSetup();
        return this.rulebook.equationTable();
    }
}
