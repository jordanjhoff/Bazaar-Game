package Runnables.actors;

import Common.ExchangeRequest;
import Common.ExchangeRule;
import Common.PebbleExchangeSequence;
import Common.TurnState;
import Player.IStrategy;

import java.util.Optional;

public class WalletCantTradeActor extends BankCantTradeActor {

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
