package Common;

import java.util.*;

/**
 * This class represents all the equations in Bazaar.
 */
public record EquationTable(Set<Equation> equationSet) {
  public static int NUM_EQUATION_PER_TABLE = 10;

  public EquationTable(Set<Equation> equationSet) {
    this.equationSet = Set.copyOf(Objects.requireNonNull(equationSet));
  }

  /**
   * Determines if all Rules in the list are present in this EquationTable
   * @param rules The Rules to check
   * @return True if all exist in the table, else false
   */
  public boolean containsRules(List<ExchangeRule> rules) {
    return rules.stream().allMatch(this::containsRule);
  }

  /**
   * Determines if a Rule is present in this EquationTable
   * @param rule The Rule to check
   * @return True if the Rule exists in table, else false
   */
  public boolean containsRule(ExchangeRule rule) {
    return equationSet.stream().anyMatch(equation -> equation.containsRule(rule));
  }

  /**
   * Generates a list of usable rules given a player and bank PebbleCollection
   * @param playerInventory The player's wallet
   * @param bankInventory The inventory of the bank
   * @return the set of valid rules a player can use
   */
  public Set<ExchangeRule> usableRulesForPlayer(PebbleCollection playerInventory, PebbleCollection bankInventory) {
    Set<ExchangeRule> usableRules = new HashSet<>();
    for (Equation equation : this.equationSet) {
      Set<ExchangeRule> equationRules = equation.getRules();
      for (ExchangeRule rule : equationRules) {
        if (rule.canBeUsed(playerInventory, bankInventory)) {
          usableRules.add(rule);
        }
      }
    }
    return usableRules;
  }
}