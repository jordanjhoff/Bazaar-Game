__Memorandum__

TO: The CEOs  
FROM: Futuristic Spiders  
DATE: September 22, 2024  
SUBJECT: Game State Planning

__Data Representation:__  
The referee will need to know about `1.`the current players of the game, `2.`what is in their 
inventories as well as the inventory of the bank, `3.`the available equations and cards (both 
visible and not), and `4.`which playerInformation's turn it currently is. The equations and card will be 
represented by our IEquations and ICards interfaces. The inventory of the bank will be represented 
by a `Hashmap<PebbleColor, Integer>` of pebble colors and how many of each the bank currently 
contains. Player inventories will be included within a `Player` class which will also include 
additional information about each unique playerInformation.


__Operations Wishlist:__  
Determine if the game has ended  
`boolean isGameOver()`  

Kick a playerInformation out  
`void kickPlayerOut(Player playerInformation)`

Performs exchange between current playerInformation and bank using Equation. Only performs exchange if the 
request is valid.  
`void performExchange(Player playerInformation, IEquation equation)`

Buy card for current playerInformation. Only buys card if the request is valid.  
`void buyCard(Player playerInformation, ICard card)`

Restock cards  
`void restockCards()`

Change to the next playerInformation's turn and notify them  
`void notifyNextPlayer()`

Draw pebble for current playerInformation  
`void drawPebble()`