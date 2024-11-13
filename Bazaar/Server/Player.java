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
        outputStream.write(packageFunctionCall("setup", equations));
        outputStream.flush();
        if (!readPlayerJSONInput(input -> input.getAsString().equals("void"))) {
            throw new PlayerException();
        }
    }

    @Override
    public ExchangeRequest requestPebbleOrTrades(TurnState turnState) {
        JsonElement ts = JSONSerializer.turnStateToJson(turnState);
        outputStream.write(packageFunctionCall("request-pebble-or-trades", ts));
        outputStream.flush();
        return readPlayerJSONInput(JSONDeserializer::exchangeRequestFromJson);
    }

    @Override
    public CardPurchaseSequence requestCards(TurnState turnState) {
        JsonElement ts = JSONSerializer.turnStateToJson(turnState);
        outputStream.write(packageFunctionCall("request-cards", ts));
        outputStream.flush();
        return readPlayerJSONInput(input -> new CardPurchaseSequence(JSONDeserializer.cardListFromJson(input)));
    }


    @Override
    public void win(boolean w) {
        JsonElement bool = new JsonPrimitive(w);
        outputStream.write(packageFunctionCall("win", bool));
        outputStream.flush();
        if (!readPlayerJSONInput(input -> input.getAsString().equals("void"))) {
            throw new PlayerException();
        }
    }

    protected String packageFunctionCall(String funcName, JsonElement funcArg) {
        JsonArray functionCall = new JsonArray();
        functionCall.add(funcName);
        functionCall.add(funcArg);
        System.out.println("Sent: " +functionCall);
        return functionCall.toString();
    }

    protected <R> R readPlayerJSONInput(BadJsonFunction<JsonElement, R> applyToInput) {
        try {
            while (streamIn.available() == 0) {
                Thread.sleep(10);
            }
            if (jsonStreamIn.hasNext()) {
                JsonElement reply = jsonStreamIn.next();
                System.out.println("Received: " + reply);
                return applyToInput.apply(reply);
            }
        }
        catch (InterruptedException | BadJsonException e) {
            throw new PlayerException();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        throw new PlayerException();
    }

    @FunctionalInterface
    protected interface BadJsonFunction<T, R> {
        R apply(T t) throws BadJsonException;
    }


}



class PlayerException extends RuntimeException {}
