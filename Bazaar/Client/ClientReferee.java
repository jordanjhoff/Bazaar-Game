package Client;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

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
  // player object for reachability
  IPlayer client;
  // write JSON Strings TO the server
  protected PrintWriter outputStream;
  // read JSON Strings FROM the server
  protected JsonStreamParser jsonStreamIn;
  // used for checking if the channel is ready
  protected InputStream streamIn;
  protected Socket socket;

  /**
   * Set up the referee with a Player object.
   * Note: IPlayer has a .name() method
   */
  public ClientReferee(IPlayer client) {
    this.client = client;
  }

  /**
   * Attempt to connect to the server & send the player's .name()
   * @param addr ServerSocket address
   * @param port ServerSocket port
   */
  public void connect(InetAddress addr, int port) {
    try {
      socket = new Socket(addr, port);
      outputStream = new PrintWriter(socket.getOutputStream());
      streamIn = socket.getInputStream();
      jsonStreamIn = new JsonStreamParser(new InputStreamReader(streamIn));
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }
    sendToServer(new JsonPrimitive(client.name()));
  }

  /**
   * Until the socket is closed:
   * Wait for JSON from the server, then handle the request & send the result back to the server
   */
  public void run() {
    while (true) {
      JsonArray json;
      try {
        // read one json object from the socket
        json = jsonStreamIn.next().getAsJsonArray();
        System.out.println(json.toString());
      }
      // exception occurs when .next() throws, indicates that the socket has been closed
      catch (JsonIOException e) {
        break;
      }
      String MName = json.get(0).getAsString();
      JsonElement Argument = json.get(1);
      sendToServer(delegateRequest(MName, Argument)); // handle & reply
    }
  }

  /**
   * Generic writer method.
   */
  private void sendToServer(JsonElement request) {
    System.out.println(request.toString());
    outputStream.println(request);
    outputStream.flush();
  }

  /**
   * We assume that clients bear no responsibility for sanitising JSON.
   * Delegates requests to the IPlayer client.
   */
  public JsonElement delegateRequest(String MName, JsonElement Argument) {
    isConnected();
    JsonElement firstArgument = Argument.getAsJsonArray().get(0);
    try {
      return switch (MName) {
        case "setup" -> setup(JSONDeserializer.equationTableFromJSON(firstArgument));
        case "request-pebble-or-trades" -> requestPT(JSONDeserializer.turnStateFromJson(firstArgument));
        case "request-cards" -> requestCards(JSONDeserializer.turnStateFromJson(firstArgument));
        case "win" -> win(firstArgument.getAsBoolean());
        default -> throw new BadJsonException("Bad MName: " + MName);
      };
    } catch (BadJsonException e) {
      throw new RuntimeException("Not supposed to be getting bad json from the server! " + e.getMessage());
    }
  }

  /**
   * Sets up the equatiom table and replies with JSON String "void"
   * @throws BadJsonException when the json is malformed; this should not happen. If so, no reply
   */
  public JsonElement setup(EquationTable table) throws BadJsonException {
    client.setup(table);
    return new JsonPrimitive("void");
  }

  /**
   * Ask the player for their pebble or trade request and return the json Element
   * @param t turn state
   * @return JSON Element representing the player's answer. False = pebble request.
   */
  public JsonElement requestPT(TurnState t) {
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
  public JsonElement requestCards(TurnState t) {
    return JSONSerializer.cardListToJson(
            client.requestCards(t).cards());
  }

  /**
   * Notify the player that they have won (or not).
   * @return JSON Element "void" IF the player returns from their win call.
   */
  public JsonElement win(Boolean b) {
    client.win(b);
    return new JsonPrimitive("void");
  }

  /**
   * Checks that the socket is connected... in a lot of ways. Much safety very gud 4 bossman blerner
   */
  private void isConnected() {
    if (socket == null || outputStream == null || jsonStreamIn == null || streamIn == null) {
      throw new IllegalStateException("Socket not connected");
    }
  }
}
