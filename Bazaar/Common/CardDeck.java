package Common;

import java.util.List;
import java.util.Objects;

/**
 * This class represents an immutable cardDeck object for bazaar. Intended use by the referee and gamestate.
 * Players should not have access to information in this object.
 */
public record CardDeck(List<Card> visibleCards, List<Card> nonVisibleCards) {
  public static final int MAX_VISIBLE_CARDS = 4;
  public static final int DECK_STARTING_SIZE = 20;
  /**
   * Constructs the cards
   */
  public CardDeck(List<Card> visibleCards, List<Card> nonVisibleCards) {
    this.visibleCards = Objects.requireNonNull(visibleCards).stream().map(Objects::requireNonNull).toList();
    this.nonVisibleCards = Objects.requireNonNull(nonVisibleCards).stream().map(Objects::requireNonNull).toList();
    boolean visibleCardCountsValid = visibleCards.size() <= MAX_VISIBLE_CARDS;
    if (!(visibleCardCountsValid)) {
      throw new IllegalArgumentException("Invalid construction");
    }
  }

  /**
   * Determines if there are no more available cards in the game
   * @return true if there are no cards, else false
   */
  public boolean isEmpty() {
    return this.visibleCards.isEmpty() && this.nonVisibleCards.isEmpty();
  }
}
