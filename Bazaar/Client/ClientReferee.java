package Client;

import Common.converters.MName;

import com.google.gson.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import Common.EquationTable;
import Common.ExchangeRequest;
import Common.PebbleDrawRequest;
import Common.PebbleExchangeSequence;
import Common.TurnState;
import Common.converters.BadJsonException;
import Common.converters.JSONDeserializer;
import Common.converters.JSONSerializer;
import Player.IPlayer;

/**
 * ClientReferee acts as an abstraction layer that allows regular IPlayers to play remote games.
 * This ClientReferee is responsible for deserializing server messages into requests to the player,
 * and serializing responses from the player and sending them back to the server.
 * ----
 * The ClientReferee is also responsible for setting up its own Socket & I/O--just give it the IP/port
 * in the connect method before calling run().
 * ----
 * Clients provided to the ClientReferee may be denied from the server if their name is already taken.
 * The server requires all playernames to be distinct. Please make sure your IPlayers are set up with unique names.
 */
public class ClientReferee {
  private static final Logger log = Logger.getLogger(ClientReferee.class.getName());
  // player object for reachability
  IPlayer client;
  // write JSON Strings TO the server
  protected PrintWriter outputWriter;
  // read JSON Strings FROM the server
  protected JsonStreamParser jsonStreamIn;
  // used for checking if the channel is ready
  protected InputStream streamIn;
  public final static String ACK = "void";
  protected boolean gameOver;

  /**
   * Set up the referee with a Player object.
   * Note: IPlayer has a .name() method
   */
  public ClientReferee(IPlayer client, InputStream streamIn, OutputStream streamOut) throws IOException {
    this.client = client;
    this.outputWriter = new PrintWriter(streamOut, true);
    this.streamIn = streamIn;
    this.jsonStreamIn = new JsonStreamParser(new InputStreamReader(streamIn));
    this.gameOver = false;
    sendToServer(new JsonPrimitive(client.name()));
  }

  /**
   * Until the socket is closed:
   * Wait for JSON from the server, then handle the request & send the result back to the server
   */
  public void run() {
    while (!gameOver) {
      JsonArray json;
      try {
        // read one json object from the socket
        json = jsonStreamIn.next().getAsJsonArray();
        log.info(this.client.name() + " Received: " + json.toString());
      }
      // exception occurs when .next() throws, indicates that the socket has been closed
      catch (JsonIOException | NoSuchElementException e) {
        log.info("Socket connection broken or closed");
        break;
      }
      String MName = json.get(0).getAsString();
      JsonElement Argument = json.get(1);
      sendToServer(delegateRequest(MName, Argument)); // handle & reply
    }
    log.info("Client shutting down.");
  }

  /**
   * Generic writer method.
   */
  public void sendToServer(JsonElement request) {
    log.info(this.client.name() + " Sent to server " + request.toString());
    outputWriter.println(request);
    outputWriter.flush();
  }

  /**
   * We assume that clients bear no responsibility for sanitising JSON.
   * Delegates requests to the IPlayer client.
   */
  public JsonElement delegateRequest(String methodName, JsonElement Argument) {
    JsonElement firstArgument = Argument.getAsJsonArray().get(0);
    try {
      MName methodEnum = MName.fromString(methodName);
      return switch (methodEnum) {
        case MName.SETUP -> setup(firstArgument);
        case MName.REQUESTPT -> requestPT(firstArgument);
        case MName.REQUESTCARDS -> requestCards(firstArgument);
        case MName.WIN -> win(firstArgument);
      };
    } catch (BadJsonException e) {
      throw new RuntimeException("Not supposed to be getting bad json from the server! " + e.getMessage());
    }
  }

  /**
   * Sets up the equation table and replies with {@link #ACK}
   * @throws BadJsonException when the json is malformed; this should not happen. If so, no reply
   */
  public JsonElement setup(JsonElement argument) throws BadJsonException {
    EquationTable table = JSONDeserializer.equationTableFromJSON(argument);
    client.setup(table);
    return new JsonPrimitive(ACK);
  }

  /**
   * Ask the player for their pebble or trade request and return the json Element
   * @return JSON Element representing the player's answer. False = pebble request.
   */
  public JsonElement requestPT(JsonElement argument) throws BadJsonException {
    TurnState t = JSONDeserializer.turnStateFromJson(argument);
    ExchangeRequest r = client.requestPebbleOrTrades(t);
    if (r instanceof PebbleDrawRequest) {
      return new JsonPrimitive(false);
    }
    else {
      return JSONSerializer.exchangeSequenceToJson(
              ((PebbleExchangeSequence) r));
    }
  }

  /**
   * Request the cards from the player
   * @return JSON Element representing the player's answer.
   */
  public JsonElement requestCards(JsonElement argument) throws BadJsonException {
    TurnState t = JSONDeserializer.turnStateFromJson(argument);
    return JSONSerializer.cardListToJson(
            client.requestCards(t).cards());
  }

  /**
   * Notify the player that they have won (or not).
   * @return JSON Element {@link #ACK} IF the player returns from their win call.
   */
  public JsonElement win(JsonElement argument) throws BadJsonException {
    if (argument.isJsonPrimitive() && argument.getAsJsonPrimitive().isBoolean()) {
      boolean b = argument.getAsBoolean();
      client.win(b);
      this.gameOver = true;
      return new JsonPrimitive(ACK);
    }
    throw new BadJsonException("Non-boolean fed to win method");
  }

}
