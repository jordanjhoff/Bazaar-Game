package UnitTests;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import Common.CardDeck;
import Common.PebbleCollection;
import Common.PlayerInformation;
import Referee.GameObjectGenerator;
import Referee.GameState;
import Referee.MockObserver;
import Referee.Observer;

public class ObserverTests {
  @Test
  public void testSetup() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream p = new PrintStream(baos);
    System.setOut(p);

    Observer ob = new MockObserver();
    GameObjectGenerator generator = new GameObjectGenerator();

    CardDeck cards = generator.generateRandomCards(20);
    PebbleCollection bank = generator.generateFullBank(20);
    PebbleCollection brokePlayerWallet = new PebbleCollection(new ArrayList<>());
    PebbleCollection normalPlayerWallet = generator.generateRandomPebbleCollection(3);
    PlayerInformation richPlayer = new PlayerInformation("jordy",bank, 10);
    PlayerInformation brokePlayer = new PlayerInformation("benlerner", brokePlayerWallet, 19);
    PlayerInformation normalPlayer = new PlayerInformation("jack",normalPlayerWallet, 5);
    GameState gameState = new GameState(bank, cards, List.of(richPlayer, brokePlayer, normalPlayer));

    ob.setup(generator.generateRandomEquationTable(), gameState);
    // we are expecting that only one file has been saved.
    Assert.assertTrue(baos.toString().contains("createFile_0.png"));
    // bugfix: gh issue #13
    Assert.assertFalse(baos.toString().contains("createFile_1.png"));
  }
}
