package Common;

import java.util.Objects;
import java.util.Optional;

/**
 * A record of information about a player
 * @param name The player's distinct name, or empty if it is an unnamed player
 * @param wallet The player's wallet
 * @param score  The player's current score
 */
public record PlayerInformation(Optional<String> name, PebbleCollection wallet, int score) {
    public PlayerInformation {
        Objects.requireNonNull(name);
        Objects.requireNonNull(wallet);
    }

    public PlayerInformation(String name, PebbleCollection wallet, int score) {
        this(Optional.of(name), wallet, score);
    }

    public PlayerInformation(PebbleCollection wallet, int score) {
        this(Optional.empty(), wallet, score);
    }
}
