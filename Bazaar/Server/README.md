# Bazaar - Server
This folder contains the components used to connect to and play games with a Bazaar Server. It includes classes for
communication.

### Player
The `Player` class represents a proxy player, and implements the `IPlayer` interface. This component gets constructed using
input and output streams (intended to be client socket input and output streams), and converts client JSON requests (received through the input stream)
into Bazaar `IPlayer` requests.

### ServerReferee
The `ServerReferee` class is an extension of the `ObservableReferee` class, and implements additional handling for move timeouts.
It handles timeout failures from its list of `IPlayer` (where the `ObservableReferee` does not).

### Server
The `Server` component is the entry point for a game running a server-side game of Bazaar. It is in charge of creating a socket
and accepting client connections, creating a `Player` for each client, and running a full game using the `ServerReferee`.
The communication specifications for this component are found within the Bazaar spec.

__How to run a Server-side game of Bazaar__ 

To run a server-side game of Bazaar, the `Server` component contains a main method, where the first argument is the port to
open the server side socket. The `Server` then accepts client-side connections through this port, and automatically begins a game when
required number of valid clients connect to the server-side socket. When the game is complete, the `Server` terminates
and writes the output to System.out.

### CommunicationUtils
The `CommunicationUtils` class contains methods for timing out tasks, and creating custom `ExecutorServices` that utilize 
safer daemon threads. The static methods in this class are used in other components in this package.