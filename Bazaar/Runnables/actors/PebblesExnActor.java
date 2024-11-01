package Runnables.actors;

import Common.ExchangeRequest;
import Common.TurnState;
import Player.IStrategy;
import Player.Mechanism;

/**
 * This IPlayer implementation throws an exception when the "requestPebbleOrTrades" method is called
 */
public class PebblesExnActor extends Mechanism {
    public PebblesExnActor(String name, IStrategy strategy) {
        super(name, strategy);
    }

    @Override
    public ExchangeRequest requestPebbleOrTrades(TurnState turnState) {
        throw new IllegalStateException("Actor throwing exception on setup");
    }
}