package UnitTests;

import Common.CardDeck;
import Common.PebbleCollection;
import Common.PlayerInformation;
import Common.RuleBook;
import Player.IPlayer;
import Player.Mechanism;
import Player.Strategy;
import Player.comparators.MaxCardsComparator;
import Player.comparators.MaxPointsComparator;
import Referee.GameObjectGenerator;
import Referee.GameState;
import Referee.GameResult;
import Referee.Referee;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class RefereeTests {

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
    GameObjectGenerator deterministicGenerator;
    RuleBook ruleBook;
    @Before
    public void setupEquations() {
        generator = new GameObjectGenerator(1);
        deterministicGenerator = new DeterministicObjectGenerator();

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
    public void runFullGameAndKicksCheater() {
        Mechanism jordy = new Mechanism("jordy", new Strategy(new MaxCardsComparator()));
        Mechanism jack = new Mechanism("jack", new Strategy(new MaxCardsComparator()));
        Mechanism matthias = new Mechanism("matthias", new Strategy(new MaxPointsComparator()));
        Mechanism benlerner = new Mechanism("benlerner", new MockCheatingStrategy());

        List<IPlayer> players = List.of(jordy, jack, benlerner, matthias);

        Referee ref = new Referee(players, ruleBook);
        GameResult result = ref.runGame();
        Assert.assertEquals("benlerner", result.kicked().getFirst().name());
    }

    @Test
    public void runFullGameAndNoCheaters() {
        Mechanism jordy = new Mechanism("jordy", new Strategy(new MaxCardsComparator()));
        Mechanism jack = new Mechanism("jack", new Strategy(new MaxCardsComparator()));
        Mechanism matthias = new Mechanism("matthias", new Strategy(new MaxPointsComparator()));
        Mechanism benlerner = new Mechanism("benlerner", new Strategy(new MaxPointsComparator()));

        List<IPlayer> players = List.of(jordy, jack, benlerner, matthias);
        List<PlayerInformation> playerInformations = List.of(new PlayerInformation(new PebbleCollection(), 0),
                new PlayerInformation(new PebbleCollection(), 0),
                new PlayerInformation(new PebbleCollection(), 0),
                new PlayerInformation(new PebbleCollection(), 0));

        GameObjectGenerator generator = new GameObjectGenerator(1);

        GameState state = new GameState(generator.generateFullBank(20), generator.generateRandomCards(20), playerInformations);
        Referee ref = new Referee(players, state, ruleBook, deterministicGenerator);
        GameResult result = ref.runGame();

        Assert.assertEquals("jack", result.winners().getFirst().name());
        Assert.assertEquals(0, result.kicked().size());
    }

    @Test
    public void testPlayerWinsWith20Points() {
        Mechanism jordy = new Mechanism("jordy", new Strategy(new MaxCardsComparator()));
        Mechanism jack = new Mechanism("jack", new Strategy(new MaxCardsComparator()));
        Mechanism matthias = new Mechanism("matthias", new Strategy(new MaxPointsComparator()));
        Mechanism benlerner = new Mechanism("benlerner", new Strategy(new MaxPointsComparator()));

        List<IPlayer> players = List.of(jordy, jack, matthias, benlerner);
        List<PlayerInformation> playerInformations = List.of(new PlayerInformation(new PebbleCollection(), 0),
                new PlayerInformation(new PebbleCollection(), 0),
                new PlayerInformation(new PebbleCollection(), 18),
                new PlayerInformation(new PebbleCollection(), 0));

        GameObjectGenerator generator = new GameObjectGenerator(1);

        GameState state = new GameState(generator.generateFullBank(20), generator.generateRandomCards(20), playerInformations);
        Referee ref = new Referee(players, state, ruleBook, deterministicGenerator);
        GameResult result = ref.runGame();

        Assert.assertEquals("matthias", result.winners().getFirst().name());
        Assert.assertEquals(0, result.kicked().size());
    }

    @Test
    public void testPlayersTieWinWith20Points() {
        Mechanism jordy = new Mechanism("jordy", new Strategy(new MaxCardsComparator()));
        Mechanism jack = new Mechanism("jack", new Strategy(new MaxCardsComparator()));
        Mechanism matthias = new Mechanism("matthias", new Strategy(new MaxPointsComparator()));
        Mechanism benlerner = new Mechanism("benlerner", new Strategy(new MaxPointsComparator()));

        List<IPlayer> players = List.of(jordy, jack, matthias, benlerner);
        List<PlayerInformation> playerInformations = List.of(new PlayerInformation(new PebbleCollection(), 0),
                new PlayerInformation(new PebbleCollection(), 0),
                new PlayerInformation(new PebbleCollection(), 20),
                new PlayerInformation(new PebbleCollection(), 20));

        GameObjectGenerator generator = new GameObjectGenerator(1);

        GameState state = new GameState(generator.generateFullBank(20), generator.generateRandomCards(20), playerInformations);
        Referee ref = new Referee(players, state, ruleBook, deterministicGenerator);
        GameResult result = ref.runGame();

        Assert.assertTrue(result.winners().stream().anyMatch(p -> p.name().equals("matthias")));
        Assert.assertTrue(result.winners().stream().anyMatch(p -> p.name().equals("benlerner")));
        Assert.assertEquals(0, result.kicked().size());
    }

    @Test
    public void testGameEndsAfterExchange() {
        Mechanism jordy = new Mechanism("jordy", new Strategy(new MaxCardsComparator()));
        Mechanism jack = new Mechanism("jack", new Strategy(new MaxCardsComparator()));

        List<IPlayer> players = List.of(jordy, jack);
        List<PlayerInformation> playerInformations = List.of(new PlayerInformation(new PebbleCollection(), 0),
                new PlayerInformation(new PebbleCollection(), 0));

        GameObjectGenerator generator = new GameObjectGenerator(1);

        //one exchange request is made, and game ends
        GameState state = new GameState(generator.generateFullBank(20), generator.generateRandomCards(5), playerInformations);
        Referee ref = new Referee(players, state, ruleBook, deterministicGenerator);
        GameResult result = ref.runGame();

        Assert.assertEquals(0, result.kicked().size());
    }

    @Test
    public void testBankEmptyGameEnds() {
        Mechanism jordy = new Mechanism("jordy", new Strategy(new MaxCardsComparator()));
        Mechanism jack = new Mechanism("jack", new Strategy(new MaxCardsComparator()));
        Mechanism matthias = new Mechanism("matthias", new Strategy(new MaxPointsComparator()));
        Mechanism benlerner = new Mechanism("benlerner", new Strategy(new MaxPointsComparator()));

        List<IPlayer> players = List.of(jordy, jack, matthias, benlerner);
        List<PlayerInformation> playerInformations = List.of(new PlayerInformation(new PebbleCollection(), 0),
                new PlayerInformation(new PebbleCollection(), 0),
                new PlayerInformation(new PebbleCollection(), 0),
                new PlayerInformation(new PebbleCollection(), 0));

        GameObjectGenerator generator = new GameObjectGenerator(1);

        GameState state = new GameState(new PebbleCollection(), generator.generateRandomCards(CardDeck.DECK_STARTING_SIZE), playerInformations);
        Referee ref = new Referee(players, state, ruleBook, deterministicGenerator);
        GameResult result = ref.runGame();

        Assert.assertEquals(4, result.winners().size());
    }

    @Test
    public void testAllCheaters() {
        Mechanism evilJordy = new Mechanism("jordy", new MockHalfCheatingStrategy());
        Mechanism evilJack = new Mechanism("jack", new MockHalfCheatingStrategy());
        Mechanism matthias = new Mechanism("matthias", new MockCheatingStrategy());
        Mechanism benlerner = new Mechanism("benlerner", new MockCheatingStrategy());

        List<IPlayer> players = List.of(evilJordy, evilJack, benlerner, matthias);

        Referee ref = new Referee(players, ruleBook);
        GameResult result = ref.runGame();
        Assert.assertEquals(0, result.winners().size());
        Assert.assertEquals(4, result.kicked().size());
    }

    @Test
    public void testCheatsAfterFirstMoveUpdatesGameState() {
        Mechanism jordy = new Mechanism("half cheater", new MockHalfCheatingStrategy());
        List<IPlayer> players = List.of(jordy);

        Referee ref = new Referee(players, ruleBook);
        GameState startingGameState = ref.getGameState();
        //the half cheater requests a pebble, then requests to buy all cards getting kicked
        GameResult result = ref.runGame();
        GameState endingGameState = ref.getGameState();
        Assert.assertNotEquals(startingGameState, endingGameState);
        Assert.assertEquals(100, startingGameState.bank().size());
        Assert.assertEquals(99, endingGameState.bank().size());
        Assert.assertEquals(1, result.kicked().size());
    }

    @Test
    public void testNoCheating() {
        Mechanism jordy = new Mechanism("jordy", new Strategy(new MaxCardsComparator()));
        Mechanism jack = new Mechanism("jack", new Strategy(new MaxCardsComparator()));
        Mechanism matthias = new Mechanism("matthias", new Strategy(new MaxPointsComparator()));
        Mechanism benlerner = new Mechanism("benlerner", new Strategy(new MaxPointsComparator()));

        List<IPlayer> players = List.of(jordy, jack, benlerner, matthias);

        Referee ref = new Referee(players, ruleBook);
        GameResult result = ref.runGame();
        Assert.assertTrue(result.kicked().isEmpty());
    }
}
