package Common;

import java.util.Objects;

/**
 * A class representing single directional trade of pebbles. Input corresponds
 */
public record ExchangeRule(PebbleCollection input, PebbleCollection output) {

  public static final int MIN_PEBBLES_PER_SIDE = 1;
  public static final int MAX_PEBBLES_PER_SIDE = 4;

  public ExchangeRule {
    Objects.requireNonNull(input);
    Objects.requireNonNull(output);
    checkPebblesSize(input, output);
    checkPebblesDisjoint(input, output);
  }

  /**
   * Checks if this rule can be used with a specific wallet and bank
   * @param wallet The wallet of the player trying to use the Rule
   * @param bank The bank
   * @return True if this can be used, else false
   */
  public boolean canBeUsed(PebbleCollection wallet, PebbleCollection bank) {
    return wallet.contains(input) && bank.contains(output);
  }

  /**
   * Returns the PebbleCollection that this Rule takes as input
   * @return The input PebbleCollection
   */
  public PebbleCollection getInputPebbles() {
    return this.input;
  }

  /**
   * Returns the PebbleCollection that this Rule produces as output
   * @return The output PebbleCollection
   */
  public PebbleCollection getOutputPebbles() { return this.output; }

  /**
   * Updates the given bank based on this Rule
   * @param bank The bank before the exchange
   * @return The updated bank
   */
  public PebbleCollection getBankAfter(PebbleCollection bank) {
    PebbleCollection intermediateBank = bank.subtract(output);
    return intermediateBank.add(input);
  }

  /**
   * Updates the given player wallet based on this Rule
   * @param wallet The wallet before the exchange
   * @return The updated wallet
   */
  public PebbleCollection getWalletAfter(PebbleCollection wallet) {
    PebbleCollection intermediateWallet = wallet.subtract(input);
    return intermediateWallet.add(output);
  }

  /**
   * Ensures that the PebbleCollections being used to construct this Rule are of a valid size
   * @param input The input PebbleCollection for the Rule
   * @param output The output PebbleCollection for the Rule
   */
  private void checkPebblesSize(PebbleCollection input, PebbleCollection output) {
    int inputSize = input.size();
    int outputSize = output.size();
    if (inputSize < MIN_PEBBLES_PER_SIDE || outputSize < MIN_PEBBLES_PER_SIDE) {
      throw new IllegalArgumentException("Invalid number of pebbles");
    }
    else if (inputSize > MAX_PEBBLES_PER_SIDE || outputSize > MAX_PEBBLES_PER_SIDE) {
      throw new IllegalArgumentException("Invalid number of pebbles");
    }
  }

  /**
   * Ensures that the PebbleCollections being used to construct this Rule do not contain common colors
   * @param input The input PebbleCollection for the Rule
   * @param output The output PebbleCollection for the Rule
   */
  private void checkPebblesDisjoint(PebbleCollection input, PebbleCollection output) {
    if (!output.disjoint(input)) {
      throw new IllegalArgumentException("Sides of rule must be disjoint");
    }
  }
}
