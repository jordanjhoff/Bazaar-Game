package Runnables.actors;

import Player.IStrategy;
import Player.Mechanism;

/**
 * This IPlayer implementation throws an exception when the "win" method is called
 */
public class WinExnActor extends Mechanism {
    int count;
    public WinExnActor(String name, IStrategy strategy, int count) {
        super(name, strategy);
        this.count = count;
    }

    @Override
    public void win(boolean win) {
        if (--count == 0)
            throw new IllegalStateException("Actor throwing exception on setup");
        super.win(win);
    }
}