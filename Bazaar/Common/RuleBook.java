package Common;

import Referee.GameObjectGenerator;
import Referee.GameState;
import Server.Player;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Function;

/**
 * A class that manages execution of rules for a single game of Bazaar.
 * This class throws exceptions when the attempted modifications on Bazaar objects in a game are invalid.
 */
public record RuleBook(EquationTable equationTable, Function<PlayerInformation, PlayerInformation> bonusFunction) {

  public RuleBook {
    Objects.requireNonNull(equationTable);
    Objects.requireNonNull(bonusFunction);
  }

  public RuleBook(EquationTable equationTable) {
    this(equationTable, Function.identity());
  }

  public static final int WINNING_POINTS = 20;

  /**
   * Determines if the game is over
   *
   * @return True if game is over, else false
   */
  public boolean isGameOver(GameState gameState) {
    boolean bankEmptyNoPlayersCanBuy = (gameState.bank().isEmpty() && !canPlayersBuyCards(gameState));
    boolean noCards = gameState.cards().isEmpty();
    List<PlayerInformation> players = gameState.players();
    return players.isEmpty() || this.playerWinWithPoints(players) || noCards || bankEmptyNoPlayersCanBuy;
  }


  /**
   * Determines if a draw request is legal given a turnstate
   * @param turnState a turnstate
   * @return true if a draw request is legal, or else false
   */
  public boolean validDrawRequest(TurnState turnState) {
    return !turnState.bank().isEmpty();
  }

  /**
   * Validates the exchanges on the given TurnState by executing them. It executes all the exchanges in sequential order,
   * and if it is valid, returns the updated TurnState.
   *
   * @param before         The previous TurnState
   * @param rulesToExecute The exchange sequence to perform
   * @return The new TurnState if exchange is valid, or empty if it is not
   */
  public Optional<TurnState> validExchanges(TurnState before, PebbleExchangeSequence rulesToExecute) {
    try {
      if (!equationTable.containsRules(rulesToExecute.rules())) {
        throw new IllegalArgumentException("Tried to use invalid exchange rules.");
      }
      PebbleCollection bank = executeExchangesOnBank(before.bank(), rulesToExecute);
      PlayerInformation activePlayer = executeExchangesOnPlayer(before.activePlayer(), rulesToExecute);
      return Optional.of(new TurnState(bank, activePlayer, before.scores(), before.visibleCards()));
    }
    catch (Exception e) {
      return Optional.empty();
    }
  }

  /**
   * Validates the purchases on the given TurnState by executing them. It purchases all the cards in sequential order,
   * and if it is valid, returns the updated TurnState.
   *
   * @param before     The previous TurnState
   * @param cardsToBuy The cards to purchase
   * @return The new TurnState if purchase is valid, or empty if it is not
   */
  public Optional<TurnState> validPurchases(TurnState before, CardPurchaseSequence cardsToBuy) {
    try {
      PebbleCollection bank = executePurchasesOnBank(before.bank(), cardsToBuy);
      PlayerInformation activePlayer = executePurchasesOnPlayer(before.activePlayer(), cardsToBuy);
      List<Card> visibleCards = executePurchasesOnCards(before.visibleCards(), cardsToBuy);
      return Optional.of(new TurnState(bank, activePlayer, before.scores(), visibleCards));
    } catch (Exception e) {
      return Optional.empty();
    }
  }
  /**
   * Validates the exchanges on the given GameState by executing them. It executes all the exchanges in sequential order,
   * and if it is valid, returns the updated GameState.
   *
   * @param before     The previous GameState
   * @param exchanges The exchanges to use
   * @return The new GameState if exchange is valid, or empty if it is not
   */
  public Optional<GameState> validExchanges(GameState before, PebbleExchangeSequence exchanges) {
    return validExchanges(before.getTurnState(), exchanges)
            .map(turnStateAfter -> updatedGameStateWithTurnState(before, turnStateAfter))
            .map(beforeRemovedBottomCard -> new GameState(beforeRemovedBottomCard.bank(),
                    removeBottomCard(beforeRemovedBottomCard.cards()),
                    beforeRemovedBottomCard.players()));
  }

  /**
   * Validates the purchases on the given GameState by executing them. It executes all the purchases in sequential order,
   * and if it is valid, returns the updated GameState.
   *
   * @param before     The previous GameState
   * @param cardsToBuy The cards to purchase
   * @return The new GameState if the purchases are valid, or empty if it is not
   */
  public Optional<GameState> validPurchases(GameState before, CardPurchaseSequence cardsToBuy) {
    return validPurchases(before.getTurnState(), cardsToBuy)
            .map(afterExecution -> updatedGameStateWithTurnState(before, afterExecution));
  }

  private GameState updatedGameStateWithTurnState(GameState before, TurnState after) {
    List<PlayerInformation> newPlayers = new ArrayList<>(before.players());
    newPlayers.set(0, after.activePlayer());
    CardDeck newCards = new CardDeck(after.visibleCards(), before.cards().nonVisibleCards());
    return new GameState(after.bank(), newCards, newPlayers);
  }

  /**
   * Advances to the next turn
   *
   * @param before     The previous GameState
   * @return The new GameState
   */
  public GameState advanceTurn(GameState before) {
    List<PlayerInformation> newPlayers = new ArrayList<>(before.players());
    PlayerInformation turnEndPlayer = newPlayers.removeFirst();
    newPlayers.add(turnEndPlayer);
    CardDeck newCards = populateVisibleCards(before.cards());
    return new GameState(before.bank(), newCards, newPlayers);
  }

  /**
   * Kicks the active player and advances to the next turn
   *
   * @param before     The previous GameState
   * @return The new GameState
   */
  public GameState kickActivePlayerAndAdvanceTurn(GameState before) {
    List<PlayerInformation> newPlayers = new ArrayList<>(before.players());
    if (newPlayers.isEmpty()) {
      throw new IllegalStateException("There are no players.");
    }
    newPlayers.removeFirst();
    CardDeck newCards = populateVisibleCards(before.cards());
    return new GameState(before.bank(), newCards, newPlayers);
  }

  public GameState kickPlayer (GameState before, String name) {
    List<PlayerInformation> players = new ArrayList<>(before.players());
    players.removeIf(player -> player.name().orElseThrow().equals(name));
    return new GameState(before.bank(), before.cards(), players);
  }

  /**
   * Calculates the score the player would receive for the given turn
   *
   * @param turn The turn to be taken
   * @return The score for the turn
   * @throws IllegalArgumentException if the turn is not valid
   */
  public int getTurnScore(Turn turn) {
    PebbleCollection walletBeforePurchases;
    if (turn.exchangeRequest() instanceof PebbleExchangeSequence) {
      walletBeforePurchases = executeExchangesOnWallet(turn.stateBeforeTurn().getPlayerWallet(),
              (PebbleExchangeSequence) turn.exchangeRequest());
    }
    else {
      walletBeforePurchases = turn.stateBeforeTurn().getPlayerWallet();
    }
    CardPurchaseSequence cardsToBuy = turn.cardPurchases();
    return getPurchaseSequenceScore(walletBeforePurchases, cardsToBuy.cards());
  }

  /**
   * Gets the winners from the final GameState
   * @param gameState The final GameState
   * @return A list of the winners
   * @throws IllegalStateException if game is not over
   */
  public List<PlayerInformation> getWinners(GameState gameState) {
    if (!isGameOver(gameState)) {
      throw new IllegalStateException("Game not over");
    }
    if (gameState.players().isEmpty()) {
      return new ArrayList<>();
    }
    else {
      List<PlayerInformation> remaining = gameState.players().stream().map(bonusFunction).toList();
      int maxScore = remaining.stream().map(PlayerInformation::score).max(Integer::compareTo).orElse(0);
      return remaining.stream()
              .filter(player -> player.score() == maxScore)
              .toList();
    }
  }

  /**
   * Draws a pebble for the active player on the given GameState
   *
   * @param before     The previous GameState
   * @param randomizer The randomizer to draw a random pebble
   * @return The new GameState after the pebble request, or empty if it can't be performed
   */
  public Optional<GameState> executePebbleRequestOnGameState(GameState before, GameObjectGenerator randomizer) {
    try {
      Pebble color = randomizer.generateRandomPebbleColor(before.bank());
      PlayerInformation newPlayer = executePebbleRequestOnPlayer(before.getActivePlayer(), color);
      List<PlayerInformation> newPlayers = new ArrayList<>(before.players());
      newPlayers.set(0, newPlayer);
      return Optional.of(new GameState(before.bank().subtract(color), before.cards(), newPlayers));
    }
    catch (Exception e) {
      return Optional.empty();
    }
  }

  /**
   * Adds the drawn Pebble to the player's wallet
   * @param before The player before they draw the Pebble
   * @param color The Pebble drawn
   * @return The player after drawing the Pebble
   */
  private PlayerInformation executePebbleRequestOnPlayer(PlayerInformation before, Pebble color) {
    return new PlayerInformation(before.name(), before.wallet().add(color), before.score(), before.purchases());
  }

  /**
   * Returns the score of a list of cards
   * @param walletBefore the wallet before the sequence
   * @param cardsToBuy the cards bought
   * @return the integer score of a purchase sequence
   * @throws IllegalArgumentException if the sequence is invalid
   */
  private int getPurchaseSequenceScore(PebbleCollection walletBefore, List<Card> cardsToBuy) {
    List<Card> currentCards = new ArrayList<>(cardsToBuy);
    if (currentCards.isEmpty()) {
      return 0;
    }
    Card toBuy = currentCards.removeFirst();
    PebbleCollection newWallet = walletBefore.subtract(toBuy.pebbles());

    return getCardScore(toBuy, newWallet.size()) + getPurchaseSequenceScore(newWallet, currentCards);
  }

  /**
   * Calculates the score for a card given the players remaining pebbles at the end of their turn
   *
   * @param card       The card for which to calculate the score
   * @param walletSize The number of pebbles remaining in the player's wallet
   * @return The score value of the card
   */
  private int getCardScore(Card card, int walletSize) {
    return switch (walletSize) {
      case 0 -> {
        if (card.hasFace()) yield 8;
        yield 5;
      }
      case 1 -> {
        if (card.hasFace()) yield 5;
        yield 3;
      }
      case 2 -> {
        if (card.hasFace()) yield 3;
        yield 2;
      }
      default -> {
        if (card.hasFace()) yield 2;
        yield 1;
      }
    };
  }

  /**
   * Executes the exchanges on the bank and returns the updated PebbleCollection
   *
   * @param bankPreExchange The bank before exchanges
   * @param rulesToExecute  The exchanges to perform
   * @return The updated bank
   * @throws IllegalArgumentException if the exchanges are not valid exchanges
   */
  private PebbleCollection executeExchangesOnBank(PebbleCollection bankPreExchange, PebbleExchangeSequence rulesToExecute) {
    PebbleCollection intermediateBank = bankPreExchange;
    for (ExchangeRule rule : rulesToExecute.rules()) {
      intermediateBank = rule.getBankAfter(intermediateBank);
    }
    return intermediateBank;
  }

  /**
   * Executes the exchanges on the player and returns the updated PlayerInformation
   *
   * @param player         The player before exchanges
   * @param rulesToExecute The exchanges to perform
   * @return The updated player
   * @throws IllegalArgumentException if the exchanges are not valid exchanges
   */
  private PlayerInformation executeExchangesOnPlayer(PlayerInformation player, PebbleExchangeSequence rulesToExecute) {
    PebbleCollection wallet = executeExchangesOnWallet(player.wallet(), rulesToExecute);
    return new PlayerInformation(player.name(), wallet, player.score(), player.purchases());
  }

  /**
   * Executes the exchanges on the player's wallet and returns the updated PebbleCollection
   *
   * @param walletPreExchange The player's wallet before exchanges
   * @param rulesToExecute    The exchanges to perform
   * @return The updated wallet
   * @throws IllegalArgumentException if the exchanges are not valid
   */
  private PebbleCollection executeExchangesOnWallet(PebbleCollection walletPreExchange, PebbleExchangeSequence rulesToExecute) {
    PebbleCollection intermediateWallet = walletPreExchange;
    for (ExchangeRule rule : rulesToExecute.rules()) {
      intermediateWallet = rule.getWalletAfter(intermediateWallet);
    }
    return intermediateWallet;
  }

  /**
   * Executes the purchases on the bank and returns the updated PebbleCollection
   *
   * @param bankPreExchange The bank before the purchases
   * @param cardsToBuy      The cards to be bought
   * @return The updated bank
   * @throws IllegalArgumentException if the purchases are not valid
   */
  private PebbleCollection executePurchasesOnBank(PebbleCollection bankPreExchange, CardPurchaseSequence cardsToBuy) {
    return bankPreExchange.add(calculateTotalCost(cardsToBuy));
  }

  /**
   * Executes the purchases on the player and returns the updated PlayerInformation
   *
   * @param player     The player to execute the purchases on
   * @param cardsToBuy The cards to be bought
   * @return The updated player information
   * @throws IllegalArgumentException if the purchases are not valid
   */
  private PlayerInformation executePurchasesOnPlayer(PlayerInformation player, CardPurchaseSequence cardsToBuy) {
    PebbleCollection wallet = executePurchasesOnWallet(player.wallet(), cardsToBuy);
    int score = player.score() + getPurchaseSequenceScore(player.wallet(), cardsToBuy.cards());
    CardPurchaseSequence newCards = player.purchases().addPurchase(cardsToBuy.cards().toArray(new Card[0]));
    return new PlayerInformation(player.name(), wallet, score, newCards);
  }

  /**
   * Executes the purchases on the player's wallet and returns the updated PebbleCollection
   *
   * @param walletPreExchange The player's wallet before any purchases
   * @param cardsToBuy        The cards to be bought
   * @return The updated wallet
   * @throws IllegalArgumentException if the purchases are not valid
   */
  private PebbleCollection executePurchasesOnWallet(PebbleCollection walletPreExchange, CardPurchaseSequence cardsToBuy) {
    return walletPreExchange.subtract(calculateTotalCost(cardsToBuy));
  }

  /**
   * Calculates the total required pebbles to purchase all cards in the sequence
   *
   * @param cardsToBuy The cards to be bought
   * @return The total required PebbleCollection
   */
  private PebbleCollection calculateTotalCost(CardPurchaseSequence cardsToBuy) {
    PebbleCollection totalCost = new PebbleCollection(new ArrayList<>());
    for (Card card : cardsToBuy.cards()) {
      totalCost = totalCost.add(card.pebbles());
    }
    return totalCost;
  }

  /**
   * Removes the purchased cards from the list of visible cards and returns the remaining visible Cards
   *
   * @param visibleCardsPreExchanges The available visible cards at the start of the turn
   * @param cardsToBuy               The cards to be bought
   * @return The remaining list of visible Cards
   * @throws IllegalArgumentException if the purchases are not valid
   */
  private List<Card> executePurchasesOnCards(List<Card> visibleCardsPreExchanges, CardPurchaseSequence cardsToBuy) {
    List<Card> cardsAfter = new ArrayList<>(visibleCardsPreExchanges);
    for (Card card : cardsToBuy.cards()) {
      if (!cardsAfter.contains(card)) {
        throw new IllegalArgumentException("Visible cards don't contain requested purchase");
      }
      cardsAfter.remove(card);
    }
    return cardsAfter;
  }

  /**
   * Determines if any player has enough points to win
   *
   * @return True if player won, else false
   */
  private boolean playerWinWithPoints(List<PlayerInformation> players) {
    return players.stream().anyMatch(player -> player.score() >= WINNING_POINTS);
  }

  /**
   * Determines if any players can buy cards
   *
   * @return True if any can buy card, else false
   */
  private boolean canPlayersBuyCards(GameState gameState) {
    List<PlayerInformation> players = gameState.players();
    List<Card> visibleCards = gameState.cards().visibleCards();
    return players.stream().anyMatch(player -> canBuyACard(player, visibleCards));
  }

  /**
   * Can a player buy any cards from the cardDeck
   *
   * @param player The player being checked
   * @param cards  The CardDeck
   * @return True if they can buy any Card, else false
   */
  private boolean canBuyACard(PlayerInformation player, List<Card> cards) {
    PebbleCollection wallet = player.wallet();
    return cards.stream().anyMatch(card -> card.canBeAcquiredBy(wallet));
  }


  /**
   * Removes the Cards in the list from the visible cards and returns the updated deck
   * @param cards The cards to remove
   * @return The update CardDeck
   * @throws IllegalArgumentException if the purchases are not valid
   */
  private CardDeck buyCards(CardDeck before, List<Card> cards) {
    List<Card> newDeck = new ArrayList<>(before.visibleCards());
    for (Card card : cards) {
      if (newDeck.contains(card)) {
        newDeck.remove(card);
      }
      else {
        throw new IllegalArgumentException("Card does not belong to this Deck");
      }
    }
    return new CardDeck(newDeck, before.nonVisibleCards());
  }

  /**
   * Refills the visible cards
   * @return The new CardDeck with replenished visible cards
   */
  private CardDeck populateVisibleCards(CardDeck before) {
    List<Card> newDeck = new ArrayList<>(before.nonVisibleCards());
    List<Card> newVisible = new ArrayList<>(before.visibleCards());
    while (newVisible.size() < CardDeck.MAX_VISIBLE_CARDS && !newDeck.isEmpty()) {
      newVisible.add(newDeck.removeFirst());
    }
    return new CardDeck(newVisible, newDeck);
  }

  /**
   * Removes the bottom card from the non-visible deck, or if there are none left clears the deck
   * @return The updated CardDeck
   */
  private CardDeck removeBottomCard(CardDeck before) {
    List<Card> newCards = new ArrayList<>(before.nonVisibleCards());
    if (newCards.isEmpty()) {
      return new CardDeck(new ArrayList<>(), new ArrayList<>());
    }
    newCards.removeLast();
    return new CardDeck(before.visibleCards(), newCards);
  }
}
