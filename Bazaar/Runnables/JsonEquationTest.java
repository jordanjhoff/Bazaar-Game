package Runnables;

import Common.converters.BadJsonException;
import com.google.gson.*;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import Common.EquationTable;
import Common.PebbleCollection;
import Common.ExchangeRule;
import Common.converters.JSONDeserializer;
import Common.converters.JSONSerializer;

/**
 * This is a testing class for sending and receiving JSON values
 */
public class JsonEquationTest {
  public static void main(String[] args) throws BadJsonException {
    JsonStreamParser p = new JsonStreamParser(new InputStreamReader(System.in, StandardCharsets.UTF_8));

    JsonElement equationsArray = p.next();
    EquationTable equations = JSONDeserializer.equationTableFromJSON(equationsArray);

    JsonElement walletArray = p.next();
    PebbleCollection wallet = JSONDeserializer.pebbleCollectionFromJson(walletArray);

    JsonElement bankArray = p.next();
    PebbleCollection bank = JSONDeserializer.pebbleCollectionFromJson(bankArray);

    Set<ExchangeRule> usableRules = equations.usableRulesForPlayer(wallet, bank);

    JsonElement usableRulesArray = JSONSerializer.rulesSetToJson(usableRules);

    System.out.println(usableRulesArray);
  }
}