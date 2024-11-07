package Runnables.actors;

import Common.ExchangeRequest;
import Common.ExchangeRule;
import Common.PebbleExchangeSequence;
import Common.TurnState;
import Player.IStrategy;
import Player.Mechanism;

import java.util.Optional;

/**
 * This IPlayer implementation cheats by making an exchange that the player can't afford.
 */
public class WalletCantTradeActor extends AbstractCantTradeActor {

    public WalletCantTradeActor(String name, IStrategy strategy) {
        super(name, strategy);
    }

    @Override
    public ExchangeRequest requestPebbleOrTrades(TurnState turnState) {
        Optional<ExchangeRule> bestCheat = getBestCheat(turnState.getPlayerWallet());
        if (bestCheat.isPresent()) {
            return new PebbleExchangeSequence(bestCheat.get());
        }
        else {
            return super.requestPebbleOrTrades(turnState);
        }
    }
}
