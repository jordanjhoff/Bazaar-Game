package UnitTests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import Common.Pebble;
import Common.PebbleCollection;
import Common.ExchangeRule;

/**
 * This class tests all public equation and equations methods.
 */
public class RuleTests {
    PebbleCollection pebbles1;
    PebbleCollection pebbles2;
    PebbleCollection pebbles3;
    PebbleCollection emptyPebbles;
    PebbleCollection bigPebbles;
    ExchangeRule rule1;
    ExchangeRule rule2;
    ExchangeRule rule1Copy;


    @Before
    public void setupRules() {
        pebbles1 = new PebbleCollection(List.of(Pebble.RED, Pebble.GREEN, Pebble.BLUE));
        pebbles2 = new PebbleCollection(List.of(Pebble.WHITE, Pebble.YELLOW));
        pebbles3 = new PebbleCollection(List.of(Pebble.RED));
        emptyPebbles = new PebbleCollection(List.of());
        bigPebbles = new PebbleCollection(List.of(Pebble.RED,
                Pebble.BLUE,
                Pebble.BLUE,
                Pebble.BLUE,
                Pebble.RED));
        rule1 = new ExchangeRule(pebbles1, pebbles2);
        rule2 = new ExchangeRule(pebbles3, pebbles2);
        rule1Copy = new ExchangeRule(pebbles1,pebbles2);
    }

    @Test
    public void testInvalidRulesCreated() {
        Assert.assertThrows(IllegalArgumentException.class, () -> new ExchangeRule(pebbles1, pebbles1));
        Assert.assertThrows(IllegalArgumentException.class, () -> new ExchangeRule(emptyPebbles, pebbles1));
        Assert.assertThrows(IllegalArgumentException.class, () -> new ExchangeRule(pebbles2, bigPebbles));
    }

    @Test
    public void testCanUseFailsNotEnough() {
        Assert.assertFalse(rule1.canBeUsed(pebbles3,pebbles2));
    }

    @Test
    public void testCanUseEnough() {
        Assert.assertTrue(rule1.canBeUsed(pebbles1,pebbles2));
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(rule1, rule1Copy);
    }
}
