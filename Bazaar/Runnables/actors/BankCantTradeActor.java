package Runnables.actors;

import Common.*;
import Player.IStrategy;
import Player.Mechanism;

import java.util.Optional;

/**
 * This IPlayer implementation cheats by making an exchange that the bank can't afford.
 */
public class BankCantTradeActor extends Mechanism {
    protected EquationTable equations;
    public BankCantTradeActor(String name, IStrategy strategy) {
        super(name, strategy);
    }

    @Override
    public ExchangeRequest requestPebbleOrTrades(TurnState turnState) {
        Optional<ExchangeRule> bestCheat = getBestBankCheat(turnState.bank());
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
     * Returns the best cheat, where an gets a rule that can't be used by the givel pebbles
     * @param bank
     * @return
     */
    protected Optional<ExchangeRule> getBestBankCheat(PebbleCollection bank) {
        Optional<ExchangeRule> bestCheat = Optional.empty();
        mainLoop:
        for (Equation eq : equations.equationSet()) {
            for (ExchangeRule rule : eq.getRules()) {
                if (!bank.contains(rule.getOutputPebbles())) {
                    bestCheat = Optional.of(rule);
                    break mainLoop;
                }
            }
        }
        return bestCheat;
    }

}
