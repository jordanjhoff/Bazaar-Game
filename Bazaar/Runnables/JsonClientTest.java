package Runnables;

import Client.Client;
import Common.RuleBook;
import Common.converters.BadJsonException;
import Common.converters.JSONDeserializer;
import Player.IPlayer;
import Referee.GameResult;
import Referee.GameState;
import Server.ServerReferee;
import UnitTests.DeterministicObjectGenerator;
import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static Server.CommunicationUtils.createDaemonExecutor;

public class JsonClientTest {

    public static int portNumber = 4414;
    public static void main(String[] args) throws IOException, BadJsonException, InterruptedException {
        if (args.length == 1) {
            portNumber = Integer.parseInt(args[0]);
        }
        run(new InputStreamReader(System.in), new PrintWriter(System.out), portNumber);
    }

    public static void run(InputStreamReader input, Writer out, int port) throws IOException, BadJsonException, InterruptedException {

        JsonStreamParser p = new JsonStreamParser(input);
        List<IPlayer> gameActors = JSONDeserializer.actorsFromJson(p.next());
        List<Client> clients = gameActors.stream().map(actor -> new Client(actor)).toList();
        ExecutorService executor = createDaemonExecutor();
        for (Client client : clients) {
            client.startAsync(InetAddress.getLocalHost(), port, executor);
        }
        executor.shutdown();
        executor.awaitTermination(15,TimeUnit.MINUTES);
    }
}


