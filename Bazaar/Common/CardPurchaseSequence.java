package Common;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A record of a sequence of purchases
 */
public record CardPurchaseSequence(List<Card> cards) {

  public CardPurchaseSequence(List<Card> cards) {
    this.cards = Objects.requireNonNull(cards).stream().map(Objects::requireNonNull).toList();
  }

  /**
   * Adds a Card to this sequence and returns the new sequence
   * @param card The Card to add
   * @return The sequence with the added Card
   */
  public CardPurchaseSequence addPurchase(Card card) {
    List<Card> newCards = new ArrayList<>(this.cards);
    newCards.add(card);
    return new CardPurchaseSequence(newCards);
  }
}