package Common;

import java.util.*;
import java.util.List;

/**
 * This class represents an immutable unordered collection of pebbles.
 */
public class PebbleCollection {
  // the collection of pebbles
  private final HashMap<Pebble, Integer> pebbles;

  public PebbleCollection() {
    this.pebbles = new HashMap<>();
  }

  public PebbleCollection(Pebble... pebbles) {
    this(Arrays.stream(pebbles).toList());
  }

  public PebbleCollection(List<Pebble> pebbleList) {
    Objects.requireNonNull(pebbleList);
    pebbles = new HashMap<>();
    for (Pebble p : pebbleList) {
      if (Objects.isNull(p)) {
        throw new IllegalArgumentException("No null");
      }
      pebbles.merge(p, 1, Integer::sum);
    }
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof PebbleCollection otherPebbles)) {
      return false;
    }
    boolean equal = true;
    for (Pebble color : this.pebbles.keySet()) {
      if (this.pebbles.get(color) != (otherPebbles.numOfColor(color))) {
        equal = false;
      }
    }
    return equal;
  }

  @Override
  public int hashCode() {
    return this.pebbles.hashCode();
  }

  /**
   * Checks the two Pebbles do not contain the same colors
   * @param other the other set of Pebbles
   * @return true if no overlapping colors, else false
   */
  public boolean disjoint(PebbleCollection other) {
    for (Pebble color : this.pebbles.keySet()) {
      if (other.numOfColor(color) != 0) {
        return false;
      }
    }
    return true;
  }

  /**
   * Gets the set of colors this group of pebbles contains
   * @return the colors included in this PebbleCollection as a Set
   */
  public Set<Pebble> getColors() {
    return this.pebbles.keySet();
  }

  /**
   * Determines if this PebbleCollection contains no pebbles
   * @return true if this is empty
   */
  public boolean isEmpty() {
    return this.pebbles.isEmpty();
  }


  /**
   * Removes the pebbles in another PebbleCollection from this
   * @param other The PebbleCollection to remove from this
   * @return The PebbleCollection difference
   * @throws IllegalArgumentException If this PebbleCollection doesn't have enough pebbles
   */
  public PebbleCollection subtract(PebbleCollection other) {
    if (!this.contains(other)) {
      throw new IllegalArgumentException("Not enough pebbles");
    }
    List<Pebble> newList = this.getPebblesAsList();
    for (Pebble pebble : other.getPebblesAsList()) {
      newList.remove(pebble);
    }
    return new PebbleCollection(newList);
  }

  /**
   * Removes the pebbles in another PebbleCollection from this
   * @param color The Pebble to remove from this
   * @return The new PebbleCollection
   * @throws IllegalArgumentException If this PebbleCollection doesn't have enough pebbles
   */
  public PebbleCollection subtract(Pebble color) {
    if (!this.contains(color)) {
      throw new IllegalArgumentException("Not enough pebbles");
    }
    List<Pebble> newList = this.getPebblesAsList();
    newList.remove(color);
    return new PebbleCollection(newList);
  }

  /**
   * Combines this PebbleCollection with another
   * @param other The PebbleCollection to add to this
   * @return The combined PebbleCollection
   */
  public PebbleCollection add(PebbleCollection other) {
    List<Pebble> newList = this.getPebblesAsList();
    newList.addAll(other.getPebblesAsList());
    return new PebbleCollection(newList);
  }

  /**
   * Adds the given Pebble to this collection
   * @param color The Pebble to add
   * @return The updated PebbleCollection
   */
  public PebbleCollection add(Pebble color) {
    List<Pebble> newList = this.getPebblesAsList();
    newList.add(color);
    return new PebbleCollection(newList);
  }

  /**
   * Returns how many pebbles are in this collection
   * @return number of pebbles as an integer
   */
  public int size() {
    int size = 0;
    for (Integer counts : pebbles.values()) {
      size = size + counts;
    }
    return size;
  }

  /**
   * Returns the number of pebbles of the given color
   *
   * @param color - color to count
   */
  public int numOfColor(Pebble color) {
    return this.pebbles.getOrDefault(color, 0);
  }

  /**
   * Gets the PebbleCollection as a list
   *
   * @return the list of colors of each pebble
   */
  public List<Pebble> getPebblesAsList() {
    List<Pebble> pebbles = new ArrayList<>();
    for (Pebble color : this.pebbles.keySet()) {
      for (int count = 0; count < this.pebbles.get(color); count++) {
        pebbles.add(color);
      }
    }
    return pebbles;
  }

  /**
   * Is this collection of pebbles greater than or equal to
   * the given collection?
   * Is the given collection a subset of this collection?
   *
   * @param target - the pebbles to be compared to this collection
   * @return True if the given collection is a subset of this, else false
   */
  public boolean contains(PebbleCollection target) {
    Set<Pebble> colors = new HashSet<>(this.getColors());
    colors.addAll(target.getColors());
    for (Pebble color : colors) {
      int targetCount = target.numOfColor(color);
      if(this.pebbles.getOrDefault(color,0) < targetCount) {
        return false;
      }
    }
    return true;
  }

  /**
   * Determines if this PebbleCollection contains any Pebble of the given color
   * @param color The color to be checked
   * @return True if this contains the given color, else false
   */
  public boolean contains(Pebble color) {
    return this.numOfColor(color) > 0;
  }
}
