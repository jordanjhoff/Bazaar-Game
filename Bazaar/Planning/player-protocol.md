__Memorandum__

TO: The CEOs  
FROM: Educational parrots
DATE: October 10, 2024  
SUBJECT: Player protocol design


__Setup game__  
In this phase, the `Referee` will communicate to each `Player` the setup, which would be in the form 
`setup(EquationTable)`. This would notify `Player` that the game has begun, and will inform the players of the 
global `EquationTable` to be used for this game.\

Referee -----setup(EquationTable)-------> Player\

__Playing/Making turns__  
In this phase, for each turn the `Referee` will send the `TurnState` to the active `Player`. The `Player` will then send
a response based upon how they want to proceed with their turn.\

Referee -----requestMove(TurnState)-----> Player (Request)\

OPTION 1: the `Player` returns `draw()`, indicating it wants to draw a `Pebble`\
Referee <-----------draw()--------------- Player (Return)\

OPTION 2: the `Player` sends a list of exchanges it wants to make\
Referee <---exchange(exchangeSequence)--- Player (Return)\

OPTION 3: the `Player` foregoes any pebble exchanges\
Referee <--------skipExchange()---------- Player (Return)\

After the three options: the `Referee` requests a purchase request, while sending an updated `TurnState`. 
The `Player` responds with the cards it wants to buy.\
Referee -----requestBuy(TurnState)------> Player (Request)\
Referee <------buyCard(List<Card>)------- Player (Return)\

`Referee` repeats this sequence for all `Player`, until it deems the game over. If a `Player` makes an
invalid move, the `Referee` eliminates the `Player` and no longer requests the `Player` for its turn.\

__Game end__  
Upon game completion, the `Referee` will notify each `Player` using `playerWon(boolean)`. All `Player` are 
notified (both winners and losers), and the communication between `Referee` and `Player` terminates.\

Referee -----playerWon(boolean)-------> Player