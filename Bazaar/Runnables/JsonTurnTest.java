package Runnables;

import Common.TurnState;
import Common.converters.BadJsonException;
import Referee.GameState;

import com.google.gson.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import Common.converters.JSONDeserializer;
import Common.converters.JSONSerializer;

/**
 * This is a testing class for sending and receiving JSON values
 */
public class JsonTurnTest {
    public static void main(String[] args) throws IOException, BadJsonException {
        new TurnRunner().run(new InputStreamReader(System.in), new PrintWriter(System.out));
    }
}

class TurnRunner implements TestRunner {
    @Override
    public List<Object> run(InputStreamReader input, Writer out) throws IOException, BadJsonException {
        JsonStreamParser p = new JsonStreamParser(input);

        JsonElement gameStateJson = p.next();
        GameState gameState = JSONDeserializer.gameStateFromJson(gameStateJson);

        TurnState turnState = gameState.getTurnState();

        JsonElement turnStateJson = JSONSerializer.turnStateToJson(turnState);

        out.write(turnStateJson.toString());
        out.close();
        return List.of(turnState);
    }
}

class TurnTester extends MilestoneIntegrationTester {
    @Override
    List<Object> runTest(InputStreamReader testInput, StringWriter testOutput) throws IOException, BadJsonException {
        return new TurnRunner().run(testInput, testOutput);
    }

    @Override
    public List<Object> jsonResultToObjects(InputStreamReader input) throws BadJsonException {
        JsonStreamParser p = new JsonStreamParser(input);
        List<Object> objects = new ArrayList<>();
        objects.add(JSONDeserializer.turnStateFromJson(p.next()));
        return objects;
    }
}


