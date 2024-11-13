package Server;

import Common.RuleBook;
import Common.converters.JSONDeserializer;
import Common.converters.JSONSerializer;
import Player.IPlayer;
import Referee.GameObjectGenerator;
import Referee.GameResult;
import Referee.Observer;
import com.google.gson.JsonStreamParser;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.ServerRef;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

public class Server {
    private static final int waitingRoomMS = 5000;
    private final ServerSocket serverSocket;
    private final List<IPlayer> proxies = new ArrayList<>();
    public static void main(String[] args) throws IOException {
        Server s = new Server(4114);
        s.startBazaarServer();
    }

    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    public void startBazaarServer(Observer... observers) throws IOException {
        GameResult result = new GameResult(new ArrayList<>(), new ArrayList<>());
        if (lobby()) {
            result = playGame(Arrays.stream(observers).toList());
        }
        sendResults(result, new PrintWriter(System.out));
        shutDown();
    }



    protected <T> Optional<T> timeout(Callable<T> task, int timeoutMs) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<T> future = executor.submit(task);
        try {
            T result = future.get(timeoutMs, TimeUnit.MILLISECONDS);
            return Optional.of(result);
        } catch (TimeoutException | ExecutionException | InterruptedException ex) {
            future.cancel(true);
        } finally {
            future.cancel(true);
        }
        return Optional.empty();
    }

    public void sendResults(GameResult result, Writer out) throws IOException {
        out.write(JSONSerializer.gameResultToJson(result).toString());
        out.flush();
    }

    public void shutDown() throws IOException {
        this.serverSocket.close();
    }



    public boolean lobby() throws IOException {
        System.out.println("Waiting room 1");
        waitingRoom(waitingRoomMS);
        if (this.proxies.size() < 2) {
            System.out.println("Waiting room 2");
            waitingRoom(waitingRoomMS);
        }
        return this.proxies.size() >= 2;
    }

    public void waitingRoom(int waitTimeMs) {
        long startingTime = System.currentTimeMillis();
        while (startingTime + waitTimeMs > System.currentTimeMillis()) {
            try {
                serverSocket.setSoTimeout(100);
                Socket playerSocket = serverSocket.accept();
                new Thread(() -> createPlayerProxy(playerSocket)).start();
            }
            catch (IOException ex) {
                //do nothing
            }
        }
    }


    public GameResult playGame(List<Observer> observers) throws IOException {
        System.out.println("Starting game with " + proxies.size() + " players.");
        GameObjectGenerator g = new GameObjectGenerator();
        ServerReferee serverReferee = new ServerReferee(this.proxies, new RuleBook(g.generateRandomEquationTable()));
        for (Observer observer : observers) {
            serverReferee.addListener(observer);
        }
        return serverReferee.runGame();
    }


    private void createPlayerProxy(Socket playerSocket) {
        IPlayer guy;
        try {
            InputStream serverStreamIn = playerSocket.getInputStream();
            OutputStream serverStreamOut = playerSocket.getOutputStream();
            JsonStreamParser parse = new JsonStreamParser(new InputStreamReader(serverStreamIn));
            long startTime = System.currentTimeMillis();
            while (!parse.hasNext() && startTime + 3000 < System.currentTimeMillis() ) {
                Thread.sleep(100);
            }
            String name = parse.next().getAsString();
            guy = new Player(name, serverStreamIn, serverStreamOut);
        }
        catch (Exception ex) {
            return; //doesn't add player to list
        }
        synchronized (proxies) {
            proxies.add(guy);
            System.out.println(guy.name() + " connected.");
            proxies.notifyAll();
        }
    }

}
