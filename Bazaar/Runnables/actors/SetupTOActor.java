package Runnables.actors;

import Common.EquationTable;
import Player.IStrategy;
import Player.Mechanism;


/**
 * This IPlayer implementation times out when the "setup" method is called for the nth time
 */
public class SetupTOActor extends Mechanism {
    int count;
    public SetupTOActor(String name, IStrategy strategy, int n) {
        super(name, strategy);
        this.count = n;
    }

    @Override
    public void setup(EquationTable e) {
        if (count <= 1) {
            while (true) {
                //loop forever
            }
        }
        count--;
        super.setup(e);
    }
}
