Pair: likeable-gophers \
Commit: 6868ad05359ddfd0358555058e45dda700d18f6 \
Score: 245/275 \
Grader: Daniel Allex

- [30/30] README Inspection
- [180/210] Program Inspection
  - [20/20] Accurate self-eval
  - [74/80] Bazaar/Server/player Inspection
    - [50/50] Bazaar/Server/player implements same interface as Bazaar/Player/mechanism
      - [10/10] name call
      - [10/10] setup call
      - [10/10] requestPebbleortrades call
      - [10/10] requestCards call
      - [10/10] win call
    - [15/15] constructors (builders, factories) that receive handles for sending/receiving over some stream (so that TCP can be mocked)
    - [9/15] unit tests for Bazaar/Server/player checks that an object writes JSON for the given arguments to a mocked output stream
      - Honesty points
  - [64/70] Bazaar/Clients/referee Inspection
    - [40/40] Bazaar/Clients/referee implementation
      - It is fine that the proxy referee implements a different interface. It just needs to implement under the same context, which yours does.
    - [15/15] constructor (builder, factory) must receive (1) a player and (2) handles for sending/receiving JSON over streams
    - [9/15] unit tests for Bazaar/Client/referee checks whether the method can read JSON arguments from a mock input device
      - Honesty points
  - [12/20] synchronizing client start-ups with servers (client waits for server or shuts down gracefully if server is not up yet)
   - Honesty points
  - [10/20] abstracting over the “wait for two periods” property of the server
    - created function to abstract away the waiting task and explicitly called it twice. would be ideal to abstract away the number of waiting periods by using a loop to easily allow for any number of waiting periods.
- [35/35] Design Task
  - [10/10] first proposed change
    - [5/5] change to the data representation of the referee’s knowledge about players that includes the cards a player has purchased
    - [5/5] change to the rule book’s “determine winners and losers” functionality so that it can assign a bonus for the red-white-and-blue flag
  - [25/25] second proposed change
    - [5/5] change to the data representation of pebbles
    - [15/15] potential change to the data representation of cards and wallets
    - [5/5] an additional note that the “determine winners and losers” functionality must check for colors and the glowing factor