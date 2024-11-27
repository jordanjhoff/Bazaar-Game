package Common.converters;

import Common.*;

import java.util.List;
import java.util.function.Function;

public enum Bonus {
    RWB("RWB", BonusFunctions.redwhiteblue()),
    SEY("SEY", BonusFunctions.seychelles());

    private final String bonusName;
    private final Function<PlayerInformation, PlayerInformation> bonusFunction;

    Bonus(String bonusName, Function<PlayerInformation, PlayerInformation> bonusFunction) {
        this.bonusName = bonusName;
        this.bonusFunction = bonusFunction;
    }

    public static Bonus fromString(String name) throws BadJsonException {
        for (Bonus bonus : Bonus.values()) {
            if (bonus.bonusName.equalsIgnoreCase(name)) {
                return bonus;
            }
        }
        throw new BadJsonException("Invalid bonus name " + name);
    }

    public String toString() {
        return bonusName;
    }

    public Function<PlayerInformation, PlayerInformation> getBonusFunction() {
        return bonusFunction;
    }

}


class BonusFunctions {

    private static final int SEYBONUS = 50;
    private static final int RWBBONUS = 10;
    public static Function<PlayerInformation, PlayerInformation> seychelles() {
        return player -> {
            PebbleCollection allColors = collectAllColors(player.purchases());
            if (allColors.contains(Pebble.BLUE)
                    && allColors.contains(Pebble.RED)
                    && allColors.contains(Pebble.WHITE)
                    && allColors.contains(Pebble.GREEN)
                    && allColors.contains(Pebble.YELLOW)) {
                return new PlayerInformation(player.name(),
                        player.wallet(),
                        player.score() + SEYBONUS,
                        player.purchases());
            }
            else {
                return player;
            }
        };
    }


    public static Function<PlayerInformation, PlayerInformation> redwhiteblue() {
        return player -> {
            PebbleCollection allColors = collectAllColors(player.purchases());
            if (allColors.contains(Pebble.BLUE)
                    && allColors.contains(Pebble.RED)
                    && allColors.contains(Pebble.WHITE)) {
                return new PlayerInformation(player.name(),
                        player.wallet(),
                        player.score() + RWBBONUS,
                        player.purchases());
            }
            else {
                return player;
            }
        };
    }

    private static PebbleCollection collectAllColors(List<CardPurchaseSequence> purchases) {
        PebbleCollection allColors = new PebbleCollection();
        for (CardPurchaseSequence cardPurchaseSequence : purchases) {
            for (Card card : cardPurchaseSequence.cards()) {
                allColors = allColors.add(card.pebbles());
            }
        }
        return allColors;
    }
}
