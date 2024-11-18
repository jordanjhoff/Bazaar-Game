The commit we tagged for your submission is b96f9e00e49b5abad7734882bb3399db44be9ae.
**If you use GitHub permalinks, they must refer to this commit or your self-eval will be rejected.**
Navigate to the URL below to create permalinks and check that the commit hash in the final permalink URL is correct:

https://github.khoury.northeastern.edu/CS4500-F24/likeable-gophers/tree/b96f9e00e49b5abad7734882bb3399db44be9ae

## Self-Evaluation Form for Milestone 8

Indicate below each bullet which file/unit takes care of each task:

- did you consider what role an observer plays in the overall system?
  - This was considered, but was not documented in the codebase. We believed the observer would be used by DevOps to view games in progress.


- concerning the modifications to the referee: 

  - is the referee programmed to the observer's interface or is it hardwired?
    - We created the ObservableReferee class, which is an extension of Referee. The standard Referee behavior was unchanged.
    - https://github.khoury.northeastern.edu/CS4500-F24/likeable-gophers/blob/b96f9e00e49b5abad7734882bb3399db44be9aec/Bazaar/Referee/ObservableReferee.java#L11

  - if an observer is desired, is every state per player action sent to the observer? Where?
    - Upon start: https://github.khoury.northeastern.edu/CS4500-F24/likeable-gophers/blob/b96f9e00e49b5abad7734882bb3399db44be9aec/Bazaar/Referee/ObservableReferee.java#L29
    - Upon a valid player turn (exchanges or request pebble): https://github.khoury.northeastern.edu/CS4500-F24/likeable-gophers/blob/b96f9e00e49b5abad7734882bb3399db44be9aec/Bazaar/Referee/ObservableReferee.java#L35
    - Upon a valid player turn (card purchases): https://github.khoury.northeastern.edu/CS4500-F24/likeable-gophers/blob/b96f9e00e49b5abad7734882bb3399db44be9aec/Bazaar/Referee/ObservableReferee.java#L41
    - Upon advancing the turn: https://github.khoury.northeastern.edu/CS4500-F24/likeable-gophers/blob/b96f9e00e49b5abad7734882bb3399db44be9aec/Bazaar/Referee/ObservableReferee.java#L48, https://github.khoury.northeastern.edu/CS4500-F24/likeable-gophers/blob/b96f9e00e49b5abad7734882bb3399db44be9aec/Bazaar/Referee/ObservableReferee.java#L55

  - if an observer is not desired, how does the referee avoid calls to the observer?
    - The ObservableReferee uses the notifyListeners() method to notify the Observer. If no observers have been added, this method does nothing.
    - https://github.khoury.northeastern.edu/CS4500-F24/likeable-gophers/blob/b96f9e00e49b5abad7734882bb3399db44be9aec/Bazaar/Referee/ObservableReferee.java#L66


- concerning the implementation of the observer:

  - does the purpose statement explain how to program to the
    observer's interface?
    - We are missing documentation for this.

  - does the purpose statement explain how a user would use the
    observer's view? Or is it explained elsewhere? 
    - We are missing documentation for this.

The ideal feedback for each of these three points is a GitHub
perma-link to the range of lines in a specific file or a collection of
files.

A lesser alternative is to specify paths to files and, if files are
longer than a laptop screen, positions within files are appropriate
responses.

You may wish to add a sentence that explains how you think the
specified code snippets answer the request.

If you did *not* realize these pieces of functionality, say so.

