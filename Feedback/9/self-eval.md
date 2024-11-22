The commit we tagged for your submission is 6868ad05359ddfd0358555058e45dda700d18f6.
**If you use GitHub permalinks, they must refer to this commit or your self-eval will be rejected.**
Navigate to the URL below to create permalinks and check that the commit hash in the final permalink URL is correct:

https://github.khoury.northeastern.edu/CS4500-F24/likeable-gophers/tree/6868ad05359ddfd0358555058e45dda700d18f6

## Self-Evaluation Form for Milestone 9

Indicate below each bullet which file/unit takes care of each task.

### Programming Task 

For `Bazaar/Server/player`,

- explain how it implements the exact same interface as `Bazaar/Player/player`

The `Bazaar/Server/Player` literally implements the same interface https://github.khoury.northeastern.edu/CS4500-F24/likeable-gophers/blob/6868ad05359ddfd0358555058e45dda700d18f60/Bazaar/Server/Player.java#L19 and is responsible for serialization of Referee requests. It is also responsible for deserializing remote player responses, protecting against poorly formatted JSON with BadJsonExceptions (thrown when our abstracted deserialization into an object fails). These BadJsonExceptions *must* be handled. We use a helper method to abstract away any Socket usage, so each method in `player` (requestPebbleOrTrades, win, setup, requestCards) provides a JSONElement to this helper https://github.khoury.northeastern.edu/CS4500-F24/likeable-gophers/blob/6868ad05359ddfd0358555058e45dda700d18f60/Bazaar/Server/Player.java#L86

- explain how it receives the TCP connection that enables it to communicate with a client

`player` is not responsible for managing the socket. it is constructed with an InputStream and OutputStream. This is especially useful for mock testing. In `Bazaar/Server/Server`, when a remote player sends their name and is accepted into the game, a new `Bazaar/Server/Player` is constructed with the input/output streams from the socket used to receive the name.
https://github.khoury.northeastern.edu/CS4500-F24/likeable-gophers/blob/6868ad05359ddfd0358555058e45dda700d18f60/Bazaar/Server/Server.java#L141
https://github.khoury.northeastern.edu/CS4500-F24/likeable-gophers/blob/6868ad05359ddfd0358555058e45dda700d18f60/Bazaar/Server/Server.java#L148

- point to unit tests that check whether it writes (proper) JSON to a mock output device

We created a GitHub issue https://github.khoury.northeastern.edu/CS4500-F24/likeable-gophers/issues/25#issue-67313 *specifically* for this testing and forgot to complete it.

For `Bazaar/Client/referee`,

- explain how it implements the same interface as `Bazaar/Referee/referee`

Our `Bazaar/Referee/Referee` does not implement an interface. This is because it is responsible for *calling* players, but players do **not** call it. Our `Bazaar/Client/Referee` (actually named ClientReferee for package reasons) deserializes JSON instructions from the 

- explain how it receives the TCP connection that enables it to communicate with a server

The Referee is responsible for sending a socket request to the server using the connect method https://github.khoury.northeastern.edu/CS4500-F24/likeable-gophers/blob/6868ad05359ddfd0358555058e45dda700d18f60/Bazaar/Client/ClientReferee.java#L62 After the socket connection is received, the ClientReferee attempts to send the name using the IPlayer name() function
  
- point to unit tests that check whether it reads (possibly broken) JSON from a mock input device

We did not consider broken JSON from a misbehaving server to be a valid use case, so there is no test for it.

For `Bazaar/Client/client`, explain what happens when the client is started _before_ the server is up and running:

- does it wait until the server is up (best solution)
- does it shut down gracefully (acceptable now, but switch to the first option for 10)

It throws an error and crashes with Connection Refused. We should have seen this coming.


For `Bazaar/Server/server`, explain how the code implements the two waiting periods. 

[https://github.khoury.northeastern.edu/CS4500-F24/likeable-gophers/blob/6868ad05359ddfd0358555058e45dda700d18f60/Bazaar/Server/Server.java#L82](https://github.khoury.northeastern.edu/CS4500-F24/likeable-gophers/blob/6868ad05359ddfd0358555058e45dda700d18f60/Bazaar/Server/Server.java#L82-L91)
Our Lobby method makes two calls to the waitingRoom helper, the second conditional on there being 2 players accepted into the game. We allow for 20 seconds of join time and three seconds for a new connection to provide the player's name. If a player makes a socket connection at 19 seconds, the time to send their name is dynamically limited and they have 1 second. DoS attacks do not work; players that fail to join do not prevent legitimate players from joining as name reading is asynchronous.

### Design Task 

For design task 1,

- did you make sure to separate the changes into one for the knowledge
  about players and one for the rule book? Why is this separation critical?

  Yes, we indicated that a field must be added to the PlayerInformation to store the purchased cards, and we separately indicated that the RuleBook's getWinners method would be adjusted to add the bonus to the players scores before calculating the winner(s). This is critical because the responsibility for points belongs in the rulebook, and the rulebook is not responsible for storing game information--that is the job of a Player record class.

For design task 2, 

- did you consider changes to the data representation pebbles?

Yes, we would need a ComplexPebble class instead of a Pebble enum to be able to store the glowing property.

- would this change induce changes to wallets and cards?

It would induce changes to Wallets, where Wallets would need to be adjusted to use the new class instead of a Pebble Enum. Continuing to use a Pebble Enum would be possible if Glowing pebbles are not fungible with regular pebbles of the same color. This change would also affect object generators. It would not affect cards; cards use Wallets. https://github.khoury.northeastern.edu/CS4500-F24/likeable-gophers/blob/6868ad05359ddfd0358555058e45dda700d18f60/Bazaar/Common/Card.java#L10C20-L10C36

For the reflection on design tasks, you may wish to point to relevant
pieces of code to justify your responses. There was no need to
implement anything so the _old_ code is all the TAs need. 

### Form of Feedback


The ideal feedback for each of these three points is a GitHub
perma-link to the range of lines in a specific file or a collection of
files.

A lesser alternative is to specify paths to files and, if files are
longer than a laptop screen, positions within files are appropriate
responses.

You may wish to add a sentence that explains how you think the
specified code snippets answer the request.

If you did *not* realize these pieces of functionality, say so.

