package Common;

import java.util.List;
import java.util.Objects;

/**
 * A record class that represents a snapshot of the information of a current game, to be delivered to a single player.
 * @param bank
 * @param activePlayer
 * @param scores
 * @param visibleCards
 */
public record TurnState(PebbleCollection bank, PlayerInformation activePlayer, List<Integer> scores,
                        List<Card> visibleCards) {
    public TurnState(PebbleCollection bank, PlayerInformation activePlayer, List<Integer> scores,
                     List<Card> visibleCards) {
        this.bank = Objects.requireNonNull(bank);
        this.activePlayer = Objects.requireNonNull(activePlayer);
        this.scores = Objects.requireNonNull(scores).stream().map(Objects::requireNonNull).toList();
        this.visibleCards = Objects.requireNonNull(visibleCards).stream().map(Objects::requireNonNull).toList();
    }

    /**
     * Gets the active player's wallet for this TurnState
     * @return The active player's wallet
     */
    public PebbleCollection getPlayerWallet() {
        return this.activePlayer.wallet();
    }

    /**
     * Gets the active player's score for this TurnState
     * @return The active player's score
     */
    public int getPlayerScore() { return this.activePlayer.score(); }
}
