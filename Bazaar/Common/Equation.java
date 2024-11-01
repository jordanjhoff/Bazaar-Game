package Common;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * This class represents a single equation bidirectional equation in Bazaar.
 */
public class Equation {

  private final Set<ExchangeRule> rules;

  public Equation(ExchangeRule rule) {
    rules = new HashSet<>();
    rules.add(Objects.requireNonNull(rule));
    rules.add(new ExchangeRule(rule.getOutputPebbles(), rule.getInputPebbles()));
  }

  /**
   * can a player with the given inventory use this equation to exchange pebbles with the bank's
   * inventory?
   *
   * @param playerInventory - the pebbles the player who wants to make the exchange has
   * @param bankInventory - the pebbles the bank has
   * @return whether the player can use this equation or not
   */
  public boolean canPlayerUseEquation(PebbleCollection playerInventory, PebbleCollection bankInventory) {
    for (ExchangeRule rule : this.rules) {
      if (rule.canBeUsed(playerInventory, bankInventory)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if this Equation contains the given Rule
   * @param rule The Rule to check
   * @return True if the Rule is in this Equation, else False
   */
  public boolean containsRule(ExchangeRule rule) {
    return this.rules.contains(rule);
  }

  /**
   * Returns a Set of both Rules contained in this Equation
   * @return The Set of Rules
   */
  public Set<ExchangeRule> getRules() {
    return new HashSet<>(this.rules);
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof Equation otherEq) {
      return otherEq.rules.equals(this.rules);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.rules.hashCode();
  }
}