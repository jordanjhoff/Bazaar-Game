package Runnables.actors;

import Common.*;
import Player.IStrategy;
import Player.Mechanism;

import java.util.Optional;

/**
 * This IPlayer implementation cheats by making an exchange that the player can't afford.
 */
public class WalletCantTradeActor extends Mechanism {
    protected EquationTable equations;

    public WalletCantTradeActor(String name, IStrategy strategy) {
        super(name, strategy);
    }

    @Override
    public ExchangeRequest requestPebbleOrTrades(TurnState turnState) {
        Optional<ExchangeRule> bestCheat = getBestWalletCantTradeCheat(turnState.getPlayerWallet());
        if (bestCheat.isPresent()) {
            return new PebbleExchangeSequence(bestCheat.get());
        }
        else {
            return super.requestPebbleOrTrades(turnState);
        }
    }


    @Override
    public void setup(EquationTable equations) {
        this.equations = equations;
        super.setup(equations);
    }


    /**
     * Returns the best cheat, where it gets a rule that can't be used by the wallet
     * @param wallet
     */
    protected Optional<ExchangeRule> getBestWalletCantTradeCheat(PebbleCollection wallet) {
        for (Equation eq : equations.equationSet()) {
            for (ExchangeRule rule : eq.getRules()) {
                if (!wallet.contains(rule.getInputPebbles())) {
                    return Optional.of(rule);
                }
            }
        }
        return Optional.empty();
    }

}
