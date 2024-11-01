package Common;

import java.util.Objects;

/**
 * This class represents a card in Bazaar.
 *
 * @param hasFace does the card have a face?
 */
public record Card(PebbleCollection pebbles, boolean hasFace) {
  public static int NUM_PEBBLES_PER_CARD = 5;

  public Card {
    if (pebbles.size() > NUM_PEBBLES_PER_CARD) {
      throw new IllegalArgumentException("Wrong num of pebbles");
    }
    Objects.requireNonNull(pebbles);
  }

  /**
   * Determines if this card has a face
   *
   * @return True if it has a face, else false
   */
  public boolean hasFace() {
    return this.hasFace;
  }

  /**
   * Determines if this Card can be acquired by the given player's wallet
   *
   * @param playerInventory - The player's wallet
   * @return True if they can acquire this card, else false
   */
  public boolean canBeAcquiredBy(PebbleCollection playerInventory) {
    return playerInventory.contains(this.pebbles);
  }

  /**
   * Gets the pebbles for this Card
   *
   * @return The PebbleCollection
   */
  public PebbleCollection pebbles() {
    return this.pebbles;
  }
}
