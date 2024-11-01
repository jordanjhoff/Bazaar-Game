package UnitTests;

import Common.*;
import Common.converters.JSONDeserializer;
import Common.converters.JSONSerializer;
import Player.IPlayer;
import Referee.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MockReferee extends Referee {
    private final StringWriter log;
    public MockReferee(List<IPlayer> players, GameState intermediateState, RuleBook ruleBook, GameObjectGenerator randomizer, StringWriter log) {
        super(players, intermediateState, ruleBook, randomizer);
        this.log = log;
    }

    @Override
    public GameResult runGame() {
        return super.runGame();
    }

    @Override
    protected Optional<ExchangeRequest> getPlayerFirstRequest(TurnState turnState) {
        log.write(JSONSerializer.turnStateToJson(turnState) + "\n");
        log.write(theOneTrueState.getActivePlayer().name() + "'s turn. ----------------------------------\n");
        Optional<ExchangeRequest> result = super.getPlayerFirstRequest(turnState);
        if (result.isPresent()) {
            if (result.get() instanceof PebbleDrawRequest) {
                log.write(theOneTrueState.getActivePlayer().name() + " requested to draw. \n");
            }
            else {
                log.write(theOneTrueState.getActivePlayer().name() + " requested to exchange. \n");
                log.write(JSONSerializer.exchangeSequenceToJson((PebbleExchangeSequence) result.get()) + "\n");
            }
        }
        else {
            log.write(theOneTrueState.getActivePlayer().name() + " was naughty. \n");
        }
        return result;
    }

    @Override
    protected Optional<CardPurchaseSequence> getPlayerSecondRequest(TurnState turnState) {
        Optional<CardPurchaseSequence> result = super.getPlayerSecondRequest(turnState);
        if (result.isPresent()) {
            log.write(theOneTrueState.getActivePlayer().name() + " requested to buy. \n");
            log.write(JSONSerializer.cardListToJson(result.get().cards()) + "\n");
        }
        else {
            log.write(theOneTrueState.getActivePlayer().name() + " was naughty. \n");
        }
        return result;
    }
}
