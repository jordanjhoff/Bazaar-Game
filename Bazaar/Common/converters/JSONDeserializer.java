package Common.converters;

import Common.*;
import Player.IPlayer;
import Player.Mechanism;
import Player.Strategy;
import Player.comparators.ITurnComparator;
import Player.comparators.MaxCardsComparator;
import Player.comparators.MaxPointsComparator;
import Referee.GameState;
import Common.PlayerInformation;
import Runnables.actors.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;

/**
 * A class that reads json and converts it to our Bazaar game objects.
 */
public class JSONDeserializer {

  /**
   * Generates an Equation from a JsonElement
   * @param json The JsonElement to be deserialized
   * @return The generated Equation
   */
  public static Equation equationFromJSON(JsonElement json) throws BadJsonException {
    ExchangeRule rule = JSONDeserializer.ruleFromJson(json);
    return new Equation(rule);
  }

  /**
   * Generates an EquationTable from a JsonElement
   * @param json The JsonElement to be deserialized
   * @return The generated EquationTable
   */
  public static EquationTable equationTableFromJSON(JsonElement json) throws BadJsonException {
    checkJsonArray(json);
    JsonArray jsonEquations = json.getAsJsonArray();
    Set<Equation> equations = new HashSet<>();
    for (JsonElement jsonEquation : jsonEquations) {
      Equation equation = JSONDeserializer.equationFromJSON(jsonEquation.getAsJsonArray());
      equations.add(equation);
    }
    return new EquationTable(equations);
  }

  /**
   * Generates a PebbleCollection from a JsonElement
   * @param json The JsonElement to be deserialized
   * @return The generated PebbleCollection
   */
  public static PebbleCollection pebbleCollectionFromJson(JsonElement json) throws BadJsonException {
    checkJsonArray(json);
    JsonArray jsonPebbles = json.getAsJsonArray();
    List<JsonElement> colorList = new ArrayList<>();
    for (JsonElement e : jsonPebbles) {
      colorList.add(e);
    }
    List<Pebble> pebbleList = new ArrayList<>();
    for (JsonElement jsonColor : colorList) {
      Pebble pebble = pebbleFromJson(jsonColor);
      pebbleList.add(pebble);
    }
    return new PebbleCollection(pebbleList);
  }

  /**
   * Generates a Rule from a JsonElement
   * @param json The JsonElement to be deserialized
   * @return The generated Rule
   */
  public static ExchangeRule ruleFromJson(JsonElement json) throws BadJsonException {
    checkJsonArray(json);
    JsonArray jsonRule = json.getAsJsonArray();
    checkJsonArray(jsonRule.get(0));
    checkJsonArray(jsonRule.get(1));
    JsonArray leftPebblesArray = jsonRule.get(0).getAsJsonArray();
    JsonArray rightPebblesArray = jsonRule.get(1).getAsJsonArray();
    PebbleCollection inputPebbles = JSONDeserializer.pebbleCollectionFromJson(leftPebblesArray);
    PebbleCollection outputPebbles = JSONDeserializer.pebbleCollectionFromJson(rightPebblesArray);
    return new ExchangeRule(inputPebbles, outputPebbles);
  }

  /**
   * Generates a PebbleExchangeSequence from a JsonElement
   * @param json The JsonElement to be deserialized
   * @return The generated PebbleExchangeSequence
   */
  public static PebbleExchangeSequence exchangeSequenceFromJson(JsonElement json) throws BadJsonException {
    checkJsonArray(json);
    JsonArray jsonRules = json.getAsJsonArray();
    List<ExchangeRule> rules = new ArrayList<>();
    for (JsonElement jsonRule : jsonRules) {
      rules.add(ruleFromJson(jsonRule));
    }
    return new PebbleExchangeSequence(rules);
  }

  /**
   * Generates a Card from a JsonElement
   * @param json the JsonElement to be deserialized
   * @return the generated Card
   */
  public static Card cardFromJson(JsonElement json) throws BadJsonException {
    checkJsonObject(json);
    JsonObject jsonCard = json.getAsJsonObject();
    JsonElement jsonPebbles = jsonCard.get("pebbles");
    PebbleCollection pebbles = pebbleCollectionFromJson(jsonPebbles);
    JsonElement jsonFace = jsonCard.get("face?");
    if (jsonFace == null || jsonFace.isJsonNull()
            || !jsonFace.isJsonPrimitive() || !jsonFace.getAsJsonPrimitive().isBoolean()) {
      throw new IllegalArgumentException("Could not get face from json");
    }
    boolean face = jsonFace.getAsBoolean();
    return new Card(pebbles, face);
  }

  /**
   * Generates a list of Cards from a JsonElement
   * @param json the JsonElement to be deserialized
   * @return the generated list
   */
  public static List<Card> cardListFromJson(JsonElement json) throws BadJsonException {
    checkJsonArray(json);
    JsonArray jsonElements = json.getAsJsonArray();
    List<Card> cards = new ArrayList<>();
    for (JsonElement jsonCard : jsonElements) {
      cards.add(cardFromJson(jsonCard));
    }
    return cards;
  }

  /**
   * Generates a CardDeck from two JsonElements representing the visible and non-visible cards
   * @param visibleCardsJson the JsonElement of the visible cards
   * @param otherCardsJson the JsonElement of the non-visible cards
   * @return the generated CardDeck
   */
  public static CardDeck cardDeckFromJson(JsonElement visibleCardsJson, JsonElement otherCardsJson) throws BadJsonException {
    List<Card> visibleCards = cardListFromJson(visibleCardsJson);
    List<Card> otherCards = cardListFromJson(otherCardsJson);
    return new CardDeck(visibleCards, otherCards);
  }

  /**
   * Generates a PlayerInformation from a JsonElement
   * @param json the JsonElement to be deserialized
   * @return the generated PlayerInformation
   */
  public static PlayerInformation playerInformationFromJson(JsonElement json) throws BadJsonException {
    checkJsonObject(json);
    JsonObject jsonPlayer = json.getAsJsonObject();


    JsonElement jsonWallet = jsonPlayer.get("wallet");
    PebbleCollection wallet = pebbleCollectionFromJson(jsonWallet);
    JsonElement jsonScore = jsonPlayer.get("score");
    int score = jsonScore.getAsInt();
    if (jsonPlayer.has("name")) {
      String name = jsonPlayer.get("name").getAsString();
      return new PlayerInformation(name, wallet, score);
    }
    else {
      return new PlayerInformation(wallet, score);
    }
  }

  /**
   * Generates a Deque of PLayerInformation from a JsonElement
   * @param json the JsonElement to be deserialized
   * @return the generated Deque
   */
  public static List<PlayerInformation> playersFromJson(JsonElement json) throws BadJsonException {
    checkJsonArray(json);
    JsonArray jsonPlayers = json.getAsJsonArray();
    List<PlayerInformation> players = new ArrayList<>();
    for (JsonElement jsonPlayerInformation : jsonPlayers) {
      players.add(playerInformationFromJson(jsonPlayerInformation));
    }
    return players;
  }

  /**
   * Generates a GameState from a JsonElement
   * @param json the JsonElement to be deserialized
   * @return the generated GameState
   */
  public static GameState gameStateFromJson(JsonElement json) throws BadJsonException {
    checkJsonObject(json);
    JsonObject jsonGameState = json.getAsJsonObject();
    PebbleCollection bank = pebbleCollectionFromJson(jsonGameState.get("bank"));
    CardDeck cards  = cardDeckFromJson(jsonGameState.get("visibles"), jsonGameState.get("cards"));
    List<PlayerInformation> players = playersFromJson(jsonGameState.get("players"));
    return new GameState(bank, cards, players);
  }

  /**
   * Generates a list of scores as Integers from a JsonElement
   * @param json the JsonElement to be deserialized
   * @return the generated list
   */
  public static List<Integer> scoresFromJson(JsonElement json) throws BadJsonException {
    checkJsonArray(json);
    JsonArray jsonScores = json.getAsJsonArray();
    List<Integer> scores = new ArrayList<>();
    for (JsonElement jsonScore : jsonScores) {
      scores.add(jsonScore.getAsInt());
    }
    return scores;
  }

  /**
   * Generates a TurnState from a JsonElement
   * @param json the JsonElement to be deserialized
   * @return the generated TurnState
   */
  public static TurnState turnStateFromJson(JsonElement json) throws BadJsonException {
    checkJsonObject(json);
    JsonObject jsonTurnState = json.getAsJsonObject();
    PebbleCollection bank = pebbleCollectionFromJson(jsonTurnState.get("bank"));
    PlayerInformation activePlayer = playerInformationFromJson(jsonTurnState.get("active"));
    List<Integer> scores = scoresFromJson(jsonTurnState.get("scores"));
    List<Card> visibleCards = cardListFromJson(jsonTurnState.get("cards"));
    return new TurnState(bank, activePlayer, scores, visibleCards);
  }

  /**
   * Generates a Comparator of Turns from a JsonElement
   * @param jsonPolicy The Json string of the policy to use
   * @return The chosen Comparator
   */
  public static ITurnComparator policyFromJson(JsonElement jsonPolicy) throws BadJsonException {
    String stringPolicy = jsonPolicy.getAsString();
    return switch (stringPolicy) {
      case "purchase-points" -> new MaxPointsComparator();
      case "purchase-size" -> new MaxCardsComparator();
      default -> throw new BadJsonException("Invalid policy");
    };
  }

  /**
   * Generates an IPlayer from a JsonElement
   * @param json The JsonElement to be deserialized
   * @return the generated IPlayer
   */
  public static IPlayer actorFromJson(JsonElement json) throws BadJsonException {
    checkJsonArray(json);
    JsonArray jsonActor = json.getAsJsonArray();
    String name = jsonActor.get(0).getAsString();
    Strategy strategy = new Strategy(policyFromJson(jsonActor.get(1)));
    return new Mechanism(name, strategy);
  }

  /**
   * Generates an IPlayer which cheats on certain method calls
   * @param json The JsonElement to be deserialized
   * @return the generated IPlayer
   */
  public static IPlayer cheatActorFromJson(JsonElement json) throws BadJsonException {
    checkJsonArray(json);
    JsonArray jsonCheatActor = json.getAsJsonArray();
    String name = jsonCheatActor.get(0).getAsString();
    Strategy strategy = new Strategy(policyFromJson(jsonCheatActor.get(1)));
    if (!Objects.equals(jsonCheatActor.get(2).getAsString(), "a cheat")) {
      throw new BadJsonException("Invalid cheat actor");
    }
    return switch (jsonCheatActor.get(3).getAsString()) {
      case "use-non-existent-equation" -> new NonExistentEQActor(name, strategy);
      case "bank-cannot-trade" -> new BankCantTradeActor(name, strategy);
      case "wallet-cannot-trade" -> new WalletCantTradeActor(name, strategy);
      case "buy-unavailable-card" -> new BuyUnavailableCardActor(name, strategy);
      case "wallet-cannot-buy-card" -> new CantAffordCardActor(name, strategy);
      default -> throw new BadJsonException("Unsupported exn type for actor");
    };
  }

  /**
   * Generates an IPlayer which throws exceptions on certain method calls
   * @param json The JsonElement to be deserialized
   * @return the generated IPlayer
   */
  public static IPlayer exnActorFromJson(JsonElement json) throws BadJsonException {
    checkJsonArray(json);
    JsonArray jsonActor = json.getAsJsonArray();
    String name = jsonActor.get(0).getAsString();
    Strategy strategy = new Strategy(policyFromJson(jsonActor.get(1)));
    return switch (MName.fromString(jsonActor.get(2).getAsString())) {
      case MName.SETUP -> new SetupExnActor(name, strategy);
      case MName.REQUESTPT -> new PebblesExnActor(name, strategy);
      case MName.REQUESTCARDS -> new CardExnActor(name, strategy);
      case MName.WIN -> new WinExnActor(name, strategy);
      default -> throw new BadJsonException("Unsupported exn type for actor");
    };
  }

  /**
   * Generates an IPlayer which cheats on certain method calls
   * @param json The JsonElement to be deserialized
   * @return the generated IPlayer
   */
  public static IPlayer timeoutActorFromJson(JsonElement json) throws BadJsonException {
    checkJsonArray(json);
    JsonArray jsonActor = json.getAsJsonArray();
    String name = jsonActor.get(0).getAsString();
    Strategy strategy = new Strategy(policyFromJson(jsonActor.get(1)));
    int count = Integer.parseInt(jsonActor.get(3).getAsString());
    return switch (MName.fromString(jsonActor.get(2).getAsString())) {
      case MName.SETUP -> new SetupTOActor(name, strategy, count);
      case MName.REQUESTPT -> new PebblesTOActor(name, strategy, count);
      case MName.REQUESTCARDS -> new CardTOActor(name, strategy, count);
      case MName.WIN-> new WinTOActor(name, strategy, count);
      default -> throw new IllegalArgumentException("Unsupported exn type for actor");
    };
  }

  /**
   * Generates a list of IPlayers from a JsonElement
   * @param json the JsonElement to be deserialized
   * @return the generated list of IPlayer
   */
  public static List<IPlayer> actorsFromJson(JsonElement json) throws BadJsonException {
    checkJsonArray(json);
    JsonArray jsonActors = json.getAsJsonArray();
    List<IPlayer> actors = new ArrayList<>();
    for (JsonElement jsonActor : jsonActors) {
      JsonArray jsonActorArray = jsonActor.getAsJsonArray();
      if (jsonActorArray.size() == 2) {
        actors.add(actorFromJson(jsonActor));
      }
      else if (jsonActorArray.size() == 3) {
        actors.add(exnActorFromJson(jsonActor));
      }
      else if (jsonActorArray.size() == 4) {
        if (jsonActorArray.get(2).getAsString().equals("a cheat")) {
          actors.add(cheatActorFromJson(jsonActor));
        }
        else {
          actors.add(timeoutActorFromJson(jsonActor));
        }

      }
      else {
        throw new BadJsonException("Actor Json of unsupported size");
      }
    }
    return actors;
  }

  /**
   * Generates a list of Strings from a JsonElement
   * @param json the JsonElement to be deserialized
   * @return the generated List of Strings
   */
  public static List<String> namesFromJson(JsonElement json) throws BadJsonException {
    checkJsonArray(json);
    JsonArray jsonNames = json.getAsJsonArray();
    List<String> names = new ArrayList<>();
    for (JsonElement jsonName : jsonNames) {
      names.add(jsonName.getAsString());
    }
    return names;
  }

  public static ExchangeRequest exchangeRequestFromJson(JsonElement json) throws BadJsonException {
    if (json.isJsonPrimitive()) {
      if (!json.getAsJsonPrimitive().isBoolean() || json.getAsJsonPrimitive().getAsBoolean()) {
        throw new BadJsonException("Bad json: expected false primitive for draw request");
      }
      else {
        return new PebbleDrawRequest();
      }
    }
    else {
      return exchangeSequenceFromJson(json);
    }
  }

  /**
   * Checks that this JsonElement is a valid JsonArray
   * @param json the element to be tested
   */
  private static void checkJsonArray(JsonElement json) throws BadJsonException {
    if (json == null || !json.isJsonArray()) {
        throw new BadJsonException("Error Parsing JSONArray: " + json);
    }
  }

  /**
   * Checks that this JsonElement is a valid JsonObject
   * @param json the element to be tested
   */
  private static void checkJsonObject(JsonElement json) throws BadJsonException {
    if (!json.isJsonObject()) {
      throw new BadJsonException("Error Parsing JSONObject: " + json);
    }
  }

  /**
   * Generates a PebbleColor from a JsonElement
   * @param json The JsonElement to be deserialized
   * @return The generated PebbleColor
   */
  private static Pebble pebbleFromJson(JsonElement json) throws BadJsonException {
    if (!json.isJsonPrimitive()) {
      throw new BadJsonException("Parsing error");
    }
    String color = json.getAsString();
    return switch (color) {
      case "red" -> Pebble.RED;
      case "blue" -> Pebble.BLUE;
      case "white" -> Pebble.WHITE;
      case "green" -> Pebble.GREEN;
      case "yellow" -> Pebble.YELLOW;
      default -> throw new BadJsonException("Not a real pebble color");
    };
  }
}

