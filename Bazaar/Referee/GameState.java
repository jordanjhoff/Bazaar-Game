package Referee;

import java.util.*;

import Common.*;
import Common.CardDeck;

/**
 * Represents the state of a Bazaar game.
 *
 * @param players Active player is always at the front of players
 */
public record GameState(PebbleCollection bank, CardDeck cards, List<PlayerInformation> players) {

  public GameState(PebbleCollection bank, CardDeck cards, List<PlayerInformation> players) {
    this.bank = Objects.requireNonNull(bank);
    this.cards = Objects.requireNonNull(cards);
    this.players = Objects.requireNonNull(players)
            .stream()
            .map(Objects::requireNonNull)
            .toList();
  }

  /**
   * Gets the turn state for the active player
   *
   * @return A TurnState for the active player
   */
  public TurnState getTurnState() {
    if (this.players.isEmpty()) {
      throw new IllegalStateException("No turn state");
    }
    List<Integer> otherPlayerScores = this.getScoresForOthers();
    return new TurnState(this.bank, getActivePlayer(), otherPlayerScores, this.cards.visibleCards());
  }

  public PlayerInformation getActivePlayer() {
    return this.players.getFirst();
  }

  /**
   * Returns the list of all player scores
   * @return The scores for all players currently in the game
   */
  public List<Integer> getScores() {
    List<Integer> scores = new ArrayList<>();
    for (PlayerInformation playerInformation : this.players) {
      scores.add(playerInformation.score());
    }
    return scores;
  }

  /**
   * Returns a list of scores for all other players than the active player
   *
   * @return A list of integers representing the scores
   */
  private List<Integer> getScoresForOthers() {
    List<Integer> scores = getScores();
    scores.removeFirst();
    return scores;
  }
}
