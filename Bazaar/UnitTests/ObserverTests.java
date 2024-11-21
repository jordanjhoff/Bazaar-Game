package UnitTests;

import Common.converters.BadJsonException;
import com.google.gson.JsonStreamParser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import Common.CardDeck;
import Common.PebbleCollection;
import Common.PlayerInformation;
import Common.RuleBook;
import Common.converters.JSONDeserializer;
import Player.IPlayer;
import Player.Mechanism;
import Player.Strategy;
import Player.comparators.MaxCardsComparator;
import Player.comparators.MaxPointsComparator;
import Referee.GameObjectGenerator;
import Referee.GameState;
import Referee.MockObserver;
import Referee.ObservableReferee;
import Referee.Observer;

public class ObserverTests {
  Observer ob;
  ByteArrayOutputStream baos;
  GameObjectGenerator generator;
  GameState gameState;

  @Before
  public void setup() {
    baos = new ByteArrayOutputStream();
    PrintStream p = new PrintStream(baos);
    System.setOut(p);

    ob = new MockObserver();
    generator = new GameObjectGenerator();

    CardDeck cards = generator.generateRandomCards(20);
    PebbleCollection bank = generator.generateFullBank(20);
    PebbleCollection brokePlayerWallet = new PebbleCollection(new ArrayList<>());
    PebbleCollection normalPlayerWallet = generator.generateRandomPebbleCollection(3);
    PlayerInformation richPlayer = new PlayerInformation(bank, 10);
    PlayerInformation brokePlayer = new PlayerInformation(brokePlayerWallet, 19);
    PlayerInformation normalPlayer = new PlayerInformation(normalPlayerWallet, 5);
    gameState = new GameState(bank, cards, List.of(richPlayer, brokePlayer, normalPlayer));
  }

  @Test
  public void testSetup() {
    ob.setup(generator.generateRandomEquationTable(), gameState);
    // we are expecting that only one file has been saved.
    Assert.assertTrue(baos.toString().contains("createFile_0.png"));
    // bugfix: gh issue #13
    Assert.assertFalse(baos.toString().contains("createFile_1.png"));
  }

  /**
   * Tests the functionality of the observer's game state pointer,
   * which is an internal field that keeps track of the currently rendered
   * game state.

   * The pointer should advance and retreat properly through the list of supplied game states,
   * and it should not go past the end of the list of states.
   */
  @Test
  public void testPointer() {

    ob.setup(generator.generateRandomEquationTable(), gameState);
    ob.notifyOfGameStateUpdate(gameState);
    ((MockObserver) ob).checkPointer();
    ob.moveCurrentGameStateForward();
    ((MockObserver) ob).checkPointer();
    ob.moveCurrentGameStateForward();
    ob.moveCurrentGameStateForward();
    ob.moveCurrentGameStateForward();
    ((MockObserver) ob).checkPointer();
    ob.moveCurrentGameStateBackwards();
    ((MockObserver) ob).checkPointer();
    ob.moveCurrentGameStateBackwards();
    ob.moveCurrentGameStateBackwards();
    ob.moveCurrentGameStateBackwards();
    ((MockObserver) ob).checkPointer();

    String[] output = baos.toString().split("::");
    // advances properly
    Assert.assertEquals(output[8], "pointer_0");
    Assert.assertEquals(output[9], "advance");
    Assert.assertEquals(output[10], "pointer_1");

    // can't move past the end of the list
    Assert.assertEquals(output[11], "advance");
    Assert.assertEquals(output[12], "advance");
    Assert.assertEquals(output[13], "advance");
    Assert.assertEquals(output[14], "pointer_1");

    // retreat works
    Assert.assertEquals(output[15], "retreat");
    Assert.assertEquals(output[16], "pointer_0");

    // can't move past the start of the list
    Assert.assertEquals(output[17], "retreat");
    Assert.assertEquals(output[18], "retreat");
    Assert.assertEquals(output[19], "retreat");
    Assert.assertEquals(output[20], "pointer_0");

    ob.notifyOfGameStateUpdate(gameState);
    ob.moveCurrentGameStateForward();
    ob.moveCurrentGameStateForward();
    ((MockObserver) ob).checkPointer();
    Assert.assertTrue(baos.toString().contains("pointer_2"));
  }

  /**
   * Tests that after a shutdown instruction, no more game states are accepted
   */
  @Test
  public void testShutdown() {
    ob.setup(generator.generateRandomEquationTable(), gameState);

    Assert.assertFalse(baos.toString().contains("createFile_1.png"));
    Assert.assertFalse(baos.toString().contains("createFile_2.png"));
    ob.notifyOfGameStateUpdate(gameState);
    Assert.assertTrue(baos.toString().contains("createFile_1.png"));
    Assert.assertFalse(baos.toString().contains("createFile_2.png"));
    ob.shutDown();
    // does not accept new states after shutdown
    ob.notifyOfGameStateUpdate(gameState);
    Assert.assertTrue(baos.toString().contains("createFile_1.png"));
    Assert.assertFalse(baos.toString().contains("createFile_2.png"));
  }

  /**
   * Tests that saveJson accurately records the gameState.
   *
   * @throws IOException
   */
  @Test
  public void testSaveJson() throws IOException, BadJsonException {
    ob.setup(generator.generateRandomEquationTable(), gameState);

    ob.saveGameStateJson("first.json");
    String[] output = baos.toString().split("::");
    Assert.assertEquals("fileName_first.json", output[4]);
    // make sure it contains parts of the game state
    Assert.assertTrue(output[5].contains("{\"wallet\":[],\"score\":19}"));
    Assert.assertTrue(output[5].contains("\"score\":5}"));

    // reconstruct game state from the json
    GameState New = JSONDeserializer.gameStateFromJson(new JsonStreamParser(output[5]).next());

    // really cool equality. ty records
    Assert.assertEquals(New, gameState);
  }

  @Test
  public void testObservableRefereeNotifies() {
    Mechanism jordy = new Mechanism("jordy", new Strategy(new MaxCardsComparator()));
    Mechanism jack = new Mechanism("jack", new Strategy(new MaxCardsComparator()));
    Mechanism matthias = new Mechanism("matthias", new Strategy(new MaxPointsComparator()));
    Mechanism benlerner = new Mechanism("benlerner",new Strategy(new MaxPointsComparator()));

    List<IPlayer> players = List.of(jordy, jack, benlerner, matthias);
    RuleBook ruleBook = new RuleBook(generator.generateRandomEquationTable());

    ObservableReferee ref = new ObservableReferee(players, ruleBook, ob);
    ref.runGame();

    String[] output = baos.toString().split("::");

    Assert.assertEquals("shutdown", output[output.length - 1]);
    Assert.assertTrue(baos.toString().contains("20.png"));
    System.out.println(baos.toString());
  }

  @Test
  public void testObservableRefereeMultipleObservers() throws IOException {
    Mechanism jordy = new Mechanism("jordy", new Strategy(new MaxCardsComparator()));
    Mechanism jack = new Mechanism("jack", new Strategy(new MaxCardsComparator()));
    Mechanism matthias = new Mechanism("matthias", new Strategy(new MaxPointsComparator()));
    Mechanism benlerner = new Mechanism("benlerner",new Strategy(new MaxPointsComparator()));

    List<IPlayer> players = List.of(jordy, jack, benlerner, matthias);
    RuleBook ruleBook = new RuleBook(generator.generateRandomEquationTable());
    Observer ob2 = new MockObserver();
    ObservableReferee ref = new ObservableReferee(players, ruleBook, ob, ob2);
    ref.runGame();
    ((MockObserver) ob).checkPointer();
    ((MockObserver) ob2).checkPointer();
    ob.saveGameStateJson("ob1.json");
    ob2.saveGameStateJson("ob2.json");
    String[] output = baos.toString().split("::");

    Assert.assertFalse(baos.toString().indexOf("0.png") == baos.toString().lastIndexOf("0.png"));
    Assert.assertFalse(baos.toString().indexOf("1.png") == baos.toString().lastIndexOf("1.png"));
    Assert.assertFalse(baos.toString().indexOf("shutdown") == baos.toString().lastIndexOf("shutdown"));
  }
}
