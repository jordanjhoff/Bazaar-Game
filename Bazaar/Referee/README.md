# Bazaar - Referee
This folder contains classes used by the referee to store, update, and interpret the state of the
game.

### GameState:
The game state, represented by the `GameState` class contains the information the referee needs to update about the 
game. This includes the cards, the inventory of the bank, and information about the current players including their 
inventories and the turn order.

### GameResult:
The `GameResult` class represents the results of a completed game of Bazaar. It includes two lists of `IPlayer`. One of 
the winning players and one of the players who were kicked from the game.

### GameObjectGenerator:
The `GameObjectGenerator` class contains generation and randomization of any game objects that need to be used.

### Referee: 
The `Referee`controls individual games of Bazaar. It is in charge of communicating with player components, enforcing 
Bazaar rules, and executing a full game. It constructs a game, and uses the `RuleBook` class to validate actions within 
a game.

### Observer:
The `Observer` is a listener that can be added to `ObservableReferee`. Upon notification of new `GameStates`, it renders
the states as images, and contains functionality for viewing the `GameState` history in a GUI.

### ObservableReferee:
The `ObservableReferee` is an extension of `Referee` that controls individual games of Bazaar. It does everything a 
`Referee` does, in addition to notifying its `Observers` of changes in it's `GameState`.

__How to use ObservableReferee__
To attach an `Observer` to the `ObservableReferee`, pass as many `Observer` as desired upon construction. 

