package UnitTests;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.junit.Assert;
import org.junit.Test;

import Common.Card;
import Common.Equation;
import Common.ExchangeRule;
import Common.Pebble;
import Common.PebbleCollection;
import Common.converters.BadJsonException;
import Common.converters.JSONDeserializer;

public class jsonDeserializerTests {

  @Test
  public void pebbleCollectionFromJSON() throws BadJsonException {
    PebbleCollection wallet = new PebbleCollection(Pebble.BLUE, Pebble.RED, Pebble.RED);
    String json = "[\"blue\", \"red\", \"red\"]";
    JsonElement element = JsonParser.parseString(json);
    Assert.assertEquals(wallet, JSONDeserializer.pebbleCollectionFromJson(element));

    // order doesn't matter
    json = "[\"red\", \"blue\", \"red\"]";
    JsonElement element2 = JsonParser.parseString(json);
    Assert.assertEquals(wallet, JSONDeserializer.pebbleCollectionFromJson(element2));

    // empty wallet
    json = "[]";
    JsonElement element3 = JsonParser.parseString(json);
    wallet = new PebbleCollection();
    Assert.assertEquals(wallet, JSONDeserializer.pebbleCollectionFromJson(element3));

    // non-accepted colors
    String badjson = "[\"red\", \"blue\", \"orange\"]";
    element = JsonParser.parseString(badjson);
    JsonElement bad = element;
    Assert.assertThrows(BadJsonException.class, () -> JSONDeserializer.equationFromJSON(bad));

    // non-primitives
    badjson = "[\"red\", \"blue\", [\"red\"]]";
    element = JsonParser.parseString(badjson);
    JsonElement bad2 = element;
    Assert.assertThrows(BadJsonException.class, () -> JSONDeserializer.equationFromJSON(bad2));

    // non-colors
    badjson = "[\"red\", \"blue\", False]";
    element = JsonParser.parseString(badjson);
    JsonElement bad3 = element;
    Assert.assertThrows(BadJsonException.class, () -> JSONDeserializer.equationFromJSON(bad3));

  }

  @Test
  public void equationFromJSON() throws BadJsonException {
    Equation e = new Equation(new ExchangeRule(new PebbleCollection(Pebble.RED),
            new PebbleCollection(Pebble.BLUE)));
    String json = "[[\"red\"],[\"blue\"]]";
    JsonElement element = JsonParser.parseString(json);
    Assert.assertEquals(e, JSONDeserializer.equationFromJSON(element));

    // non color
    String badjson = "[[\"red\"],[\"giraffe\"]]";
    element = JsonParser.parseString(badjson);
    JsonElement bad = element;
    Assert.assertThrows(BadJsonException.class, () -> JSONDeserializer.equationFromJSON(bad));

    // non array
    badjson = "[[\"red\"],False]";
    element = JsonParser.parseString(badjson);
    JsonElement bad2 = element;
    Assert.assertThrows(BadJsonException.class, () -> JSONDeserializer.equationFromJSON(bad2));

    // bad structure
    badjson = "[[\"red\"],[[\"red\"],[\"blue\"]]]";
    element = JsonParser.parseString(badjson);
    JsonElement bad3 = element;
    Assert.assertThrows(BadJsonException.class, () -> JSONDeserializer.equationFromJSON(bad3));
  }

  @Test
  public void ruleFromJSON() throws BadJsonException {
    ExchangeRule r = new ExchangeRule(new PebbleCollection(Pebble.RED),
            new PebbleCollection(Pebble.BLUE));
    String json = "[[\"red\"],[\"blue\"]]";
    JsonElement element = JsonParser.parseString(json);
    Assert.assertEquals(r, JSONDeserializer.ruleFromJson(element));

    // one way rules are not allowed
    json = "[[\"red\"],[]]";
    JsonElement element2 = JsonParser.parseString(json);
    Assert.assertThrows(IllegalArgumentException.class,
            () -> new ExchangeRule(new PebbleCollection(Pebble.RED), new PebbleCollection()));
    Assert.assertThrows(IllegalArgumentException.class,
            () -> JSONDeserializer.ruleFromJson(element2));
  }

  @Test
  public void cardFromJSON() throws BadJsonException {
    Card c = new Card(new PebbleCollection(Pebble.RED, Pebble.RED, Pebble.WHITE, Pebble.BLUE,
            Pebble.GREEN), true);
    String json = "{\"pebbles\":[\"red\",\"red\",\"white\",\"blue\",\"green\"], \"face?\":true}";
    JsonElement element = JsonParser.parseString(json);
    Assert.assertEquals(c, JSONDeserializer.cardFromJson(element));

    // long list of pebbles
    json = "{\"pebbles\":[\"red\",\"red\",\"white\",\"blue\",\"green\",\"yellow\"],\"face?\":true}";
    JsonElement element2 = JsonParser.parseString(json);
    Assert.assertThrows(IllegalArgumentException.class,
            () -> JSONDeserializer.cardFromJson(element2));

    // no face key
    json = "{\"pebbles\":[\"red\",\"red\",\"white\",\"blue\",\"green\"]}";
    JsonElement element3 = JsonParser.parseString(json);
    Assert.assertThrows(IllegalArgumentException.class,
            () -> JSONDeserializer.cardFromJson(element3));

    // bad face value
    json = "{\"pebbles\":[\"red\",\"red\",\"white\",\"blue\",\"green\"], \"face?\":\"red\"}";
    JsonElement element4 = JsonParser.parseString(json);
    Assert.assertThrows(IllegalArgumentException.class,
            () -> JSONDeserializer.cardFromJson(element4));

    // empty json
    json = "{}";
    JsonElement element5 = JsonParser.parseString(json);
    Assert.assertThrows(BadJsonException.class,
            () -> JSONDeserializer.cardFromJson(element5));
  }
}
