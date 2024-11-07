package Runnables.actors;

import Common.*;
import Player.IStrategy;
import Player.Mechanism;

import java.util.Optional;

/**
 * This IPlayer implementation cheats by making an exchange that the bank can't afford.
 */
public class BankCantTradeActor extends Mechanism {
    private EquationTable equations;
    public BankCantTradeActor(String name, IStrategy strategy) {
        super(name, strategy);
    }

    @Override
    public void setup(EquationTable equations) {
        this.equations = equations;
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

    protected Optional<ExchangeRule> getBestCheat(PebbleCollection pebbles) {
        ExchangeRule bestCheat = null;
        mainLoop:
        for (Equation eq : equations.equationSet()) {
            for (ExchangeRule rule : eq.getRules()) {
                if (!pebbles.contains(rule.getInputPebbles())) {
                    bestCheat = rule;
                    break mainLoop;
                }
                if (!pebbles.contains(rule.getOutputPebbles())) {
                    bestCheat = rule;
                    break mainLoop;
                }
            }
        }
        return Optional.ofNullable(bestCheat);
    }
}
