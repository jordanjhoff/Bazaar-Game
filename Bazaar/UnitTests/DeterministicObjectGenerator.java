package UnitTests;

import java.util.Arrays;
import java.util.List;

import Common.Pebble;
import Common.PebbleCollection;
import Referee.GameObjectGenerator;

/**
 * An implementation of GameObjectGenerator which draws pebbles in a deterministic order.
 * Order: Red, White, Blue, Green, Yellow
 */
public class DeterministicObjectGenerator extends GameObjectGenerator {

  @Override
  public Pebble generateRandomPebbleColor(PebbleCollection bank) {
    ensureBankNotEmpty(bank);
    List<Pebble> colors = Arrays.stream(Pebble.values()).toList();
      for (Pebble color : colors) {
          if (bank.contains(color)) {
              return color;
          }
      }
    return colors.getFirst(); //doesn't matter, since the bank has no pebbles
  }
}
