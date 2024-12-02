package Common.converters;

import Common.*;

import java.util.Arrays;
import java.util.function.Function;

/**
 *
 */
public enum Bonus {
    // used for deserialization
    RWB("RWB", BonusFunctions.redwhiteblue()),
    SEY("SEY", BonusFunctions.seychelles());

    private final String bonusName;
    private final Function<PlayerInformation, PlayerInformation> bonusFunction;

    Bonus(String bonusName, Function<PlayerInformation, PlayerInformation> bonusFunction) {
        this.bonusName = bonusName;
        this.bonusFunction = bonusFunction;
    }

    public static Bonus fromString(String name) throws BadJsonException {
        for (Bonus bonus : Bonus.values())
            if (bonus.bonusName.equalsIgnoreCase(name))
                return bonus;

        throw new BadJsonException("Invalid bonus name " + name);
    }

    public String toString() {
        return bonusName;
    }

    public Function<PlayerInformation, PlayerInformation> getFunction() {
        return bonusFunction;
    }

}

/**
 * Defines the available Bonus Functions for
 */
class BonusFunctions {

    /**
     * Seychelles
     */
    private static final int SEYBONUS = 50;
    private static final Pebble[] SEYCOLORS =
            new Pebble[]{Pebble.BLUE, Pebble.RED, Pebble.GREEN, Pebble.YELLOW, Pebble.WHITE};

    public static Function<PlayerInformation, PlayerInformation> seychelles() {
        return collectedColorsBonus(SEYCOLORS, SEYBONUS);
    }

    /**
     * USA :eagle: :boom: :bicep:
     */
    private static final int RWBBONUS = 10;
    private static final Pebble[] RWBCOLORS =
            new Pebble[]{Pebble.BLUE, Pebble.RED, Pebble.WHITE};

    public static Function<PlayerInformation, PlayerInformation> redwhiteblue() {
        return collectedColorsBonus(RWBCOLORS, RWBBONUS);
    }

    /**
     * @return the updated PlayerInformation, with the bonus applied, iff the player has purchased
     * cards collectively containing the pebble colors provided.
     */
    public static Function<PlayerInformation, PlayerInformation> collectedColorsBonus(Pebble[] colors, int bonus) {
        return player -> {
            PebbleCollection allColors = collectAllColors(player.purchases());
            int newScore = player.score();
            if (Arrays.stream(colors).allMatch(allColors::contains))
                newScore += bonus;
            return new PlayerInformation(player.name(), player.wallet(), newScore, player.purchases());
        };
    }

    /**
     * @return the sum of all cards' PebbleCollections
     */
    private static PebbleCollection collectAllColors(CardPurchaseSequence purchases) {
        PebbleCollection allColors = new PebbleCollection();
        for (Card purchase : purchases.cards()) {
            allColors = allColors.add(purchase.pebbles());
        }
        return allColors;
    }
}
