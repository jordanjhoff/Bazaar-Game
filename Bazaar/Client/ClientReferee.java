package Client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
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
    outputStream.write(client.name());
    outputStream.flush();
  }

  public void run() {
    while (!socket.isClosed()) {
      waitForUpdate();
      JsonArray json = readUpdate();
      String MName = json.get(0).getAsString();
      JsonElement Argument = json.get(1);
      delegateRequest(MName, Argument);
    }
  }

  /**
   * Sleep until the socket's ready.
   */
  public void waitForUpdate() {
    try {
      while (streamIn.available() == 0) {
        Thread.sleep(10);
      }
    }
    catch (InterruptedException e) {

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Read the update from the socket and return the JSON
   * The JSON is 'safe'. trust me bro
   * @return jsonelement with the request
   */
  public JsonArray readUpdate() {
    if (!jsonStreamIn.hasNext())
      throw new IllegalStateException("Reading update when not ready");
    return jsonStreamIn.next().getAsJsonArray();
  }

  /**
   * We assume that all json from the server is well-formed and not the client's responsibility.
   * Delegate requests to the client.
   * @param MName
   * @param Argument
   * @throws BadJsonException
   */
  public String delegateRequest(String MName, JsonElement Argument) {
    try {
      return switch (MName) {
        case "setup" -> setup(JSONDeserializer.equationTableFromJSON(Argument.getAsJsonArray()));
        case "request-pebble-or-trades" -> requestPT(JSONDeserializer.turnStateFromJson(Argument));
        case "request-cards" -> requestCards(JSONDeserializer.turnStateFromJson(Argument));
        case "win" -> win(Argument.getAsBoolean());
        default -> throw new BadJsonException("Bad MName: " + MName);
      };
    } catch (BadJsonException e) {
      throw new RuntimeException("Not supposed to be getting bad json! " + e.getMessage());
    }
  }

  public String setup(EquationTable table) throws BadJsonException {
    client.setup(table);
    return "void";
  }

  /**
   * Ask the player for their pebble or trade request and return the json string
   * @param t turn state
   */
  public String requestPT(TurnState t) {
    ExchangeRequest r = client.requestPebbleOrTrades(t);
    if (r instanceof PebbleDrawRequest)
      return "False";
    return JSONSerializer.exchangeSequenceToJson(
            ((PebbleExchangeSequence) r))
            .toString();
  }

  public String requestCards(TurnState t) {
    return JSONSerializer.cardListToJson(
            client.requestCards(t).cards())
            .toString();
  }

  public String win(Boolean b) {
    client.win(b);
    return "void";
  }
}
