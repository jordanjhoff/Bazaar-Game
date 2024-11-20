package Server;

import Common.RuleBook;
import Common.converters.JSONSerializer;
import Player.IPlayer;
import Referee.GameObjectGenerator;
import Referee.GameResult;
import Referee.Observer;
import com.google.gson.JsonStreamParser;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.logging.Logger;

import static Server.CommunicationUtils.createDaemonExecutor;

public class Server {
    private static final Logger log = Logger.getLogger(Server.class.getName());
    private static final int waitingRoomMS = 20000;
    private static final int moveTimeoutMS = 5000;
    private static final int receiveNameTimeoutMS = 3000;
    private static final int maxNumPlayers = 6;
    private final ServerSocket serverSocket;
    public static void main(String[] args) throws IOException {
        Server s = new Server(4114);
        s.startBazaarServer();
    }

    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    public void startBazaarServer(Observer... observers) throws IOException {
        GameResult result = new GameResult(new ArrayList<>(), new ArrayList<>());
        List<IPlayer> acceptedPlayers = lobby();
        if (acceptedPlayers.size() > 1) {
            result = playGame(acceptedPlayers, observers);
        }
        sendResults(result, new PrintWriter(System.out));
        shutDown();
    }

    public void sendResults(GameResult result, Writer out) throws IOException {
        out.write(JSONSerializer.gameResultToJson(result).toString());
        out.flush();
    }

    public void shutDown() throws IOException {
        this.serverSocket.close();
    }


    /**
     * Creates two waiting rooms, and accepts player connections
     * @return a list of accepted IPlayers
     */
    public List<IPlayer> lobby() {
        log.info("Starting waiting room 1");
        List<IPlayer> players = new ArrayList<>(waitingRoom());
        if (players.size() < 2) {
            log.info("Starting waiting room 2");
            players.addAll(waitingRoom());
        }
        log.info("Received Players: " + players.stream().map(IPlayer::name).toList());
        return players;
    }

    /**
     * Accepts player connections
     * @return a list of accepted IPlayers
     */
    public List<IPlayer> waitingRoom() {
        List<IPlayer> players = new ArrayList<>();
        long startingTime = System.currentTimeMillis();
        ExecutorService executor = createDaemonExecutor();
        while (startingTime + waitingRoomMS > System.currentTimeMillis()) {
            try {
                serverSocket.setSoTimeout(100);
                Socket playerSocket = serverSocket.accept();
                playerSocket.setSoLinger(true, 0);
                executor.submit(() -> createProxyPlayerTask(players, playerSocket,
                        (int)Math.min(moveTimeoutMS, System.currentTimeMillis() - (startingTime + waitingRoomMS))));
            }
            catch (IOException ex) {
                //do nothing
            }
        }
        executor.shutdownNow();
        return new ArrayList<>(players);
    }


    public GameResult playGame(List<IPlayer> acceptedPlayers, Observer... observers)  {
        log.info("Starting game with " + acceptedPlayers.size() + " players.");
        GameObjectGenerator g = new GameObjectGenerator();
        ServerReferee serverReferee = new ServerReferee(acceptedPlayers, new RuleBook(g.generateRandomEquationTable()), moveTimeoutMS);
        for (Observer observer : observers) {
            serverReferee.addListener(observer);
        }
        return serverReferee.runGame();
    }


    private IPlayer createPlayerProxy(Socket playerSocket, int moveTimeoutMS) {
        log.info("Received player connection");
        try {
            InputStream serverStreamIn = playerSocket.getInputStream();
            JsonStreamParser parse = new JsonStreamParser(new InputStreamReader(serverStreamIn));
            Callable<String> getNameFromPlayer = () -> parse.next().getAsString();
            Optional<String> playerName = CommunicationUtils.timeout(getNameFromPlayer, moveTimeoutMS);
            log.info("Player name: " + playerName.orElseThrow());
            return new Player(playerName.orElseThrow(), serverStreamIn, playerSocket.getOutputStream(), log);
        }
        catch (Exception ex) {
            throw new PlayerException(ex.getMessage());
        }
    }

    private boolean uniqueName(String name, List<IPlayer> players) {
        return !players.stream().map(p -> p.name()).toList().contains(name);
    }

    private void createProxyPlayerTask(List<IPlayer> playerListToMutate, Socket playerSocket, int moveTimeoutMS) {
        IPlayer playerProxy = createPlayerProxy(playerSocket, moveTimeoutMS);
        synchronized (playerListToMutate) {
            if (uniqueName(playerProxy.name(), playerListToMutate) && playerListToMutate.size() < maxNumPlayers) {
                playerListToMutate.add(playerProxy);
            }
        }
    }
}
