package Runnables;

import Common.*;

import com.google.gson.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import Common.converters.JSONDeserializer;
import Common.converters.JSONSerializer;

/**
 * This is a testing class for sending and receiving JSON values
 */
public class JsonRulesTest {
    public static void main(String[] args) throws IOException {
        new RulesRunner().run(new InputStreamReader(System.in), new PrintWriter(System.out));
    }
}

class RulesRunner implements TestRunner {
    @Override
    public void run(InputStreamReader input, Writer out) throws IOException {
        JsonStreamParser p = new JsonStreamParser(input);

        // Get inputs
        JsonElement jsonEquations = p.next();
        JsonElement jsonRules = p.next();
        JsonElement jsonTurnState = p.next();

        // Deserialize
        EquationTable equations = JSONDeserializer.equationTableFromJSON(jsonEquations);
        PebbleExchangeSequence rules = JSONDeserializer.exchangeSequenceFromJson(jsonRules);
        TurnState turnState = JSONDeserializer.turnStateFromJson(jsonTurnState);

        // Execute given exchanges
        RuleBook ruleBook = new RuleBook(equations);
        Optional<TurnState> potentialAfter = ruleBook.validExchanges(turnState, rules);

        // Check if Turn was valid
        if (potentialAfter.isPresent()) {
            TurnState after = potentialAfter.get();

            // Serialize
            JsonElement jsonWallet = JSONSerializer.pebbleCollectionToJson(after.getPlayerWallet());
            JsonElement jsonBank = JSONSerializer.pebbleCollectionToJson(after.bank());

            // Output
            out.write(jsonWallet.toString());
            out.write(jsonBank.toString());
        }
        else {
            // Output
            out.write("false");
        }
        out.close();
    }
}

class RulesTester extends MilestoneIntegrationTester {

    @Override
    void runTest(InputStreamReader testInput, StringWriter testOutput) throws IOException {
        new RulesRunner().run(testInput, testOutput);
    }

    @Override
    public List<Object> jsonResultToObjects(InputStreamReader input) {

        JsonStreamParser p = new JsonStreamParser(input);
        List<Object> objects = new ArrayList<>();
        JsonElement next = p.next();
        if (next.isJsonPrimitive()) {
            objects.add(next.getAsString());
        }
        else {
            objects.add(JSONDeserializer.pebbleCollectionFromJson(next));
            objects.add(JSONDeserializer.pebbleCollectionFromJson(p.next()));
        }
        return objects;
    }
}



