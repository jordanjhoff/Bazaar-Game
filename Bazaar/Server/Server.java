package Server;

import Common.EquationTable;
import Common.RuleBook;
import Common.converters.JSONSerializer;
import Common.converters.Time;
import Player.IPlayer;
import Referee.GameObjectGenerator;
import Referee.GameResult;
import Referee.GameState;
import Referee.Observer;

import UnitTests.DeterministicObjectGenerator;
import com.google.gson.JsonStreamParser;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.logging.Logger;

import static Server.CommunicationUtils.createDaemonExecutor;

/**
 * Represents a Server component in a game of Bazaar. Is in charge of creating a socket
 * and accepting client connections, and running a full game.
 */
public class Server {

  private static final Logger log = Logger.getLogger(Server.class.getName());
  // time for a single waiting room
  private static final int waitingRoomMS = 20000;
  // number of times to restart waiting room
  private static final int numWaitingRoom = 2;
  // time before a single move is timed out
  private static final int moveTimeoutMS = 1000;
  // time before a player is timed out for not sending name
  private static final int receiveNameTimeoutMS = 3000;
  // num players before exiting the waiting room
  private static final int numPlayersToExitWaitingRoom = 2;
  // max number of players in one Bazaar game
  private static final int maxNumPlayers = 6;
  // number of at most players before the game returns default result
  private static final int numPlayersToStartGame = 1;
  // the socket port used for communication
  private final int port;

  public static void main(String[] args) throws IOException {
    if (args.length == 1) {
      Server s = new Server(Integer.parseInt(args[0]));
      //s.startBazaarServer(new OutputStreamWriter(System.out));
    }
  }

  /**
   * Setup the referee
   *
   * @param port the port to open the socket
   * @throws IOException if the serversocket fails
   */
  public Server(int port) throws IOException {
    this.port = port;
  }

  /**
   * Runs a full lifecycle of a Bazaar server. This method creates a new server socket for every game
   *
   * @param observers the optional observers to add the ServerReferee
   * @throws IOException if the serverSocket fails
   */
  public GameResult startBazaarServer(GameState gameState, RuleBook ruleBook, GameObjectGenerator generator, Observer... observers) throws IOException {
    ServerSocket serverSocket = new ServerSocket(port);
    GameResult result = new GameResult(new ArrayList<>(), new ArrayList<>());
    List<IPlayer> acceptedPlayers = runWaitingRooms(serverSocket);
    if (acceptedPlayers.size() > numPlayersToStartGame) {
      result = playGame(acceptedPlayers, gameState, ruleBook, generator, observers);
    }
    return result;
  }

  /**
   * Creates two waiting rooms, and accepts player connections
   *
   * @return a list of accepted IPlayers
   */
  private List<IPlayer> runWaitingRooms(ServerSocket serverSocket) {
    List<IPlayer> players = new ArrayList<>();
    int counter = 0;
    while (players.size() < numPlayersToExitWaitingRoom && counter < numWaitingRoom) {
      counter+=1;
      log.info("Starting waiting room " + counter);
      players = waitingRoom(serverSocket, players);
    }
    log.info("Received Players: " + players.stream().map(IPlayer::name).toList());
    return players;
  }

  /**
   * Accepts client connections in a waiting room.
   *
   * @param serverSocket the socket to be used for this current game
   * @return the list of accepted players during this waiting room
   */
  private List<IPlayer> waitingRoom(ServerSocket serverSocket, List<IPlayer> previousPlayers) {
    List<IPlayer> players = new ArrayList<>(previousPlayers);
    long startingTime = Time.NsToMs(System.nanoTime());
    ExecutorService executor = createDaemonExecutor();
    while (startingTime + waitingRoomMS > Time.NsToMs(System.nanoTime()) && players.size() < maxNumPlayers) {
      try {
        /* yes, this is a magic constant. no, it's not actually important
           we don't want to listen on the server socket the whole time;
           what if a thread returns, and six players are now in the game,
           but there is like 15 seconds left on the waiting room?
           we could be in a game already. no need to keep waiting.         */
        int maxtime = (int) Math.min(100, (startingTime + waitingRoomMS) - Time.NsToMs(System.nanoTime()));
        serverSocket.setSoTimeout(maxtime);
        Socket playerSocket = serverSocket.accept();
        int maxtimetosendname = (int) Math.min(receiveNameTimeoutMS, (startingTime + waitingRoomMS) - Time.NsToMs(System.nanoTime()));
        executor.submit(() -> createProxyPlayerTask(players, playerSocket, maxtimetosendname));
      } catch (IOException ex) {
        //do nothing
      }
    }
    executor.shutdownNow();
    return new ArrayList<>(players);
  }

  /**
   * Executes a single game of Bazaar with the clients
   *
   * @param players   the players to play the game with
   * @param observers the optional observers to add to the ServerReferee
   * @return the result of the game
   */
  private GameResult playGame(List<IPlayer> players, GameState intermediateState, RuleBook ruleBook, GameObjectGenerator randomizer, Observer... observers) {
    log.info("Starting game with " + players.size() + " players.");
    ServerReferee serverReferee = new ServerReferee(players, intermediateState, ruleBook, randomizer, moveTimeoutMS, observers);
    return serverReferee.runGame();
  }


  /**
   * Creates a Player, and awaits a name
   *
   * @param playerSocket        the clientSocket
   * @param acceptNameTimeoutMS the window of time a player must send their name after being accepted
   * @return the created proxy player
   * @throws PlayerException if a name is not received, or if the client errored
   */
  private IPlayer createPlayerProxy(Socket playerSocket, int acceptNameTimeoutMS) {
    log.info("Received player connection");
    try {
      InputStream serverStreamIn = playerSocket.getInputStream();
      JsonStreamParser parse = new JsonStreamParser(new InputStreamReader(serverStreamIn));
      Callable<String> getNameFromPlayer = () -> parse.next().getAsJsonPrimitive().getAsString();
      Optional<String> playerName = CommunicationUtils.timeout(getNameFromPlayer, acceptNameTimeoutMS);
      return new Player(playerName.orElseThrow(), serverStreamIn, playerSocket.getOutputStream(), log);
    } catch (Exception ex) {
      log.info("Did not accept player: " + ex.getMessage());
      throw new PlayerException(ex.getMessage());
    }
  }

  /**
   * Checks for unique name
   *
   * @param name    the name to check
   * @param players the list of players
   * @return true if the name is unique, else return false
   */
  private boolean uniqueName(String name, List<IPlayer> players) {
    return !players.stream().map(p -> p.name()).toList().contains(name);
  }

  /**
   * The executable task to create a proxy player, and add them to the list of players. This method
   * only adds them if they have a distinct name, and the number of players is less than the max.
   *
   * @param playerListToMutate  the list of players that the created proxy Player will be added to
   * @param playerSocket        the clientSocket
   * @param acceptNameTimeoutMS the window of time a player must send their name after being accepted
   */
  private void createProxyPlayerTask(List<IPlayer> playerListToMutate, Socket playerSocket, int acceptNameTimeoutMS) {
    IPlayer playerProxy = createPlayerProxy(playerSocket, acceptNameTimeoutMS);
    synchronized (playerListToMutate) {
      if (uniqueName(playerProxy.name(), playerListToMutate) && playerListToMutate.size() < maxNumPlayers) {
        playerListToMutate.add(playerProxy);
        log.info("Player name: " +  playerProxy.name());
      }
      else {
        log.info("Did not accept player: " + playerProxy.name());
      }
    }
  }

  /**
   * Sends the game result to the output
   *
   * @param result the gameresult
   * @param out    the output
   * @throws IOException
   */
  private void sendResults(GameResult result, Writer out) throws IOException {
    out.write(JSONSerializer.gameResultToJson(result).toString());
    out.flush();
  }
}
