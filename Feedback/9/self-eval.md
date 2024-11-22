The commit we tagged for your submission is 6868ad05359ddfd0358555058e45dda700d18f6.
**If you use GitHub permalinks, they must refer to this commit or your self-eval will be rejected.**
Navigate to the URL below to create permalinks and check that the commit hash in the final permalink URL is correct:

https://github.khoury.northeastern.edu/CS4500-F24/likeable-gophers/tree/6868ad05359ddfd0358555058e45dda700d18f6

## Self-Evaluation Form for Milestone 9

Indicate below each bullet which file/unit takes care of each task.

### Programming Task 

For `Bazaar/Server/player`,

- explain how it implements the exact same interface as `Bazaar/Player/player`
- explain how it receives the TCP connection that enables it to communicate with a client
- point to unit tests that check whether it writes (proper) JSON to a mock output device

For `Bazaar/Client/referee`,

- explain how it implements the same interface as `Bazaar/Referee/referee`
- explain how it receives the TCP connection that enables it to communicate with a server
- point to unit tests that check whether it reads (possibly broken) JSON from a mock input device

For `Bazaar/Client/client`, explain what happens when the client is started _before_ the server is up and running:

- does it wait until the server is up (best solution)
- does it shut down gracefully (acceptable now, but switch to the first option for 10)

For `Bazaar/Server/server`, explain how the code implements the two waiting periods. 

### Design Task 

For design task 1,

- did you make sure to separate the changes into one for the knowledge
  about players and one for the rule book? Why is this separation critical?

For design task 2, 

- did you consider changes to the data representation pebbles?
- would this change induce changes to wallets and cards?

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

