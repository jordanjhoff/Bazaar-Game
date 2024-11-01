package UnitTests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Common.EquationTable;
import Common.Equation;
import Common.Pebble;
import Common.PebbleCollection;
import Common.ExchangeRule;

/**
 * This class tests all public equation and equations methods.
 */
public class EquationsTests {
  EquationTable equations;
  PebbleCollection pebbles1;
  PebbleCollection pebbles2;
  PebbleCollection pebbles3;
  ExchangeRule rule1;
  ExchangeRule rule2;
  Equation equation1;
  Equation equation2;

  @Before
  public void setupEquations() {
    pebbles1 = new PebbleCollection(List.of(Pebble.RED, Pebble.GREEN, Pebble.BLUE));
    pebbles2 = new PebbleCollection(List.of(Pebble.WHITE, Pebble.YELLOW));
    pebbles3 = new PebbleCollection(List.of(Pebble.RED));
    rule1 = new ExchangeRule(pebbles1, pebbles2);
    rule2 = new ExchangeRule(pebbles2, pebbles3);
    equation1 = new Equation(rule1);
    equation2 = new Equation(rule2);
  }

  @Test
  public void testContainsRule() {
    Assert.assertTrue(equation1.containsRule(rule1));
    Assert.assertTrue(equation1.containsRule(new ExchangeRule(rule1.getOutputPebbles(), rule1.getInputPebbles())));
    Assert.assertTrue(equation2.containsRule(rule2));
    Assert.assertTrue(equation2.containsRule(new ExchangeRule(rule2.getOutputPebbles(), rule2.getInputPebbles())));
    Assert.assertFalse(equation1.containsRule(rule2));
    Assert.assertFalse(equation2.containsRule(rule1));
  }

  @Test
  public void testCanPlayerUseEquation() {
    Assert.assertTrue(equation1.canPlayerUseEquation(pebbles1, pebbles2));
    Assert.assertTrue(equation2.canPlayerUseEquation(pebbles2, pebbles3));
    Assert.assertFalse(equation1.canPlayerUseEquation(pebbles1, pebbles3));
    Assert.assertFalse(equation1.canPlayerUseEquation(pebbles3,pebbles2));
  }

  @Test
  public void testGetRules() {
    Set<ExchangeRule> rules = new HashSet<>();
    rules.add(rule1);
    rules.add(new ExchangeRule(rule1.getOutputPebbles(), rule1.getInputPebbles()));
    Assert.assertEquals(rules, equation1.getRules());
  }

  @Test
  public void testNoEquationsUsable() {
  }
}
