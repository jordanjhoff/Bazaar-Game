package UnitTests;

import Common.*;
import Common.converters.BadJsonException;
import Common.converters.Bonus;
import Common.converters.JSONDeserializer;
import Referee.GameObjectGenerator;
import Referee.GameState;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class RuleBookTests {

    GameObjectGenerator generator;
    EquationTable equationTable;
    TurnState turnState;
    PlayerInformation playerInformation;
    PebbleCollection bank;
    PebbleCollection wallet;
    List<Card> visibleCards;
    RuleBook rulebook;

    @Before
    public void setUp() {
        generator = new GameObjectGenerator(1);
        wallet = new PebbleCollection(List.of(Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED));
        bank = generator.generateFullBank(20);
        equationTable = generator.generateRandomEquationTable();
        playerInformation = new PlayerInformation(wallet, 0);
        visibleCards = new ArrayList<>(List.of(
                new Card(wallet, true),
                new Card(
                        new PebbleCollection(
                                new ArrayList<>(List.of(Pebble.BLUE, Pebble.BLUE, Pebble.BLUE, Pebble.BLUE, Pebble.BLUE))),
                        true),
                new Card(wallet, false),
                new Card(
                        new PebbleCollection(
                                new ArrayList<>(List.of(Pebble.BLUE, Pebble.BLUE, Pebble.BLUE, Pebble.BLUE, Pebble.BLUE))),
                        false)
        ));
        turnState = new TurnState(bank, playerInformation, List.of(0), visibleCards);
        rulebook = new RuleBook(equationTable);
    }

    @Test
    public void testExecuteExchange() {
        ExchangeRule rule1 = new ExchangeRule(
                new PebbleCollection(new ArrayList<>(List.of(Pebble.RED))),
                new PebbleCollection(new ArrayList<>(List.of(Pebble.BLUE, Pebble.GREEN, Pebble.WHITE)))
        );
        ExchangeRule rule2 = new ExchangeRule(
                new PebbleCollection(new ArrayList<>(List.of(Pebble.RED, Pebble.WHITE))),
                new PebbleCollection(new ArrayList<>(List.of(Pebble.GREEN)))
        );
        equationTable = new EquationTable(Set.of(new Equation(rule1), new Equation(rule2)));
        rulebook = new RuleBook(equationTable);
        PebbleExchangeSequence exchanges = new PebbleExchangeSequence(List.of(rule1, rule2));
        PebbleCollection newBank = bank.add(new PebbleCollection(List.of(Pebble.RED, Pebble.RED))).subtract(new PebbleCollection(List.of(Pebble.GREEN, Pebble.GREEN, Pebble.BLUE)));
        PebbleCollection newWallet = wallet.add(new PebbleCollection(List.of(Pebble.GREEN, Pebble.GREEN, Pebble.BLUE))).subtract(new PebbleCollection(List.of(Pebble.RED, Pebble.RED)));
        TurnState expectedTurnState = new TurnState(newBank, new PlayerInformation(newWallet, playerInformation.score()), List.of(0), visibleCards);

       Optional<TurnState> actualTurnState = rulebook.validExchanges(turnState, exchanges);

       Assert.assertEquals(expectedTurnState, actualTurnState.orElse(null));
    }

    @Test
    public void testExecuteInvalidExchange() {

        ExchangeRule rule = new ExchangeRule(
                new PebbleCollection(new ArrayList<>(List.of(Pebble.RED, Pebble.BLUE, Pebble.GREEN, Pebble.GREEN))),
                new PebbleCollection(new ArrayList<>(List.of(Pebble.YELLOW)))
        );
        PebbleExchangeSequence exchangeSequence = new PebbleExchangeSequence(List.of(rule));
        Assert.assertTrue(rulebook.validExchanges(turnState, exchangeSequence).isEmpty());

        ExchangeRule nonExistentRule = new ExchangeRule(
                new PebbleCollection(new ArrayList<>(List.of(Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED))),
                new PebbleCollection(new ArrayList<>(List.of(Pebble.GREEN, Pebble.GREEN, Pebble.GREEN, Pebble.GREEN))));
        //player doesnt have enough pebbles
        turnState = new TurnState(bank, new PlayerInformation(new PebbleCollection(new ArrayList<>()),0), List.of(0), visibleCards);
        Assert.assertTrue(rulebook.validExchanges(turnState, exchangeSequence).isEmpty());

        //bank doesn't have enough pebbles
        turnState = new TurnState(new PebbleCollection(new ArrayList<>()), playerInformation, List.of(0), visibleCards);
        Assert.assertTrue(rulebook.validExchanges(turnState, exchangeSequence).isEmpty());


        //rule is not in equation table
        PebbleExchangeSequence exchangeSequence2 = new PebbleExchangeSequence(List.of(nonExistentRule));
        turnState = new TurnState(bank, playerInformation, List.of(0), visibleCards);
        Assert.assertTrue(rulebook.validExchanges(turnState, exchangeSequence2).isEmpty());
    }

    @Test
    public void testExecutePurchases() {
        Card card = new Card(wallet, false);
        CardPurchaseSequence purchases = new CardPurchaseSequence(new ArrayList<>(List.of(card)));

        Optional<TurnState> afterState = rulebook.validPurchases(turnState, purchases);
        TurnState after = afterState.orElseThrow();

        ArrayList<Card> afterCards = new ArrayList<>(visibleCards);
        afterCards.remove(card);
        Assert.assertEquals(afterCards.size(), 3);
        Assert.assertEquals(after.bank().size(), turnState.bank().size() + 5);
        Assert.assertEquals(after.getPlayerWallet().size(), turnState.getPlayerWallet().size() - 5);
    }

    @Test
    public void testExecuteInvalidPurchase() {
        //this card isn't in visibleCards
        Card card = new Card(new PebbleCollection(List.of(Pebble.BLUE, Pebble.GREEN, Pebble.BLUE, Pebble.BLUE, Pebble.BLUE)), false);
        CardPurchaseSequence purchases = new CardPurchaseSequence(new ArrayList<>(List.of(card)));
        Assert.assertTrue(rulebook.validPurchases(turnState, purchases).isEmpty());

        //player doesn't have enough pebbles to buy this card
        Card card2 = new Card(new PebbleCollection(List.of(Pebble.BLUE, Pebble.BLUE, Pebble.BLUE, Pebble.BLUE, Pebble.BLUE)), false);
        CardPurchaseSequence purchases2 = new CardPurchaseSequence(new ArrayList<>(List.of(card2)));
        Assert.assertTrue(rulebook.validPurchases(turnState, purchases2).isEmpty());
    }

    @Test
    public void testIsGameOver() {
        GameState gameState1 = new GameState(bank, new CardDeck(visibleCards, visibleCards), List.of(playerInformation));
        Assert.assertFalse(rulebook.isGameOver(gameState1));
        GameState gameState2 = new GameState(bank, new CardDeck(new ArrayList<>(), new ArrayList<>()), List.of(playerInformation));
        Assert.assertTrue(rulebook.isGameOver(gameState2));
        GameState gameState3 = new GameState(bank, new CardDeck(visibleCards, visibleCards), List.of(new PlayerInformation(wallet, 20)));
        Assert.assertTrue(rulebook.isGameOver(gameState3));
        GameState gameState4 = new GameState(bank, new CardDeck(visibleCards, visibleCards), new ArrayList<>());
        Assert.assertTrue(rulebook.isGameOver(gameState4));
        GameState gameState5 = new GameState(new PebbleCollection(new ArrayList<>()), new CardDeck(visibleCards, visibleCards), List.of(new PlayerInformation(new PebbleCollection(new ArrayList<>()),0)));
        Assert.assertTrue(rulebook.isGameOver(gameState5));
    }

    @Test
    public void testJsonUtil() throws BadJsonException {
        PebbleCollection collection = JSONDeserializer.pebbleCollectionFromJson(TestingUtils.getJsonElementString("[\"red\"]"));
        Assert.assertEquals(collection, new PebbleCollection(new ArrayList<>(List.of(Pebble.RED))));
    }


    @Test
    public void testRuleBookBonus() throws BadJsonException {
        rulebook = new RuleBook(equationTable, Bonus.RWB.getBonusFunction());
        //rrbrr + gyggw
        CardPurchaseSequence multicolor = new CardPurchaseSequence(JSONDeserializer.cardListFromJson(TestingUtils.getJsonElementString("[{\"face?\":true,\"pebbles\":[\"red\",\"red\",\"blue\",\"red\",\"red\"]},{\"face?\":false,\"pebbles\":[\"green\",\"yellow\",\"green\",\"green\",\"white\"]}]")));
        PlayerInformation boughtAll = new PlayerInformation(Optional.of("Jordy"), wallet, 20, List.of(multicolor));
        GameState gameState1 = new GameState(bank, new CardDeck(visibleCards, visibleCards), List.of(boughtAll));

        List<PlayerInformation> winners = rulebook.getWinners(gameState1);
        Assert.assertEquals(winners.getFirst().score(), 30);

        rulebook = new RuleBook(equationTable, Bonus.SEY.getBonusFunction());
        winners = rulebook.getWinners(gameState1);
        Assert.assertEquals(winners.getFirst().score(), 70);

        rulebook = new RuleBook(equationTable);
        winners = rulebook.getWinners(gameState1);
        Assert.assertEquals(winners.getFirst().score(), 20);
    }

    @Test
    public void testBonusAppliedAtEnd() throws BadJsonException {
        rulebook = new RuleBook(equationTable, Bonus.RWB.getBonusFunction());
        //rrbrr + gyggw
        CardPurchaseSequence multicolor = new CardPurchaseSequence(JSONDeserializer.cardListFromJson(TestingUtils.getJsonElementString("[{\"face?\":true,\"pebbles\":[\"red\",\"red\",\"blue\",\"red\",\"red\"]},{\"face?\":false,\"pebbles\":[\"green\",\"yellow\",\"green\",\"green\",\"white\"]}]")));
        PlayerInformation boughtAll = new PlayerInformation(Optional.of("Jordy"), wallet, 20, List.of(multicolor));
        PlayerInformation boughtNone = new PlayerInformation(Optional.of("Ben"), wallet, 20, List.of());
        GameState gameState1 = new GameState(bank, new CardDeck(visibleCards, visibleCards), List.of(boughtAll, boughtNone));

        //jordy wins with rwb
        List<PlayerInformation> winners = rulebook.getWinners(gameState1);
        Assert.assertEquals(winners.getFirst().score(), 30);
        Assert.assertEquals(winners.getFirst().name().get(), "Jordy");

        //jordy wins with sey
        rulebook = new RuleBook(equationTable, Bonus.SEY.getBonusFunction());
        winners = rulebook.getWinners(gameState1);
        Assert.assertEquals(winners.getFirst().score(), 70);
        Assert.assertEquals(winners.getFirst().name().get(), "Jordy");

        //both players win with none
        rulebook = new RuleBook(equationTable);
        winners = rulebook.getWinners(gameState1);
        Assert.assertEquals(winners.getFirst().score(), 20);
        Assert.assertEquals(winners.getFirst().name().get(), "Jordy");
        Assert.assertEquals(winners.get(1).name().get(), "Ben");
    }

}
