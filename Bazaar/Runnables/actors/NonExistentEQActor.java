package Runnables.actors;

import Common.*;
import Player.IStrategy;
import Player.Mechanism;
import Referee.GameObjectGenerator;

import java.util.List;

/**
 * This IPlayer implementation cheats by using a non-existent equation.
 */
public class NonExistentEQActor extends Mechanism {
    private EquationTable equationTable;
    public NonExistentEQActor(String name, IStrategy strategy) {
        super(name, strategy);
    }

    @Override
    public void setup(EquationTable equations) {
        equationTable = equations;
    }

    @Override
    public ExchangeRequest requestPebbleOrTrades(TurnState turnState) {
        GameObjectGenerator generator = new GameObjectGenerator();
        ExchangeRule nonexistingeq;
        // todo: fix. relying on random generation is bad per 11/13 codewalk
        do {
            nonexistingeq = generator.generateRandomRule();
        }
        while (equationTable.containsRule(nonexistingeq));
        return new PebbleExchangeSequence(nonexistingeq);
    }
}
