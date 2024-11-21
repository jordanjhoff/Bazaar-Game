package Client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import Common.converters.BadJsonException;
import Common.converters.JSONDeserializer;
import Player.IPlayer;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

/**
 * This class accepts an IPlayer mechanism, and initializes a connection to a Server.
 * It delegates the playing of a Bazaar game to it's client referee
 */
public class Client {
  // proxy referee to bridge gap between server's referee and our player
  ClientReferee ref;
  IPlayer player;

  /**
   * Set up a client object with a player
   * note: player may persist through multiple games.
   *
   * @param player
   */
  public Client(IPlayer player) {
    this.player = player;
  }

  /**
   * Spawns a ClientReferee, tells it to connect to the server and run
   */
  public void start(InetAddress addr, int port) throws IOException {
    ref = new ClientReferee(player);
    ref.connect(addr, port);
    ref.run();
  }

  // default values for localhost server
  public void start() {
    try {
      start(InetAddress.getLocalHost(), 4114);
    } catch (IOException e) {
      System.err.printf("Could not resolve localhost: %s", e.getMessage());
    }
  }

  // runnable for tinkering purposes...
  public static void main(String[] args) throws BadJsonException, IOException {
    if (args.length < 1) {
      System.err.println("Must provide player name as an argument");
      System.exit(1);
    }
    if (args.length == 1) {
      JsonArray arr = new JsonArray();
      arr.add(JsonParser.parseString(args[0]));
      IPlayer mechanism = JSONDeserializer.actorsFromJson(arr).getFirst();
      new Client(mechanism).start();
    }

    if (args.length == 3) {
      JsonArray arr = new JsonArray();
      InetAddress address = InetAddress.getByName(args[0]);
      int port = Integer.parseInt(args[1]);
      arr.add(JsonParser.parseString(args[2]));
      IPlayer mechanism = JSONDeserializer.actorsFromJson(arr).getFirst();
      new Client(mechanism).start(address, port);
    }
  }
}
