package Runnables.actors;

import Common.*;
import Player.IStrategy;
import Player.Mechanism;

import java.util.Optional;

/**
 * This IPlayer implementation cheats by making an exchange that the bank can't afford.
 */
public class BankCantTradeActor extends AbstractCantTradeActor {
    public BankCantTradeActor(String name, IStrategy strategy) {
        super(name, strategy);
    }

    @Override
    public ExchangeRequest requestPebbleOrTrades(TurnState turnState) {
        Optional<ExchangeRule> bestCheat = getBestCheat(turnState.bank());
        if (bestCheat.isPresent()) {
            return new PebbleExchangeSequence(bestCheat.get());
        }
        else {
            return super.requestPebbleOrTrades(turnState);
        }
    }

}
