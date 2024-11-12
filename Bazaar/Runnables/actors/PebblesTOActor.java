package Runnables.actors;

import Common.EquationTable;
import Common.ExchangeRequest;
import Common.TurnState;
import Player.IStrategy;
import Player.Mechanism;

/**
 * This IPlayer implementation times out when the "request-pebble-or-trades" method is called for the nth time
 */
public class PebblesTOActor extends Mechanism {
    private int count;
    public PebblesTOActor(String name, IStrategy strategy, int n) {
        super(name, strategy);
        this.count = n;
    }

    @Override
    public ExchangeRequest requestPebbleOrTrades(TurnState turnState) {
        if (count <= 1) {
            while (true) {
                //loop forever
            }
        }
        count--;
        return super.requestPebbleOrTrades(turnState);
    }
}
