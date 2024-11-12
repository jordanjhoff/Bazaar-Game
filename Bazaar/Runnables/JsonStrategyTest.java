package Runnables;

import Common.*;
import Common.converters.BadJsonException;
import Player.Strategy;

import Player.comparators.ITurnComparator;
import com.google.gson.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import Common.converters.JSONDeserializer;
import Common.converters.JSONSerializer;

/**
 * This is a testing class for sending and receiving JSON values
 */
public class JsonStrategyTest {
  public static void main(String[] args) throws IOException, BadJsonException {
    new StrategyRunner().run(new InputStreamReader(System.in), new PrintWriter(System.out));
  }
}

class StrategyRunner implements TestRunner {

  @Override
  public void run(InputStreamReader input, Writer out) throws IOException, BadJsonException {

    JsonStreamParser p = new JsonStreamParser(input);
    // Get inputs
    JsonElement jsonEquations = p.next();
    JsonElement jsonTurnState = p.next();
    JsonElement jsonPolicy = p.next();

    // Deserialize
    EquationTable equations = JSONDeserializer.equationTableFromJSON(jsonEquations);
    TurnState turnState = JSONDeserializer.turnStateFromJson(jsonTurnState);
    ITurnComparator policy = JSONDeserializer.policyFromJson(jsonPolicy);

    // Determine best Turn
    RuleBook ruleBook = new RuleBook(equations);
    Strategy strategy = new Strategy(policy);
    strategy.setRuleBook(ruleBook);
    Turn turn = strategy.getBestTurnCandidate(turnState);
    TurnState afterExchanges = ruleBook.validExchanges(turnState, turn.pebbleExchangeSequence()).orElseThrow();
    TurnState afterPurchases = ruleBook.validPurchases(afterExchanges, turn.cardPurchases()).orElseThrow();

    // Serialize
    JsonElement jsonRules = JSONSerializer.exchangeSequenceToJson(turn.pebbleExchangeSequence());
    JsonElement jsonCards = JSONSerializer.cardListToJson(turn.cardPurchases().cards());
    int turnScore = ruleBook.getTurnScore(turn);
    JsonElement jsonWallet = JSONSerializer.pebbleCollectionToJson(afterPurchases.getPlayerWallet());

    out.write(jsonRules.toString());
    out.write(jsonCards.toString());
    out.write(turnScore + "");
    out.write(jsonWallet.toString());
    out.close();
  }
}

class StrategyTester extends MilestoneIntegrationTester {

  @Override
  void runTest(InputStreamReader testInput, StringWriter testOutput) throws IOException, BadJsonException {
    new StrategyRunner().run(testInput, testOutput);
  }

  @Override
  public List<Object> jsonResultToObjects(InputStreamReader input) throws BadJsonException {
    JsonStreamParser p = new JsonStreamParser(input);
    List<Object> objects = new ArrayList<>();
    objects.add(JSONDeserializer.exchangeSequenceFromJson(p.next()));
    objects.add(JSONDeserializer.cardListFromJson(p.next()));
    objects.add(p.next());
    objects.add(JSONDeserializer.pebbleCollectionFromJson(p.next()));
    return objects;
  }
}
