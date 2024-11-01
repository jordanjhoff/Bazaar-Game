package Referee;

import Common.*;
import Player.IPlayer;

import java.util.*;
/**
 * This class represents a Referee in a game of Bazaar. Currently, the referee handles players that make
 * illegal moves according to the Bazaar RuleBook.
 * <p>
 * This class currently does not handle players remote failures
 * (player timeout, DoS bug)
 * <p>
 * explicitly state what kind of abnormal interactions that referee takes care of now
 * and what kind are left to the project phase that adds in remote communication.
 */
public class Referee {

    private final Map<String, IPlayer> players;

    protected GameState theOneTrueState;
    private final RuleBook ruleBook;
    private final List<IPlayer> naughtyPlayers = new ArrayList<>();
    private final GameObjectGenerator randomizer;

    public Referee(List<IPlayer> players, RuleBook ruleBook) {
        this(players,
                new GameObjectGenerator((int)(100*Math.random())).generateStartingGamestate(players.size(), 20),
                ruleBook,
                new GameObjectGenerator((int)(100*Math.random())));
    }

    public Referee(List<IPlayer> players, GameState intermediateState, RuleBook ruleBook, GameObjectGenerator randomizer) {
        theOneTrueState = intermediateState;
        this.players = initializePlayers(players);
        this.ruleBook = ruleBook;
        this.randomizer = randomizer;
    }

    /**
     * Returns the current GameState
     * @return The GameState
     */
    public GameState getGameState() {
        return theOneTrueState;
    }

    /**
     * Runs a full game of Bazaar beginning with the current GameState
     * @return The GameResult containing the winners and players who were kicked
     */
    public GameResult runGame() {
        notifyPlayersOfStart();
        while (!ruleBook.isGameOver(this.theOneTrueState)) {
            this.theOneTrueState = executeOneTurnOnActive();
        }
        List<IPlayer> winners = getWinningPlayers();
        List<IPlayer> successfulWinners = notifyWinners(winners);
        return new GameResult(successfulWinners, naughtyPlayers);
    }

    /**
     * Initializes the players for this game, and overwrites the gamestate names to match
     * @param players The players in the game
     * @return A map from the player's name to the player
     */
    private Map<String, IPlayer> initializePlayers(List<IPlayer> players) {
        Map<String, IPlayer> playerMap = new HashMap<>();
        List<String> names = new ArrayList<>();
        for (IPlayer player : players) {
            String name = player.name();
            playerMap.put(name, player);
            names.add(name);
        }
        theOneTrueState = assignNamesToGameState(theOneTrueState, names);
        return playerMap;
    }

    /**
     * Provides all players with the equation table
     */
    private void notifyPlayersOfStart() {
        for (String name : players.keySet()) {
            IPlayer player = players.get(name);
            try {
                player.setup(this.ruleBook.equationTable());
            } catch (Exception e) {
                naughtyPlayers.add(player);
            }
        }
        theOneTrueState = kickNaughtyPlayers();
    }


    /**
     * Executes a complete single turn for the active player
     * @return The GameState after the turn
     */
    private GameState executeOneTurnOnActive() {
        GameState stateAfterExchanges;
        Optional<GameState> potentialStateAfterFirstRequest = firstPlayerRequest();
        if (potentialStateAfterFirstRequest.isPresent()) {
            stateAfterExchanges = potentialStateAfterFirstRequest.get();
        }
        else {
            return ejectActiveAndAdvanceTurn(theOneTrueState);
        }

        if (ruleBook.isGameOver(stateAfterExchanges)) {
            return stateAfterExchanges;
        }

        Optional<GameState> potentialStateAfterSecondRequest = secondPlayerRequest(stateAfterExchanges);
        if (potentialStateAfterSecondRequest.isPresent()) {
            GameState stateAfterPurchases = potentialStateAfterSecondRequest.get();
            return advancePlayers(stateAfterPurchases);
        }
        else {
            return ejectActiveAndAdvanceTurn(stateAfterExchanges);
        }
    }


    /**
     * Execute the players first request
     * @return the GameState if the request is valid, or optional if the players request is invalid, or the player throws
     * an exception
     */
    private Optional<GameState> firstPlayerRequest() {
        Optional<ExchangeRequest> playerFirstRequest = getPlayerFirstRequest(theOneTrueState.getTurnState());


        if (playerFirstRequest.isPresent() && playerFirstRequest.get() instanceof PebbleDrawRequest) {
            return executePebbleRequestOnActive();
        }
        else if (playerFirstRequest.isPresent() && playerFirstRequest.get()instanceof PebbleExchangeSequence) {
            return executeExchangesOnActive((PebbleExchangeSequence) playerFirstRequest.get());
        }
        return Optional.empty();
    }

    protected Optional<ExchangeRequest> getPlayerFirstRequest(TurnState turnState) {
        try {
            return Optional.of(getActivePlayer().requestPebbleOrTrades(theOneTrueState.getTurnState()));
        }
        catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Execute the players second request
     * @param stateAfterExchanges The GameState after the exchanges phase
     * @return the GameState if the request is valid, or optional if the players request is invalid, or the player throws
     * an exception
     */
    private Optional<GameState> secondPlayerRequest(GameState stateAfterExchanges) {
        Optional<CardPurchaseSequence> playerPurchaseRequest = getPlayerSecondRequest(stateAfterExchanges.getTurnState());
        if (playerPurchaseRequest.isPresent()) {
            return executePurchasesOnActive(stateAfterExchanges, playerPurchaseRequest.get());
        }
        else {
            return Optional.empty();
        }
    }

    protected Optional<CardPurchaseSequence> getPlayerSecondRequest(TurnState turnState) {
        try {
            return Optional.of(getActivePlayer().requestCards(turnState));
        }
        catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Determines the winners of the game after it ends, and notifies all playe
     * @return A list of all winning players
     */
    private List<IPlayer> getWinningPlayers() {
        List<String> winnerNames = ruleBook.getWinners(this.theOneTrueState)
                .stream()
                .map(p -> p.name().orElseThrow())
                .toList();
        List<IPlayer> winningPlayers = new ArrayList<>();
        for (String name : this.players.keySet()) {
            IPlayer player = players.get(name);
            if (winnerNames.contains(name)) {
                winningPlayers.add(player);
            }
        }
        return winningPlayers;
    }

    /**
     * Notifies remaining players if they won or not at the end of a game
     * Note: If a player throws an exception when notified they will no longer be considered a winner
     * @param winners The winning players of the game
     * @return The list of winners who were successfully notified of their win
     */
    private List<IPlayer> notifyWinners(List<IPlayer> winners) {
        List<IPlayer> successfulWinners = new ArrayList<>(winners);
        for (IPlayer player : this.players.values()) {
            try {
                player.win(winners.contains(player));
            } catch (Exception e) {
                successfulWinners.remove(player);
                naughtyPlayers.add(player);
            }
        }
        return successfulWinners;
    }

    /**
     * Ejects the current player from the game and changes to the next player
     * @param newState The GameState after kicking the active player
     * @return The updated GameState
     */
    private GameState ejectActiveAndAdvanceTurn(GameState newState) {
        naughtyPlayers.add(getActivePlayer());
        return ruleBook.kickActivePlayerAndAdvanceTurn(newState);
    }

    /**
     * Kicks all the current naughty players from the current GameState
     * @return The new GameState with all naughty players kicked
     */
    private GameState kickNaughtyPlayers() {
        GameState newState = theOneTrueState;
        for (IPlayer player : naughtyPlayers) {
            String name = player.name();
            newState = ruleBook.kickPlayer(newState, name);
        }
        return newState;
    }

    /**
     * Gets the active player
     * @return the active player
     */
    private IPlayer getActivePlayer() {
        PlayerInformation activePlayerInfo = this.theOneTrueState.getActivePlayer();
        return this.players.get(activePlayerInfo.name().orElseThrow());
    }

    /**
     * Uses the rulebook to advance the GameState turn
     * @param newState The GameState after the player's turn
     * @return The updated GameState
     */
    private GameState advancePlayers(GameState newState) {
        return ruleBook.advanceTurn(newState);
    }

    /**
     * Attempts to execute a PebbleDrawRequest for the active player
     * @return The updated GameState, or empty if it fails
     */
    private Optional<GameState> executePebbleRequestOnActive() {
        return ruleBook.executePebbleRequestOnGameState(theOneTrueState, randomizer);
    }

    /**
     * Attempts to execute the exchanges for the active player
     * @param sequence The exchanges to execute
     * @return The updated GameState, or empty if it fails
     */
    private Optional<GameState> executeExchangesOnActive(PebbleExchangeSequence sequence) {
        return ruleBook.validExchanges(this.theOneTrueState, sequence);
    }

    /**
     * Attempts to execute the purchases for the active player
     * @param before The GameState before the purchases
     * @param playerPurchaseRequest The cards to purchase
     * @return The updated GameState, or empty if it fails
     */
    private Optional<GameState> executePurchasesOnActive(GameState before, CardPurchaseSequence playerPurchaseRequest) {
        return ruleBook.validPurchases(before, playerPurchaseRequest);
    }

    /**
     * Assigns the names of the IPlayers in the game to the PlayerInformation in the GameState
     * @param unnamedPlayerGameState The GameState before names are assigned
     * @param names The names in the same order of the PlayerInformation in the GameState
     * @return The new GameState with names assigned
     */
    private GameState assignNamesToGameState(GameState unnamedPlayerGameState, List<String> names) {
        if (names.size() != unnamedPlayerGameState.players().size()) {
            throw new IllegalArgumentException("Number of names does not match");
        }
        List<PlayerInformation> newPlayerInfos = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            PlayerInformation oldPlayerInfo = unnamedPlayerGameState.players().get(i);
            PlayerInformation newPlayerInfo = new PlayerInformation(names.get(i), oldPlayerInfo.wallet(), oldPlayerInfo.score());
            newPlayerInfos.add(newPlayerInfo);
        }
        return new GameState(unnamedPlayerGameState.bank(), unnamedPlayerGameState.cards(), newPlayerInfos);
    }
}
