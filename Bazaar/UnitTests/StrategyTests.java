package UnitTests;

import Common.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import Player.comparators.MaxCardsComparator;
import Player.comparators.MaxPointsComparator;
import Player.Strategy;
import Referee.GameObjectGenerator;

public class StrategyTests {

    Strategy turnCandidateSelector;
    TurnState turnState;
    GameObjectGenerator randomizer;
    EquationTable table;
    RuleBook rulebook;

    @Before
    public void setupRules() {
        randomizer = new GameObjectGenerator(1);
        table = randomizer.generateRandomEquationTable();
        rulebook = new RuleBook(table);

        List<Card> cards = new ArrayList<>();
        cards.add(randomizer.generateRandomCard());
        cards.add(randomizer.generateRandomCard());
        cards.add(randomizer.generateRandomCard());
        cards.add(randomizer.generateRandomCard());

        PebbleCollection fullBank = randomizer.generateFullBank(20);
        PebbleCollection wallet = new PebbleCollection(List.of(Pebble.RED, Pebble.BLUE, Pebble.GREEN));

        turnState = new TurnState(fullBank, new PlayerInformation(wallet, 0), List.of(1,2,3), cards);

    }

    /**
     * This test ensures that the MaxPointsComparator correctly chooses the optimal Turn
     */
    @Test
    public void testMaxPointsTurnCandidateSelector() {
        turnCandidateSelector = new Strategy(new MaxPointsComparator());

    }

    /**
     * This test checks that the max cards candidate selector works as expected.
     */
    @Test
    public void testMaxCardsTurnCandidateSelector() {
        turnCandidateSelector = new Strategy(new MaxCardsComparator());

    }

    //doesn't buy any cards, picks smallest exchanges
    @Test
    public void testNoExchangeNoPurchase() {
        turnState = new TurnState(randomizer.generateRandomPebbleCollection(4),
                new PlayerInformation(randomizer.generateRandomPebbleCollection(5), 0),
                List.of(0,0,0,0),
                List.of(randomizer.generateRandomCard(), randomizer.generateRandomCard()));
        turnCandidateSelector = new Strategy(new MaxCardsComparator());
        turnCandidateSelector.setRuleBook(rulebook);
        Turn bestTurn = turnCandidateSelector.getBestTurnCandidate(turnState);
        Assert.assertEquals(0, bestTurn.getExchangeList().size());
        Assert.assertEquals(0   , bestTurn.cardPurchases().cards().size());
    }

    //buys one card after exchanges
    @Test
    public void testPurchase() {
        turnState = new TurnState(randomizer.generateRandomPebbleCollection(6),
                new PlayerInformation(randomizer.generateRandomPebbleCollection(5), 0),
                List.of(0,0,0,0),
                List.of(randomizer.generateRandomCard(), randomizer.generateRandomCard()));
        turnCandidateSelector = new Strategy(new MaxCardsComparator());
        turnCandidateSelector.setRuleBook(rulebook);
        Turn bestTurn = turnCandidateSelector.getBestTurnCandidate(turnState);
        Assert.assertEquals(2, bestTurn.getExchangeList().size());
        Assert.assertEquals(1, bestTurn.cardPurchases().cards().size());
    }

    //two cards can be bought after an exchange of pebbles
    @Test
    public void testBuyTwoCards() {
        turnState = new TurnState(randomizer.generateRandomPebbleCollection(20),
                new PlayerInformation(randomizer.generateRandomPebbleCollection(5), 0),
                List.of(0,0,0,0),
                List.of(randomizer.generateRandomCard(), randomizer.generateRandomCard()));
        turnCandidateSelector = new Strategy(new MaxCardsComparator());
        turnCandidateSelector.setRuleBook(rulebook);
        Turn bestTurn = turnCandidateSelector.getBestTurnCandidate(turnState);
        Assert.assertEquals(2, bestTurn.getExchangeList().size());
        Assert.assertEquals(2, bestTurn.cardPurchases().cards().size());
    }

    @Test
    public void testEmpty() {
        randomizer.generateRandomCard();
        turnState = new TurnState(randomizer.generateRandomPebbleCollection(0),
                new PlayerInformation(randomizer.generateRandomPebbleCollection(0), 0),
                List.of(0,0,0,0),
                List.of(randomizer.generateRandomCard(), randomizer.generateRandomCard()));
        turnCandidateSelector = new Strategy(new MaxCardsComparator());
        turnCandidateSelector.setRuleBook(rulebook);
        Assert.assertTrue(turnCandidateSelector.getBestTurnCandidate(turnState).cardPurchases().cards().isEmpty());
        Assert.assertTrue(turnCandidateSelector.getBestTurnCandidate(turnState).getExchangeList().isEmpty());
    }
}
