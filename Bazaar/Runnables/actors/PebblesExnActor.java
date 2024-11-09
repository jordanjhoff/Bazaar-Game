package Runnables.actors;

import Common.ExchangeRequest;
import Common.TurnState;
import Player.IStrategy;
import Player.Mechanism;

/**
 * This IPlayer implementation throws an exception when the "requestPebbleOrTrades" method is called
 */
public class PebblesExnActor extends Mechanism {
    int count;
    public PebblesExnActor(String name, IStrategy strategy, int count) {
        super(name, strategy);
        this.count = count;
    }

    @Override
    public ExchangeRequest requestPebbleOrTrades(TurnState turnState) {
        if (--count == 0)
            throw new IllegalStateException("Actor throwing exception on setup");
        return super.requestPebbleOrTrades(turnState);
    }
}