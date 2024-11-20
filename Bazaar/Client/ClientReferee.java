package Client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import Common.EquationTable;
import Common.ExchangeRequest;
import Common.PebbleDrawRequest;
import Common.PebbleExchangeSequence;
import Common.TurnState;
import Common.converters.BadJsonException;
import Common.converters.JSONDeserializer;
import Common.converters.JSONSerializer;
import Player.IPlayer;

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
  public ClientReferee(IPlayer client) {
    this.client = client;
  }

  /**
   * Attempt to connect to the server & send the player's name.
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
      System.err.printf("Could not establish socket: %s", e.getMessage());
      e.printStackTrace();
      throw new RuntimeException();
    }
    outputStream.println(new JsonPrimitive(client.name()));
    outputStream.flush();
  }

  private void write(JsonElement e) {
    outputStream.write(e.toString());
    outputStream.flush();
  }

  public void run() {
    while (waitForUpdate()) {
      JsonArray json = readUpdate();
      String MName = json.get(0).getAsString();
      JsonElement Argument = json.get(1);
      write(handleRequest(MName, Argument));
    }
  }

  /**
   * Sleep until the socket's ready.
   */
  public boolean waitForUpdate() {
    try {
      while (streamIn.available() == 0) {
        Thread.sleep(100);
        // schrodinger's TCP connection FUCK YOU
        socket.getOutputStream().write(' ');
      }
    }
    catch (InterruptedException e) {
      // this should never happen
    } catch (SocketException e) {
      return false;
    } catch (IOException e) {
      // something has gone wrong with socket availability
      throw new RuntimeException(e);
    }
    return true;
  }

  /**
   * Read the update from the socket and return the JSON
   * The JSON is 'safe'. trust me bro
   * @return jsonelement with the request
   */
  public JsonArray readUpdate() {
    if (!jsonStreamIn.hasNext())
      throw new IllegalStateException("Reading update when not ready");
    JsonArray update = jsonStreamIn.next().getAsJsonArray();
    System.out.println(update);
    return update;
  }

  /**
   * We assume that all json from the server is well-formed and not the client's responsibility.
   * Delegate requests to the client.
   * @param MName
   * @param Argument
   * @throws BadJsonException
   */
  public JsonElement handleRequest(String MName, JsonElement Argument) {
    try {
      return switch (MName) {
        case "setup" -> setup(JSONDeserializer.equationTableFromJSON(Argument.getAsJsonArray()));
        case "request-pebble-or-trades" -> requestPT(JSONDeserializer.turnStateFromJson(Argument));
        case "request-cards" -> requestCards(JSONDeserializer.turnStateFromJson(Argument));
        case "win" -> win(Argument.getAsBoolean());
        default -> throw new BadJsonException("Bad MName: " + MName);
      };
    } catch (BadJsonException e) {
      throw new RuntimeException("Not supposed to be getting bad json from the server! " + e.getMessage());
    }
  }

  public JsonPrimitive setup(EquationTable table) throws BadJsonException {
    client.setup(table);
    return new JsonPrimitive("void");
  }

  /**
   * Ask the player for their pebble or trade request and return the json string
   * @param t turn state
   */
  public JsonElement requestPT(TurnState t) {
    ExchangeRequest r = client.requestPebbleOrTrades(t);
    if (r instanceof PebbleDrawRequest)
      return new JsonPrimitive(false);

    // else
    return JSONSerializer.exchangeSequenceToJson(
            ((PebbleExchangeSequence) r));
  }

  public JsonElement requestCards(TurnState t) {
    return JSONSerializer.cardListToJson(
            client.requestCards(t).cards());
  }

  public JsonPrimitive win(Boolean b) {
    client.win(b);
    return new JsonPrimitive("void");
  }
}
