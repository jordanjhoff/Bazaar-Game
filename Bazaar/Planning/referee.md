__Memorandum__

TO: The CEOs  \
FROM: Educational parrots\
DATE: October 17, 2024  \
SUBJECT: Referee protocol design

__GameState interface__

We have decided to represent our GameState as an immutable record. \
This means that any "modification" methods return a new GameState instance with updated values. \
The interface and methods can be seen below.
```java
public interface GameState {
 
    GameState getGameStateAfterExchanges(RuleBook ruleBook, PebbleExchangeSequence exchanges);
    GameState getGameStateAfterDrawPebble(RuleBook ruleBook);
    GameState getGameStateAfterPurchases(RuleBook ruleBook, CardPurchaseSequence purchases);
    GameState kickActivePlayerAndAdvanceTurn(RuleBook ruleBook);
    GameState advanceTurnToNextPlayer(RuleBook ruleBook);
    boolean isGameOver(RuleBook ruleBook);
    List<PlayerInformation> getWinners(RuleBook ruleBook);
    List<PlayerInformation> getLosers(RuleBook ruleBook);
    TurnState getTurnState();
    PlayerInformation getActivePlayer();
}
```

__Protocol Diagram__  
The Referee will maintain a GameState field, and interact with the various GameState methods. Below is the 
sequence diagram of Referee calls to GameState methods.

_Setup game_  
In this phase, the `Referee` will set up the initial `GameState` using the game object generator.
```
Referee                                                       GameState
|                                                                 |
| ----------------------new GameState---------------------------> | (Constructs)
|                                                                 |
```

_Playing/Making turns_  
In this phase, for each turn the `Referee` will request the `TurnState` from the `GameState` to send to the `Player`. 
The `Referee` will then request the `GameState` to update based upon the `Player`'s responses.
```
Referee                                                       GameState
|                                                                 |
| ------------------------getTurnState()------------------------> | (Request)
| <-----------------------new TurnState-------------------------- | (Return)
|                                                                 |
        OPTION 1: the Player sends a list of exchanges          
|                                                                 |
| -------------getGameStateAfterExchanges(exchanges)------------> | (Request)
|                                                                 |
|                If the exchanges are valid:                      |
|<----------------------new GameState---------------------------- | (Return)
|                                                                 |
|                 If the exchanges are invalid:                   |
| <---------------IllegalArgumentException----------------------- | (Throws)
| -------------------kickAndAdvanceTurn()-----------------------> | (Request)
| <---------------------new GameState---------------------------- | (Return)
|                   SKIP TO GAME OVER CHECK                       |
|                                                                 |
         OPTION 2: the Player requests to draw a pebble         
|                                                                 |
| ----------------getGameStateAfterDrawPebble()-----------------> | (Request)
| <---------------------new PebbleColor-------------------------- | (Return)
|                                                                 |
```
After the exchange phase of the turn is over and if the exchanges were valid 
the `Referee` requests an updated `TurnState` and continues to the `Player`'s purchases.
```
Referee                                                         GameState
|                                                                 |
| ------------------------getTurnState()------------------------> | (Request)
| <-----------------------new TurnState-------------------------- | (Return)
|                                                                 |
| ------------------executePurchases(purchases)-----------------> | (Request)
|                                                                 |
                   If the purchases are invalid:
|                                                                 |
| <--------------------IllegalArgumentException------------------ | (Throws)
| ----------------kickActivePlayerAndAdvanceTurn()--------------> | (Request)
| <-------------------------new GameState------------------------ | (Return)
|                     SKIP TO GAME OVER CHECK                     |
|                                                                 |
                   If the purchases are valid:
|                                                                 |
| <-----------------------new GameState-------------------------- | (Return)
| ------------------advanceTurnToNextPlayer()-------------------> | (Request)
| <-----------------------new GameState-------------------------- | (Return)
|                                                                 |
|                        GAME OVER CHECK                          |
| -------------------------isGameOver()-------------------------> |
| <---------------------------boolean---------------------------- | (Return)
|                                                                 |
                 OPTION 1: isGameOver() is false:
|                                                                 |
|           SEQUENCE DIAGRAM LOOPS FROM PLAYING TURNS             |
              
                 OPTION 2: isGameOver() is true:
|                                                                 |          
|                         GAME ENDS                               |                 
| -------------------------getWinners()-------------------------> | (Request)
| <-------------------List<PlayerInformation>-------------------- | (Return)
| -------------------------getLosers()--------------------------> | (Request)
| <-------------------List<PlayerInformation>-------------------- | (Return)
|                                                                 |
```
`Referee` repeats this sequence for each `Player`'s turn until `isGameOver()` returns true.



