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
import java.util.logging.Logger;

/**
 * Represents a proxy player, that uses input and output s
 */
public class Player implements IPlayer {
    protected String name;
    protected Logger log;
    protected PrintWriter outputStream;
    protected JsonStreamParser jsonStreamIn;
    protected InputStream streamIn;

    public Player(String name, InputStream streamIn, OutputStream streamOut, Logger log) throws IOException {
        this.log = log;
        this.name = name;
        outputStream = new PrintWriter(streamOut);
        this.streamIn = streamIn;
        jsonStreamIn = new JsonStreamParser(new InputStreamReader(streamIn));
    }

    public Player(String name, InputStream streamIn, OutputStream streamOut) throws IOException {
        this(name, streamIn, streamOut, Logger.getLogger(Player.class.getName()));
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public void setup(EquationTable e) {
        JsonElement equations = JSONSerializer.equationTableToJson(e);
        sendToClient(packageFunctionCall("setup", equations));
        if (!readPlayerJSONInput(input -> input.getAsString().equals("void"))) {
            throw new PlayerException();
        }
    }

    @Override
    public ExchangeRequest requestPebbleOrTrades(TurnState turnState) {
        JsonElement ts = JSONSerializer.turnStateToJson(turnState);
        sendToClient(packageFunctionCall("request-pebble-or-trades", ts));
        return readPlayerJSONInput(JSONDeserializer::exchangeRequestFromJson);
    }

    @Override
    public CardPurchaseSequence requestCards(TurnState turnState) {
        JsonElement ts = JSONSerializer.turnStateToJson(turnState);
        sendToClient(packageFunctionCall("request-cards", ts));
        return readPlayerJSONInput(input -> new CardPurchaseSequence(JSONDeserializer.cardListFromJson(input)));
    }


    @Override
    public void win(boolean w) {
        JsonElement bool = new JsonPrimitive(w);
        sendToClient(packageFunctionCall("win", bool));
        if (!readPlayerJSONInput(input -> input.getAsString().equals("void"))) {
            throw new PlayerException();
        }
    }

    protected JsonElement packageFunctionCall(String funcName, JsonElement funcArg) {
        JsonArray functionCall = new JsonArray();
        functionCall.add(funcName);
        functionCall.add(funcArg);
        log.info("Sent to " + this.name + " :"+ functionCall);
        return functionCall;
    }

    protected void sendToClient(JsonElement message) {
        outputStream.println(message);
        outputStream.flush();
    }

    protected <R> R readPlayerJSONInput(BadJsonFunction<JsonElement, R> applyToInput) {
        try {
            Thread.sleep(10);
            JsonElement reply = jsonStreamIn.next();
            log.info("Received from " + this.name + ": " + reply.toString());
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

class PlayerException extends RuntimeException {
    public PlayerException(String message) {
        super(message);
    }
    public PlayerException() {
        super();
    }
}
