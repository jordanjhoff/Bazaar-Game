package Runnables.actors;

import Player.IStrategy;
import Player.Mechanism;

/**
 * This IPlayer implementation throws an exception when the "win" method is called
 */
public class WinExnActor extends Mechanism {
    public WinExnActor(String name, IStrategy strategy) {
        super(name, strategy);
    }

    @Override
    public void win(boolean win) {
        throw new IllegalStateException("Actor throwing exception on setup");
    }
}