package Runnables.actors;

import Common.Equation;
import Common.EquationTable;
import Common.ExchangeRule;
import Common.PebbleCollection;
import Player.IStrategy;
import Player.Mechanism;

import java.util.Optional;

public class AbstractCantTradeActor extends Mechanism {
    protected EquationTable equations;
    public AbstractCantTradeActor(String name, IStrategy strategy) {
        super(name, strategy);
    }

    @Override
    public void setup(EquationTable equations) {
        this.equations = equations;
        super.setup(equations);
    }


    /**
     * Returns the best cheat, where an gets a rule that can't be used by the givel pebbles
     * @param pebbles
     * @return
     */
    protected Optional<ExchangeRule> getBestCheat(PebbleCollection pebbles) {
        Optional<ExchangeRule> bestCheat = Optional.empty();
        mainLoop:
        for (Equation eq : equations.equationSet()) {
            for (ExchangeRule rule : eq.getRules()) {
                if (!pebbles.contains(rule.getInputPebbles())) {
                    bestCheat = Optional.of(rule);
                    break mainLoop;
                }
                if (!pebbles.contains(rule.getOutputPebbles())) {
                    bestCheat = Optional.of(rule);
                    break mainLoop;
                }
            }
        }
        return bestCheat;
    }
}
