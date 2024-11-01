__Memorandum__

TO: The CEOs  
FROM: Educational parrots
DATE: October 23, 2024  
SUBJECT: Game observer mechanism

__Goals__ 

We want to create an observer on a Referee, to observe the current state of the game. The Referee will maintain a list
of `BazaarObserver`, with the new added functionality of `addObserver(BazaarObserver observer)`. We have designed our
`Referee` to maintain an immutable `GameState`, so whenever this value gets updated (after a turn has been completed), 
the referee notifies the `BazaarObserver`. The `BazaarObserver` will maintain its own immutable `GameState`, and anytime a 
user wishes to view the `GameState`, the observer can provide the necessary information. 

__Operations Wishlist__

```java
public class Referee {
    /**
     * Join Referee's observer list  
     * @param observer
     */
    void addObserver(BazaarObserver observer);
}
```
```java
public class BazaarObserver {

    /**
     * Notify the observer of an updated gamestate
     * @param gameState
     */
    void notify(GameState gameState);

    /**
     * Notify observer of end of communication.
     */
    void terminateCommunication();
}
```
__Sequence Diagram__

```
Referee (observable)                                        BazaarObserver  View
|                                                                 |           |
| <-------------addObserver(BazaarObserver)---------------------- |           |
|                                                                 |           |
| -- Referees GameState changes                                   |           |
|           |                                                     |           |
| <----------                                                     |           |
|                                                                 |           |
| ----------------notify(GameState gameState)-------------------> |           |
|                                                                 |---view--->|
|                                                                 |           |
| -- Referees GameState changes                                   |           |
|           |                                                     |           |
| <----------                                                     |           |
|                                                                 |           |
| ----------------notify(GameState gameState)-------------------> |           |
|                                                                 |---view--->|
|                                                                 |           |
|              Loops until Referee shuts down                     |           |
| ----------------terminateCommunication()----------------------> |           |
|                                                                 |           |
|                                                                 |           |



```