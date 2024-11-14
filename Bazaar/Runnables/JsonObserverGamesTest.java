package Runnables;

import Common.EquationTable;
import Common.RuleBook;
import Common.converters.BadJsonException;
import Common.converters.JSONDeserializer;
import Common.converters.JSONSerializer;
import Player.IPlayer;
import Referee.GameResult;
import Referee.GameState;
import Referee.*;
import UnitTests.DeterministicObjectGenerator;
import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a testing class for sending and receiving JSON values
 */
public class JsonObserverGamesTest {
    public static void main(String[] args) throws IOException, BadJsonException {
        if (args.length == 1 && args[0].equals("--show")) {
            new ObserverGamesRunner(true).run(new InputStreamReader(System.in), new PrintWriter(System.out));
        }
        else {
            new ObserverGamesRunner().run(new InputStreamReader(System.in), new PrintWriter(System.out));
        }

    }
}

class ObserverGamesRunner extends GamesRunner {

    protected ObservableReferee referee;
    protected boolean withObserver = false;


    public ObserverGamesRunner() {
    }

    public ObserverGamesRunner(boolean withObserver) {
        this.withObserver = withObserver;
    }


    @Override
    protected GameResult runGame(List<IPlayer> players, GameState gameState, RuleBook ruleBook) throws IOException {
        referee = new ObservableReferee(players, gameState, ruleBook, new DeterministicObjectGenerator());
        addObserversToRef();
        return referee.runGame();
    }

    protected void addObserversToRef() {
        if (withObserver) {
            referee.addListener(new Observer());
        }
    }


}

class ObserverGamesTester extends GamesTester {
    @Override
    void runTest(InputStreamReader testInput, StringWriter testOutput) throws IOException, BadJsonException {
        new ObserverGamesRunner().run(testInput, testOutput);
    }
}
