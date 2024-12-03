package Client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

import Common.converters.BadJsonException;
import Common.converters.JSONDeserializer;
import Player.IPlayer;
import Server.Server;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;

import static Server.CommunicationUtils.createDaemonExecutor;

/**
 * This class accepts an IPlayer mechanism, and initializes a connection to a Server.
 * It delegates the playing of a Bazaar game to it's client referee
 */
public class Client {
  public static final int DEFAULT_PORT = 4114;

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
   * Starts a client referee asynchronously.
   * Note: retry:true may cause this to loop forever if the client is never able to connect.
   * @param retry true if a failed connection should continually initiate reconnects
   * @return true if the ref connected and ran without throwing exceptions
   */
  public boolean startAsync(InetAddress addr, int port, Executor executor, boolean retry) throws InterruptedException {
    do {
      try {
        ref = connect(addr, port);
        log.info(player.name() + " connected successfully");
        executor.execute(() -> ref.run());
        Thread.sleep(500); // todo determine source of weird behavior -- maybe don't use daemon executor?
        return true;
      } catch (IOException | InterruptedException e) {
        log.info(String.format(e.getMessage()));
        Thread.sleep(1000); // also wtf, anti-spam? :D
      }
    } while (retry);
    return false;
  }





  // runnable for tinkering purposes...
  public static void main(String[] args) throws BadJsonException, IOException, InterruptedException {
    if (args.length < 1) {
      System.err.println("Must provide player name as an argument");
      System.exit(1);
    }

    ExecutorService executor = createDaemonExecutor();

    // string[] args = [actorJSON]
    if (args.length == 1) {
      JsonArray arr = new JsonArray();
      arr.add(JsonParser.parseString(args[0]));
      IPlayer mechanism = JSONDeserializer.actorsFromJson(arr).getFirst();
      new Client(mechanism).startAsync(InetAddress.getLocalHost(), DEFAULT_PORT, executor, true);
    }

    // string[] args = [addr, port, actorJSON]
    if (args.length == 3) {
      JsonArray arr = new JsonArray();
      InetAddress address = InetAddress.getByName(args[0]);
      int port = Integer.parseInt(args[1]);
      arr.add(JsonParser.parseString(args[2]));
      IPlayer mechanism = JSONDeserializer.actorsFromJson(arr).getFirst();
      new Client(mechanism).startAsync(address, port, executor, true);
    }
  }
}
