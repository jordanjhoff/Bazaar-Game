package Common;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A record of information about a player
 * @param name The player's distinct name, or empty if it is an unnamed player
 * @param wallet The player's wallet
 * @param score  The player's current score
 */
public record PlayerInformation(Optional<String> name, PebbleCollection wallet, int score, CardPurchaseSequence purchases) {
    public PlayerInformation {
        Objects.requireNonNull(name);
        Objects.requireNonNull(wallet);
        Objects.requireNonNull(purchases);
    }

    public PlayerInformation(Optional<String> name, PebbleCollection wallet, int score) {
        this(name, wallet, score, new CardPurchaseSequence());
    }

    public PlayerInformation(String name, PebbleCollection wallet, int score) {
        this(Optional.of(name), wallet, score, new CardPurchaseSequence());
    }

    public PlayerInformation(PebbleCollection wallet, int score) {
        this(Optional.empty(), wallet, score, new CardPurchaseSequence());
    }
}
