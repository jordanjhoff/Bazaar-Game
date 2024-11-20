package Server;

import Common.CardPurchaseSequence;
import Common.EquationTable;
import Common.ExchangeRequest;
import Common.TurnState;
import Common.converters.BadJsonException;
import Common.converters.JSONDeserializer;
import Common.converters.JSONSerializer;
import Player.IPlayer;
import com.google.gson.*;

import java.io.*;

/**
 * Represents a proxy player, that uses input and output s
 */
public class Player implements IPlayer {
    protected String name;

    protected PrintWriter outputStream;
    protected JsonStreamParser jsonStreamIn;
    protected InputStream streamIn;

    public Player(String name, InputStream streamIn, OutputStream streamOut) throws IOException {
        this.name = name;
        outputStream = new PrintWriter(streamOut);
        this.streamIn = streamIn;
        jsonStreamIn = new JsonStreamParser(new InputStreamReader(streamIn));
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public void setup(EquationTable e) {
        JsonElement equations = JSONSerializer.equationTableToJson(e);
        write(packageFunctionCall("setup", equations));
        if (!readPlayerJSONInput(input -> input.getAsString().equals("void"))) {
            throw new PlayerException();
        }
    }

    @Override
    public ExchangeRequest requestPebbleOrTrades(TurnState turnState) {
        JsonElement ts = JSONSerializer.turnStateToJson(turnState);
        write(packageFunctionCall("request-pebble-or-trades", ts));
        return readPlayerJSONInput(JSONDeserializer::exchangeRequestFromJson);
    }

    @Override
    public CardPurchaseSequence requestCards(TurnState turnState) {
        JsonElement ts = JSONSerializer.turnStateToJson(turnState);
        write(packageFunctionCall("request-cards", ts));
        return readPlayerJSONInput(input -> new CardPurchaseSequence(JSONDeserializer.cardListFromJson(input)));
    }


    @Override
    public void win(boolean w) {
        JsonElement bool = new JsonPrimitive(w);
        write(packageFunctionCall("win", bool));
        if (!readPlayerJSONInput(input -> input.getAsString().equals("void"))) {
            throw new PlayerException();
        }
    }

    /**
     * Sends a function call to the remote player
     * @param e JsonElement containing the function call
     */
    private void write(JsonElement e) {
        outputStream.write(e.toString());
        outputStream.flush();
    }

    protected JsonElement packageFunctionCall(String funcName, JsonElement funcArg) {
        JsonArray functionCall = new JsonArray();
        functionCall.add(funcName);
        functionCall.add(funcArg);
        System.err.print("Sent to " + this.name + " :"+ functionCall);
        return functionCall;
    }

    protected <R> R readPlayerJSONInput(BadJsonFunction<JsonElement, R> applyToInput) {
        try {
            System.err.println("Waiting");
            Thread.sleep(10);
            System.err.println("Received: ");
            JsonElement reply = jsonStreamIn.next();
            System.err.println(reply);
            return applyToInput.apply(reply);
        }
        catch (InterruptedException | BadJsonException e) {
            System.err.println(e.getMessage());
            throw new PlayerException();
        }
    }

    @FunctionalInterface
    protected interface BadJsonFunction<T, R> {
        R apply(T t) throws BadJsonException;
    }
}

class PlayerException extends RuntimeException {}
