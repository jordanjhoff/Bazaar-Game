package Runnables.actors;

import Common.EquationTable;
import Player.IStrategy;
import Player.Mechanism;

/**
 * This IPlayer implementation throws an exception when the "setup" method is called
 */
public class SetupExnActor extends Mechanism {
    public SetupExnActor(String name, IStrategy strategy) {
        super(name, strategy);
    }

    @Override
    public void setup(EquationTable equations) {
        throw new IllegalStateException("Actor throwing exception on setup");
    }
}
