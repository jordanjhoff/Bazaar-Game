package Runnables.actors;

import Common.EquationTable;
import Player.IStrategy;
import Player.Mechanism;

/**
 * This IPlayer implementation throws an exception when the "setup" method is called
 */
public class SetupExnActor extends Mechanism {
    int count;
    public SetupExnActor(String name, IStrategy strategy, int count) {
        super(name, strategy);
        this.count = count;
    }

    @Override
    public void setup(EquationTable equations) {
        if (--count == 0)
            throw new IllegalStateException("Actor throwing exception on setup");
        super.setup(equations);
    }
}
