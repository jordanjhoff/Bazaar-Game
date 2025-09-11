# Bazaar



Bazaar is a multiplayer turn-based strategy game where players compete as merchants in a virtual marketplace, collecting colored pebbles as currency and trading them according to dynamic exchange rules to purchase valuable cards.

The Bazaar Game project involved building a distributed multiplayer game server in Java 21 that manages real-time strategy gameplay across concurrent TCP connections, featuring a custom binary protocol for client-server communication and sophisticated game state management through immutable data structures. The system handles multiple simultaneous game lobbies each using non-blocking I/O and asynchronous message processing, while also supporting bulk testing capabilities that evaluate AI strategies across 500+ game scenarios in parallel.
