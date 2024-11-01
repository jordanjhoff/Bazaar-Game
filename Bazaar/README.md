# Bazaar
https://course.ccs.neu.edu/cs4500f24/bazaar.html

## Common
This folder contains classes which are used by both the players and the referee. These classes include basic game 
objects as well as communication objects sent between the `Referee` and the `Player`.

## Referee
This folder contains classes to be used only by the referee. This includes the `Referee` class which is responsible for 
running complete games of Bazaar, as well as game object generation and data representation of the Referee's knowledge.

## Player
This folder contains classes which will only be used by a player. This includes the `IPlayer` interface representing the 
actual player mechanisms, as well as the strategies the players will use to choose their turns.

## Runnables
This folder contains classes which are used for integration milestone testing, as well as classes only used during these
integration tests.

## UnitTests
This folder contains unit tests as well as classes only used in testing.