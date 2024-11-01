# Bazaar - Player

This package represents the classes needed to represent Bazaar strategies.


### IStrategy
The `IStrategy` interface includes the basic methods which a strategy for Bazaar will need to implement. These include 
setting up the `RuleBook`, and determining the desired action for each half of the player's turn

### Strategy
`Strategy` is a class that implements `IStrategy`, and uses a `Comparator<Turn>` to determine the best move. These 
comparators are how different maximization policies are implemented.

### Comparators
These classes are the strategy implementations. They implement `Comparator<Turn>`, and contain
logic to determine which of two turns is better.

### IPlayer
The `IPlayer` interface includes the five methods all player implementations must employ.

### Mechanism
`Mechanism` is an implementation of the `IPlayer` interface which uses a `Strategy` to determine which moves it should 
make.