# Bazaar - Client 

This package contains the additional architecture necessary to run a remote client in remote games.
It is dependent on JSON (de)Serializer, GSON, Bazaar.Common, and Bazaar.Player

### Client
The client is a wrapper for the ClientReferee. It is responsible for configuring and running the
ClientReferee when executing from the command line.

__How to run a Client__

To run a client, provide the first argument as the server address, the second argument as the server port, and 
provide the third argument of a JSON `ActorB` from the [Bazaar Spec](https://course.ccs.neu.edu/cs4500f24/9.html#%28tech._actorb%29). Optionally, if only the JSON `ActorB` 
is provided, the client runs on localhost port 4114.

### ClientReferee
This is a proxy referee. It connects to a server via a new socket and transforms JSON messages from
the server into requests and Bazaar Objects usable by an `IPlayer`.

It then calls the `IPlayer` with the request and arguments. The `IPlayer` returns as normal, and the
ClientReferee serializes their answer back into JSON before sending it back to the server.

For previously `void` method calls to an IPlayer, the ClientReferee replies with the JSON string "void".

Prompts to the ClientReferee should be in the JSON format `[MName, [Argument]]`.
More information about valid MNames and Arguments is available on the [Remote Interactions Page](https://course.ccs.neu.edu/cs4500f24/remote.html)