__Memorandum__

TO: The CEOs  \
FROM: likeable-gophers\
DATE: November 7, 2024  \
SUBJECT: Remote client-server protocol
```
Setup phase

Server ------------------------------------------- Client*

  |    <----------------tcp connect---------------   | 
  |                                                  |
  |    <----------acceptName(String name)---------   |   (request)
  |    -----------boolean wasAccepted------------>   |   (return)
  |                                                  |
```

After a waiting period, the server no longer accepts new players.\
If a player requests to join, the server restricts TCP connections.\
The server returns false for wasAccepted if the given name is already taken.

```
Starting a Game

Server ------------------------------------------- Client*
  |                                                  |
  |    -----------setup(EquationTable)----------->   |   (request)
  |                                                  |   (no response)
  |                                                  |
  
Running a Game

For the active client in the turn order:
Server ------------------------------------------- Client
  |                                                  |
  |    ----requestPebbleOrExchanges(TurnState)--->   |   (request)
  |    <------------ExchangeRequest---------------   |  (response)
  |                                                  |
  |---------checkMoveLegal                           |
  |               |                                  |
  |<--------true  |  false                           |
  |                    |                             |    
  |              SKIP to kickPlayer                  |
  |                                                  |
  |                                                  |
  |-----------gameOver?                              |
  |               |                                  |
  |<--------false |  true                            |
  |                    |                             |    
  |              SKIP to endGame                     |
  |                                                  |
  |                                                  |
  |    ------requestCardPurchase(TurnState)------>   |   (request)
  |    <----------CardPurchaseSequence------------   |  (response)
  |                                                  | 
  |---------checkMoveLegal                           |
  |               |                                  |
  |<--------true  |  false                           |
  |                    |                             |    
  |              SKIP to kickPlayer                  |
  |                                                  |  
  |                                                  |  
  |-----------gameOver?                              |
  |               |                                  |
  |<--------false |  true                            |
  |                    |                             |    
  |              SKIP to endGame                     |
  |                                                  |
  |----advanceTurn                                   |
  |        |                                         |
  |        |                                         |
  |<--------                                         |
  |     SEQUENCE DIAGRAM LOOPS WITH NEXT CLIENT      |
  
If checkMoveLegal returns false:
Server ------------------------------------------- Client
  |                                                  |
  |----kickAndAdvanceTurn                            |
  |        |                                         |
  |        |                                         |
  |<--------                                         |


If gameOver returns true:
Server ------------------------------------------- Client*
  |                                                  |
  |    -----------notifyWin(boolean)------------->   |   (request)
  |                                                  |   (no response)
  |                                                  |
```
JSON specifications:

An ExchangeRequest is:
```json
{"request":*RequestType, "exchanges":*Rules}
```

A CardPurchaseSequece is:
```json
*Cards
```
RequestType is one of the String:
```json
- "draw"
- "trade"
```

For JSON specification EquationTable, Equations, Rules and Cards, visit the Bazaar course page.