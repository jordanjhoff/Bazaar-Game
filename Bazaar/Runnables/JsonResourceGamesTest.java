package Runnables;

import Common.*;
import Common.converters.BadJsonException;
import Player.IPlayer;

import Referee.GameState;
import Referee.GameResult;
import Referee.Referee;
import Server.ServerReferee;
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
public class JsonResourceGamesTest {
    public static void main(String[] args) throws IOException, BadJsonException {
        new ResourceGamesRunner().run(new InputStreamReader(System.in), new PrintWriter(System.out));
    }
}

class ResourceGamesRunner extends GamesRunner {

    protected GameResult runGame(List<IPlayer> players, GameState gameState, RuleBook ruleBook) throws IOException, BadJsonException {
        ServerReferee referee = new ServerReferee(players, gameState, ruleBook, new DeterministicObjectGenerator());
        return referee.runGame();
    }
}

class ResourceGamesTester extends MilestoneIntegrationTester {

    @Override
    List<Object> runTest(InputStreamReader testInput, StringWriter testOutput) throws IOException, BadJsonException {
        return new ResourceGamesRunner().run(testInput, testOutput);
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

