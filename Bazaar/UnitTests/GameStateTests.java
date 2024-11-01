package UnitTests;

import Common.RuleBook;
import Referee.GameObjectGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import Common.CardDeck;
import Common.PebbleCollection;
import Referee.GameState;
import Common.PlayerInformation;

/**
 * This class tests all public equation and equations methods.
 */
public class GameStateTests {
  CardDeck cards;
  PebbleCollection bank;
  PebbleCollection richPlayerWallet;
  PebbleCollection brokePlayerWallet;
  PebbleCollection normalPlayerWallet;
  PlayerInformation richPlayer;
  PlayerInformation brokePlayer;
  PlayerInformation normalPlayer;
  GameState gameState;
  GameObjectGenerator generator;
  RuleBook ruleBook;

  @Before
  public void setupEquations() {
    generator = new GameObjectGenerator(1);

    cards = generator.generateRandomCards(20);
    bank = generator.generateFullBank(20);
    richPlayerWallet = bank;
    brokePlayerWallet = new PebbleCollection(new ArrayList<>());
    normalPlayerWallet = generator.generateRandomPebbleCollection(3);
    richPlayer = new PlayerInformation(richPlayerWallet, 10);
    brokePlayer = new PlayerInformation(brokePlayerWallet, 19);
    normalPlayer = new PlayerInformation(normalPlayerWallet, 5);
    gameState = new GameState(bank, cards, new ArrayList<>());
    ruleBook = new RuleBook(generator.generateRandomEquationTable());
  }

  @Test
  public void testGameOverNoPlayers() {
    Assert.assertTrue(ruleBook.isGameOver(gameState));
  }

  @Test
  public void testAddPlayer() {
    gameState = new GameState(bank, cards, List.of(brokePlayer, richPlayer));
    Assert.assertFalse(ruleBook.isGameOver(gameState));
  }

  @Test
  public void testGameOverConditions() {
    PlayerInformation theyWon  = new PlayerInformation(brokePlayerWallet, 20);
    gameState = new GameState(bank, cards, List.of(brokePlayer, theyWon));
    Assert.assertTrue(ruleBook.isGameOver(gameState));
  }

  @Test
  public void testGetTurnState() {
    Assert.assertThrows(IllegalStateException.class, () -> gameState.getTurnState());
  }

  @Test
  public void testPointsWinGameOver() {
    Assert.assertThrows(IllegalStateException.class, () -> ruleBook.kickActivePlayerAndAdvanceTurn(gameState));
  }
}
