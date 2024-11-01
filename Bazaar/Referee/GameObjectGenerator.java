package Referee;

import java.util.*;

import Common.*;

/**
* A class to generate randomized game objects.
*/
public class GameObjectGenerator {
  private final Random rng;

  public GameObjectGenerator() { rng = new Random(); }

  public GameObjectGenerator(int seed) {
    rng = new Random(seed);
  }

  /**
   * Constructs a random starting GameState with a set number of unnamed players
   * @param bankSize the number of each colored pebble in the starting bank
   * @param playerNum the number of unnamed players to add
   * @return The randomized game state
   */
  public GameState generateStartingGamestate(int playerNum, int bankSize) {
    PebbleCollection bank = this.generateFullBank(bankSize);
    CardDeck deck = this.generateRandomCards(CardDeck.DECK_STARTING_SIZE);
    List<PlayerInformation> players = this.generateUnnamedPlayers(playerNum);
    return new GameState(bank, deck, players);
  }


  /**
   * Generates a PebbleCollection with a set number of each color
   * @param size the number of each colored pebble in the bank
   * @return The full bank PebbleCollection
   */
  public PebbleCollection generateFullBank(int size) {
    PebbleCollection bank = new PebbleCollection(new ArrayList<>());
    for (int i = 0; i < size; i++) {
      bank = bank.add(new PebbleCollection(List.of(Pebble.RED, Pebble.BLUE, Pebble.GREEN, Pebble.WHITE, Pebble.YELLOW)));
    }
    return bank;
  }

  public List<PlayerInformation> generateUnnamedPlayers(int numberOfPlayers) {
    List<PlayerInformation> playerInfos = new ArrayList<>();
    for (int i = 0; i < numberOfPlayers; i++) {
      playerInfos.add(new PlayerInformation(new PebbleCollection(), 0));
    }
    return playerInfos;
  }

  /**
   * Generates a random Card
   * @return a random Card
   */
  public Card generateRandomCard() {
    PebbleCollection pebbles = generateRandomPebbleCollection(Card.NUM_PEBBLES_PER_CARD);
    return new Card(pebbles, rng.nextBoolean());
  }

  /**
   * Generates a random CardDeck
   * @return a random CardDeck
   */
  public CardDeck generateRandomCards(int deckSize) {
    List<Card> cardDeck = new ArrayList<>();
    List<Card> visibleCards = new ArrayList<>();
    for (int cardIndex = 0; cardIndex < deckSize - CardDeck.MAX_VISIBLE_CARDS; cardIndex++) {
      Card card = generateRandomCard();
      cardDeck.addFirst(card);
    }
    for (int cardIndex = 0; cardIndex < CardDeck.MAX_VISIBLE_CARDS; cardIndex++) {
      Card card = generateRandomCard();
      visibleCards.addFirst(card);
    }
    return new CardDeck(visibleCards, cardDeck);
  }

  /**
   * Generates a random ExchangeRule
   * @return a random ExchangeRule
   */
  public ExchangeRule generateRandomRule() {
    PebbleCollection pebbles1 = this.generateRandomPebbleCollection(this.choosePebblesSize());
    PebbleCollection pebbles2 = this.generateDisjointPebbles(this.choosePebblesSize(), pebbles1);
    return new ExchangeRule(pebbles1, pebbles2);
  }

  /**
  * Generates a random equation
  * @return a random equation
  */
  public Equation generateRandomEquation() {
    ExchangeRule rule = this.generateRandomRule();
    return new Equation(rule);
  }

  /**
   * Generates a random equation table
   * @return a random equation table
   */
  public EquationTable generateRandomEquationTable() {
    Set<Equation> generatedEquations = new HashSet<>();
    while (generatedEquations.size() < EquationTable.NUM_EQUATION_PER_TABLE) {
      generatedEquations.add(this.generateRandomEquation());
    }
    return new EquationTable(generatedEquations);
  }

  /**
   * Generates a random PebbleColor
   * @return a random PebbleColor
   */
  public Pebble generateRandomPebbleColor(PebbleCollection bank) {
    ensureBankNotEmpty(bank);
    Set<Pebble> colors = EnumSet.allOf(Pebble.class);
    Pebble color;
    do {
      color = generateRandomPebbleColor(colors);
      colors.remove(color);
    }
    while (!bank.contains(color));
    return color;
  }

  /**
   * Generates a random PebbleCollection with given size
   * @param size size of Pebbles
   * @return a random pebbles
   */
  public PebbleCollection generateRandomPebbleCollection(int size) {
    return new PebbleCollection(generateListOfPebbles(size, Set.of(Pebble.values())));
  }

  /**
   * Picks a random pebble from a list of available colors
   * @param colorSet The available PebbleColors
   * @return a random PebbleColor from the set
   */
  public Pebble generateRandomPebbleColor(Set<Pebble> colorSet) {
    List<Pebble> availableColors = colorSet.stream().sorted().toList();
    int randomPebble = rng.nextInt(availableColors.size());
    return availableColors.get(randomPebble);
  }

  /**
   * Generates a list of Pebbles from the given available colors and of the given size
   * @param size The number of Pebbles to choose
   * @param availableColors The colors available to choose from
   * @return The random list of Pebbles
   */
  private List<Pebble> generateListOfPebbles(int size, Set<Pebble> availableColors) {
    List<Pebble> generatedPebbles = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      generatedPebbles.add(generateRandomPebbleColor(availableColors));
    }
    return generatedPebbles;
  }

  /**
   * Generates a list of Pebbles without the given colors and of the given size
   * @param size The number of Pebbles to choose
   * @param excludePebbles The Pebbles to not include
   * @return The random list of Pebbles
   */
  private PebbleCollection generateDisjointPebbles(int size, PebbleCollection excludePebbles) {
    Set<Pebble> availableColors = new HashSet<>(Set.of(Pebble.values()));
    availableColors.removeAll(excludePebbles.getColors());
    return new PebbleCollection(generateListOfPebbles(size, availableColors));
  }

  /**
   * Chooses a random size for one side of an ExchangeRule within the appropriate range
   * @return The randomly chosen size
   */
  private int choosePebblesSize() {
    int minSize = ExchangeRule.MIN_PEBBLES_PER_SIDE;
    int maxSize = ExchangeRule.MAX_PEBBLES_PER_SIDE;
    return rng.nextInt(minSize, maxSize+1);
  }

  /**
   * Ensures that the bank is not empty when drawing a random Pebble
   * @param bank The current bank
   */
  protected void ensureBankNotEmpty(PebbleCollection bank) {
    if (bank.isEmpty()) {
      throw new IllegalStateException("No pebbles in bank to draw");
    }
  }
}
