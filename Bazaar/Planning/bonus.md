__Memorandum__

TO: The CEOs  \
FROM: likeable-gophers\
DATE: November 21, 2024  \
SUBJECT: Updates to Bazaar Game

__Change 1: End Game Bonus__

In order to implement the requested change of 10 bonus points rewarded to players that meet the card collecting criteria,
we would need to implement the following changes:

- We would need to update the PlayerInformation record to store the purchase history of a player
- The RuleBook method that handles simulation of purchases (the validPurchases method) would additionally add the purchased cards to the record
- The RuleBook method that calculates the winning players (the getWinners method) would additionally add the bonus to player scores, and then find the winning players
  - An additional helper method would determine if a PlayerInformation contains the required cards to meet the bonus condition

This change would mainly affect the Common package, and affect RuleBook and PlayerInformation. This change doesn't affect the control flow of Referee, and the Referee package remains unchanged.
The Player package would function without any changes, however a user could implement new strategies with this bonus in mind if they wished. The communication layer (Client and Server packages) would remain unchanged,
as we do not need to transmit purchase history with every communication.

__Change 2: End Game Bonus and New Glowing Pebble__

In order to implement the requested change of the bonus only being applied if cards with a new glowing pebble type,
we would need to implement the following changes:

- We would need to use a ComplexPebble class instead of a Pebble enum
  - Pebbles are now too complex to be represented through an enum
  - This ComplexPebble class would contain our existing Pebble enum, and an additional glowing property
  - Object equality for ComplexPebble depends on required additional clarifications (see below)
- Any classes that use Pebble would need to be updated to use ComplexPebble
  - The PebbleCollection class would change depending on required additional clarifications (see below)
- The communication layer would need to change, as JSON representations of ComplexPebbles would need to change
  - This affects the JSONDeserializer and the JSONSerializer
- Additionally, all the changes needed for Change 1 would need to be implemented
  - The helper method that determines if a PlayerInformation meets the criteria for the bonus is checking different criteria

Additional Specification Clarifications Needed for Change 2
- Can glowing pebbles be exchanged with non-glowing pebbles?
  - If so, is there a specific exchange rate? Are they included within the usable equations?
  - If not, how does a player acquire glowing pebbles?

Our design decision to use enums to represent pebbles affected us, as moving to a more complex structure involves making a significant amount of changes.