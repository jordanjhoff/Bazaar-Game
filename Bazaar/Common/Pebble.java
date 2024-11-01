package Common;

import java.awt.*;

/**
 * This enum represents all the possible colors of a pebble.
 */
public enum Pebble {

  RED(Color.RED, "red"),
  WHITE(Color.WHITE, "white"),
  BLUE(Color.BLUE, "blue"),
  GREEN(Color.GREEN, "green"),
  YELLOW(Color.YELLOW, "yellow");

  // the associated Color for the enum value
  private final Color color;
  // the color of the enum value as a String
  private final String string;

  Pebble(Color color, String string) {
    this.color = color;
    this.string = string;
  }

  /**
   * Returns the associated Color of this pebble color
   * @return The associated Color
   */
  public Color getColor() {
    return  this.color;
  }

  /**
   * Returns the color of this pebble color as a String
   * @return The associated String
   */
  public String toString() {
    return this.string;
  }
}