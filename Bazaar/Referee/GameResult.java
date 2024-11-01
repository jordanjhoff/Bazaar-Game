package Referee;

import java.util.List;

import Player.IPlayer;

/**
 * A record containing the players that won the Bazaar game, and the players that misbehaved.
 * @param winners The players who won the game
 * @param kicked The players who misbehaved and were kicked
 */
public record GameResult(List<IPlayer> winners, List<IPlayer> kicked) {}