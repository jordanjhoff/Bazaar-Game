package Client;

import java.io.IOException;
import java.net.InetAddress;

import Common.converters.BadJsonException;
import Common.converters.JSONDeserializer;
import Player.IPlayer;
import Player.Mechanism;
import Player.Strategy;
import Player.comparators.MaxCardsComparator;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

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
   * Attempts to connect to the server
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
  public static void main(String[] args) throws BadJsonException {
    if (args.length < 1) {
      System.err.println("Must provide player name as an argument");
      System.exit(1);
    }

    if (args.length == 2) {
      JsonArray arr = new JsonArray();
      arr.add(JsonParser.parseString(args[1]));
      IPlayer mechanism = JSONDeserializer.actorsFromJson(arr).getFirst();
      new Client(mechanism).start();
    }
    else {
      new Client(new Mechanism(args[0], new Strategy(new MaxCardsComparator())))
              .start();
    }
  }
}
