package Runnables;

import Common.*;
import Common.converters.BadJsonException;
import Player.IPlayer;

import Referee.GameResult;
import Referee.GameState;
import Server.Server;
import UnitTests.DeterministicObjectGenerator;
import com.google.gson.*;

import java.io.*;
import java.util.List;
import java.util.function.Function;

import Common.converters.JSONDeserializer;
import Common.converters.JSONSerializer;

/**
 * This is a testing class for sending and receiving JSON values
 */
public class JsonServerTest {
    public static void main(String[] args) throws IOException, BadJsonException {
        new ServerRunner().run(new InputStreamReader(System.in), new PrintWriter(System.out), args[0]);
    }
}

class ServerRunner implements TestRunner {

    @Override
    public List<Object> run(InputStreamReader input, Writer out, String... args) throws IOException, BadJsonException {

        Function<PlayerInformation, PlayerInformation> bonusFunction = Function.identity();
        JsonStreamParser p = new JsonStreamParser(input);

        // Get inputs
        int port = Integer.parseInt(args[0]);
        JsonElement jsonEquations = p.next();
        JsonElement jsonGameState = p.next();
        if (p.hasNext()) {
            //optional bonus function
            JsonElement jsonBonus = p.next();
            bonusFunction = JSONDeserializer.bonusFromJson(jsonBonus);
        }


        // Deserialize
        EquationTable equationTable = JSONDeserializer.equationTableFromJSON(jsonEquations);
        GameState gameState = JSONDeserializer.gameStateFromJson(jsonGameState);


        // Run game
        Server server = new Server(port);
        GameResult result = server.startBazaarServer(gameState, new RuleBook(equationTable, bonusFunction), new DeterministicObjectGenerator());

        // Serialize

        JsonElement jsonResult = JSONSerializer.gameResultToJson(result);

        // Output
        out.write(jsonResult.toString());
        out.close();
        return List.of(result);
    }

}

class ServerTester extends MilestoneIntegrationTester {

    @Override
    List<Object> runTest(InputStreamReader testInput, StringWriter testOutput) throws IOException, BadJsonException {
        return new ServerRunner().run(testInput, testOutput);
    }

    @Override
    public List<Object> jsonResultToObjects(InputStreamReader input) throws BadJsonException {
        JsonStreamParser p = new JsonStreamParser(input);
        JsonArray stringNames = p.next().getAsJsonArray();
        return List.of(JSONDeserializer.namesFromJson(stringNames.get(0)),
                JSONDeserializer.namesFromJson(stringNames.get(0)));
    }
}
