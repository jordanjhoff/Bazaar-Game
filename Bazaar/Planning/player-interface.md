__Memorandum__

TO: The CEOs  
FROM: Futuristic Spiders  
DATE: October 1, 2024  
SUBJECT: Player Interface Planning

__Data Representation:__  
The players will need to know about `1.`their current wallets and scores, `2.`the inventory of the 
bank, `3.`which cards are available to buy, `4.`which equations they can use, and `5.`the current 
scores of other players. Some of this information will be sent to the player as a `Turn_State` by 
the referee when they are notified of their turn. This includes the bank inventory which will be 
sent as a `Pebbles`, the current information about the player such as their wallet and their score 
which will be sent as a `PlayerInformation`, and the scores of the other players which will be sent 
as a list of integers. Other information, such as available cards and equations will need to be
determined by the players.

__Operations Wishlist:__  
Receive game information from the referee  
`void awaitTurnState()`

Choose a move to make and return as JSON string.  
*This method will need to be able to gather the current state of the cards, as well as which 
equations can be used.*  
`String chooseMove()`

Send move to referee  
`void sendMove(String move)`