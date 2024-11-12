package Runnables.actors;

import Player.IStrategy;
import Player.Mechanism;

/**
 * This IPlayer implementation times out when the "win" method is called for the nth time
 */
public class WinTOActor extends Mechanism {
    private int count;
    public WinTOActor(String name, IStrategy strategy, int n) {
        super(name, strategy);
        this.count = n;
    }

    @Override
    public void win(boolean w) {
        if (count <= 1) {
            while (true) {
                //loop forever
            }
        }
        count--;
        super.win(w);
    }
}
