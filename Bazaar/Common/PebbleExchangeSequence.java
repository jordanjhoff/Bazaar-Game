package Common;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A record of a sequence of exchanges using ExchangeRules
 */
public record PebbleExchangeSequence(List<ExchangeRule> rules) implements ExchangeRequest {
    public PebbleExchangeSequence(List<ExchangeRule> rules) {
        this.rules = Objects.requireNonNull(rules).stream().map(Objects::requireNonNull).toList();
    }

    public PebbleExchangeSequence(ExchangeRule... rules) {
        this(List.of(rules));
    }

    /**
     * Adds a Rule to this sequence and returns the new sequence
     * @param rule The Rule to add
     * @return The sequence with the added Rule
     */
    public PebbleExchangeSequence addExchange(ExchangeRule rule) {
        List<ExchangeRule> newRules = new ArrayList<>(this.rules);
        newRules.add(rule);
        return new PebbleExchangeSequence(newRules);
    }
}

