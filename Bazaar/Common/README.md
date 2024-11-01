# Bazaar - Common

This package represents the game objects that will be used by a bazaar game. Every class in this package is
immutable, and any modification methods in any of these classes return new immutable instances of these
classes.

### PebbleCollection:
The `PebbleCollection` class represents a collection of pebbles. This can be within an equation, on a card,
or in an inventory. This class contains constructors with different argument depending on how the 
collection is created. There is also the comparison method `contains` which is used to 
check if a player or the bank have enough pebbles to buy a card or use and equation.

### ExchangeRule:
The `ExchangeRule` class represents a single directional exchange of pebbles.

### Equation:
The `Equation` class represents a collection of two `ExchangeRule`, representing a bidirectional exchange of pebbles

### EquationTable:
`EquationTable` is the collection of unordered equations available to players during a game of Bazaar.

### Card:
The `Card` class represents a single card with a collection of pebbles representing its purchase cost and optionally a 
face.

### CardDeck:
`CardDeck` is the collection of all `Card` currently in use within a game of Bazaar.

### Turn State:
The `TurnState` class represents the information that the referee needs to send to the players for
their turn. This includes the inventory of the bank as a `PebbleCollection`, information about the current 
player as a `PlayerInformation`, and the current score of other players as a list of 
integers.

### PlayerInformation:
The `PlayerInformation` represents the information of a single player. It contains their wallet and their 

### ExchangeRequest:
The `ExchangeRequest` interface is a sealed interface which represents the two possible actions a player can take during
the first half of their turn. These possibilities are one of `PebbleDrawRequest`, or `PebbleExchangeSequence`.

#### PebbleDrawRequest:
A `PebbleDrawRequest` represents the player deciding to draw a random pebble from the bank.

#### PebbleExchangeSequence:
A `PebbleExchangeSequence` represents the player deciding to conduct some number of exchanges with the bank using the
available `EquationTable`.

### CardPurchaseSequence:
The `CardPurchaseSequence` class represents a purchase of cards, and contains a list of `Card` which one wants
to purchase.

### Turn:
The `Turn` class represents the information for a single valid turn. It is composed of a `PebbleExchangeSequence` and a 
`CardPurchaseSequence`.

### RuleBook:
The `RuleBook` class represents the rules to be followed during a game of Bazaar. The `Referee` will call to the 
`RuleBook` for executing turns.