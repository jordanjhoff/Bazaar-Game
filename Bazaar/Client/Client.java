package Client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

import Common.converters.BadJsonException;
import Common.converters.JSONDeserializer;
import Player.IPlayer;
import Server.Server;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;

/**
 * This class accepts an IPlayer mechanism, and initializes a connection to a Server.
 * It delegates the playing of a Bazaar game to it's client referee
 */
public class Client {
  // proxy referee to bridge gap between server's referee and our player
  ClientReferee ref;
  IPlayer player;
  private static final Logger log = Logger.getLogger(Client.class.getName());

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
   * Attempt to connect to the server
   * @param addr ServerSocket address
   * @param port ServerSocket port
   * @return the ClientReferee if the connection is successful
   */
  public ClientReferee connect(InetAddress addr, int port) throws IOException {
      Socket clientSocket = new Socket(addr, port);
      return new ClientReferee(player, clientSocket.getInputStream(), clientSocket.getOutputStream());
  }

  /**
   * Continually tries to connect to the server.
   */
  public void start(InetAddress addr, int port, boolean retry) {
    do {
      try {
        start(addr, port);
        // this breaks after the referee is finished running.
        // otherwise, we didn't get here because an exception was thrown. so we try again.
        break;
      } catch (IOException e) {
        log.info(String.format("Could not resolve localhost: %s", e.getMessage()));
      }
    } while (true);
  }


  /**
   * Connects to the server and spawns a ClientReferee. Tells the ref to run -- send name, listen
   * Only tries once! if it fails, it will exit.
   */
  public void start(InetAddress addr, int port) throws IOException {
    ref = connect(addr, port);
    ref.run();
  }

  public boolean startAsync(InetAddress addr, int port, Executor executor) throws InterruptedException {
    do {
      try {
        ref = connect(addr, port);
        log.info(player.name() + " connected successfully");
        executor.execute(() -> ref.run());
        Thread.sleep(500);
        return true;
      } catch (IOException | InterruptedException e) {
        log.info(String.format(e.getMessage()));
        Thread.sleep(1000);
      }
    } while (true);
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
      new Client(mechanism).start(InetAddress.getLocalHost(), 4114);
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
