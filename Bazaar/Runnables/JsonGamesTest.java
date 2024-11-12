package Runnables;

import Common.*;
import Common.converters.BadJsonException;
import Player.IPlayer;

import Referee.GameState;
import Referee.GameResult;
import Referee.Referee;
import UnitTests.DeterministicObjectGenerator;
import com.google.gson.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import Common.converters.JSONDeserializer;
import Common.converters.JSONSerializer;

/**
 * This is a testing class for sending and receiving JSON values
 */
public class JsonGamesTest {
    public static void main(String[] args) throws IOException, BadJsonException {
        new GamesRunner().run(new InputStreamReader(System.in), new PrintWriter(System.out));
    }
}

class GamesRunner implements TestRunner {

    @Override
    public void run(InputStreamReader input, Writer out) throws IOException, BadJsonException {

        JsonStreamParser p = new JsonStreamParser(input);
        // Get inputs
        JsonElement jsonActors = p.next();
        JsonElement jsonEquations = p.next();
        JsonElement jsonGameState = p.next();

        // Deserialize
        List<IPlayer> players = JSONDeserializer.actorsFromJson(jsonActors);
        EquationTable equationTable = JSONDeserializer.equationTableFromJSON(jsonEquations);
        GameState gameState = JSONDeserializer.gameStateFromJson(jsonGameState);

        // Run game
        RuleBook ruleBook = new RuleBook(equationTable);
        GameResult result = runGame(players, gameState, ruleBook);
        List<String> winners = result.winners().stream().map(IPlayer::name).toList();
        List<String> kicked = result.kicked().stream().map(IPlayer::name).toList();

        // Serialize
        JsonElement jsonWinners = JSONSerializer.namesToJson(winners);
        JsonElement jsonKicked = JSONSerializer.namesToJson(kicked);

        // Output
        out.write(jsonWinners.toString());
        out.write(jsonKicked.toString());
        out.close();
    }

    protected GameResult runGame(List<IPlayer> players, GameState gameState, RuleBook ruleBook) throws IOException, BadJsonException {
        Referee referee = new Referee(players, gameState, ruleBook, new DeterministicObjectGenerator());
        return referee.runGame();
    }
}

class GamesTester extends MilestoneIntegrationTester {

    @Override
    void runTest(InputStreamReader testInput, StringWriter testOutput) throws IOException, BadJsonException {
        new GamesRunner().run(testInput, testOutput);
    }

    @Override
    public List<Object> jsonResultToObjects(InputStreamReader input) throws BadJsonException {
        JsonStreamParser p = new JsonStreamParser(input);
        List<Object> objects = new ArrayList<>();
        objects.add(JSONDeserializer.namesFromJson(p.next()));
        objects.add(JSONDeserializer.namesFromJson(p.next()));
        return objects;
    }
}
