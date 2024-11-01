package Common.converters;

import Common.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonObject;

/**
 * A class that serializes Bazaar game objects into JSON
 */
public class JSONSerializer {

  /**
   * Converts a PebbleCollection to json
   * @param pebbles a PebbleCollection
   * @return Json
   */
  public static JsonElement pebbleCollectionToJson(PebbleCollection pebbles) {
    JsonArray pebblesArray = new JsonArray();
    List<Pebble> pebbleList = pebbles.getPebblesAsList();
    for (Pebble pebble : pebbleList) {
      pebblesArray.add(pebble.toString());
    }
    return pebblesArray;
  }

  /**
   * Converts a Rule to json
   * @param rule a Rule
   * @return Json
   */
  public static JsonElement ruleToJson(ExchangeRule rule) {
    JsonArray jsonRule = new JsonArray();
    jsonRule.add(JSONSerializer.pebbleCollectionToJson(rule.getInputPebbles()));
    jsonRule.add(JSONSerializer.pebbleCollectionToJson(rule.getOutputPebbles()));
    return jsonRule;
  }

  /**
   * Converts a Set of Rules to json
   * @param rulesSet a Set of Rules
   * @return Json
   */
  public static JsonElement rulesSetToJson(Set<ExchangeRule> rulesSet) {
    JsonArray jsonRules = new JsonArray();
    for (ExchangeRule rule : rulesSet) {
      jsonRules.add(JSONSerializer.ruleToJson(rule));
    }
    return jsonRules;
  }

  /**
   * Converts a Card to json
   * @param card the Card
   * @return json
   */
  public static JsonElement cardToJson(Card card) {
    JsonObject jsonCard = new JsonObject();
    JsonElement jsonPebbles = pebbleCollectionToJson(card.pebbles());
    jsonCard.add("pebbles", jsonPebbles);
    jsonCard.addProperty("face?", card.hasFace());
    return jsonCard;
  }

  /**
   * Converts a List of Card to json
   * @param cards the List of Card
   * @return json
   */
  public static JsonElement cardListToJson(List<Card> cards) {
    JsonArray jsonCards = new JsonArray();
    for (Card card : cards) {
      jsonCards.add(cardToJson(card));
    }
    return jsonCards;
  }

  /**
   * Converts a PlayerInformation to json
   * @param player the PlayerInformation to convert
   * @return Json
   */
  public static JsonElement playerToJson(PlayerInformation player) {
    JsonObject jsonPlayer = new JsonObject();
    JsonElement jsonWallet = pebbleCollectionToJson(player.wallet());
    jsonPlayer.add("wallet", jsonWallet);
    jsonPlayer.addProperty("score", player.score());
    return jsonPlayer;
  }

  /**
   * Converts a list of Strings to json
   * @param names the list of Strings to convert
   * @return Json
   */
  public static JsonElement namesToJson(List<String> names) {
    List<String> sortedNames = new ArrayList<>(names);
    Collections.sort(sortedNames);
    JsonArray jsonNames = new JsonArray();
    for (String name : sortedNames) {
      jsonNames.add(name);
    }
    return jsonNames;
  }

  /**
   * Converts a List of scores to json
   * @param scores a List of Integer representing player scores
   * @return Json
   */
  public static JsonElement scoresToJson(List<Integer> scores) {
    JsonArray jsonScores = new JsonArray();
    for (Integer score : scores) {
      jsonScores.add(score);
    }
    return jsonScores;
  }

  /**
   * Converts a TurnState to json
   * @param turnState a TurnState
   * @return Json
   */
  public static JsonElement turnStateToJson(TurnState turnState) {
    JsonElement jsonBank = pebbleCollectionToJson(turnState.bank());
    JsonElement jsonCards = cardListToJson(turnState.visibleCards());
    JsonElement jsonPlayer = playerToJson(turnState.activePlayer());
    JsonElement jsonScores = scoresToJson(turnState.scores());
    JsonObject jsonTurn = new JsonObject();
    jsonTurn.add("bank", jsonBank);
    jsonTurn.add("cards", jsonCards);
    jsonTurn.add("active", jsonPlayer);
    jsonTurn.add("scores", jsonScores);
    return jsonTurn;
  }

  /**
   * Converts a PebbleExchangeSequence to json
   * @param exchangeSequence the PebbleExchangeSequence to convert
   * @return Json
   */
  public static JsonElement exchangeSequenceToJson(PebbleExchangeSequence exchangeSequence) {
    JsonArray jsonRules = new JsonArray();
    List<ExchangeRule> rules = exchangeSequence.rules();
    for (ExchangeRule rule : rules) {
      jsonRules.add(ruleToJson(rule));
    }
    return jsonRules;
  }

  /**
   * Converts a CardPurchases to json
   * @param purchase the CardPurchases to convert
   * @return Json
   */
  public static JsonElement purchaseToJson(CardPurchaseSequence purchase) {
    List<Card> cards = purchase.cards();
    return cardListToJson(cards);
  }
}
