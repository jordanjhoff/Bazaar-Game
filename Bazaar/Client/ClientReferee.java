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
      throw new RuntimeException(e.getMessage());
    }
    sendToServer(new JsonPrimitive(client.name()));
  }

  public void run() {
    while (true) {
      JsonArray json;
      try {
         json = jsonStreamIn.next().getAsJsonArray();
      }
      catch (JsonIOException e) {
        break;
      }
      String MName = json.get(0).getAsString();
      JsonElement Argument = json.get(1);
      sendToServer(delegateRequest(MName, Argument));
    }
  }

  private void sendToServer(JsonElement request) {
    outputStream.println(request);
    outputStream.flush();
  }

  /**
   * We assume that all json from the server is well-formed and not the client's responsibility.
   * Delegate requests to the client.
   * @param MName
   * @param Argument
   * @throws BadJsonException
   */
  public JsonElement delegateRequest(String MName, JsonElement Argument) {
    isConnected();
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

  public JsonElement setup(EquationTable table) throws BadJsonException {
    client.setup(table);
    return new JsonPrimitive("void");
  }

  /**
   * Ask the player for their pebble or trade request and return the json string
   * @param t turn state
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

  public JsonElement requestCards(TurnState t) {
    return JSONSerializer.cardListToJson(
            client.requestCards(t).cards());
  }

  public JsonElement win(Boolean b) {
    client.win(b);
    return new JsonPrimitive("void");
  }

  private void isConnected() {
    if (socket == null || outputStream == null || jsonStreamIn == null || streamIn == null) {
      throw new IllegalStateException("Socket not connected");
    }
  }
}
