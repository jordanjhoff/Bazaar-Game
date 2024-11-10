package Server;

import Common.RuleBook;
import Player.IPlayer;
import Referee.GameObjectGenerator;
import Referee.GameResult;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

public class Server {

    ServerSocket serverSocket;
    final List<IPlayer> proxies = new ArrayList<>();
    public static void main(String[] args) throws IOException {
        new Server().lobby();
    }

    public Server() {

    }

    public void startServer() throws IOException {
        GameResult result = new GameResult(new ArrayList<>(), new ArrayList<>());
        if (lobby()) {
            result = playGame();
        }
        //sendResults(result);
        //shutDown();
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



    public boolean lobby() throws IOException {
        waitingRoom(20000);
        if (this.proxies.size() < 2) {
            waitingRoom(20000);
        }
        return this.proxies.size() >= 2;
    }

    public void waitingRoom(int waitTimeMs) {
        long startingTime = System.currentTimeMillis();
        while (System.currentTimeMillis() + waitTimeMs < startingTime) {
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

    public GameResult playGame() {
        GameObjectGenerator g = new GameObjectGenerator();
        ServerReferee serverReferee = new ServerReferee(this.proxies, new RuleBook(g.generateRandomEquationTable()));
        return serverReferee.runGame();
    }


    private void createPlayerProxy(Socket playerSocket) {
        IPlayer guy;
        try {
            guy = new Player(playerSocket.getInputStream(), playerSocket.getOutputStream());
        }
        catch (IOException ex) {
            return; //doesn't add player to list
        }
        synchronized (proxies) {
            proxies.add(guy);
            proxies.notifyAll();
        }
    }

}
