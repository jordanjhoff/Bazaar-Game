__Memorandum__

TO: The CEOs  
FROM: Likeable Gophers 
DATE: December 2nd, 2024  
SUBJECT: System modifications

We have made the following changes to our codebase

- PlayerInformation record class contains card purchase history -- https://github.khoury.northeastern.edu/CS4500-F24/likeable-gophers/blob/3e94c3a585d624be9d2e01da83a563b653a7200c/Bazaar/Common/PlayerInformation.java#L18
- Serialization of PlayerInformation contains card purchase history-- https://github.khoury.northeastern.edu/CS4500-F24/likeable-gophers/blob/3e94c3a585d624be9d2e01da83a563b653a7200c/Bazaar/Common/converters/JSONSerializer.java#L126
- Deserialization of the PlayerInformation class supports card purchase history (or not) -- https://github.khoury.northeastern.edu/CS4500-F24/likeable-gophers/blob/3e94c3a585d624be9d2e01da83a563b653a7200c/Bazaar/Common/converters/JSONDeserializer.java#L163-L167
- RuleBook updates PlayerInformation's card purchase history between turns -- https://github.khoury.northeastern.edu/CS4500-F24/likeable-gophers/blob/3e94c3a585d624be9d2e01da83a563b653a7200c/Bazaar/Common/RuleBook.java#L348
- Referee renames PlayerInformations upon game start -- https://github.khoury.northeastern.edu/CS4500-F24/likeable-gophers/blob/3e94c3a585d624be9d2e01da83a563b653a7200c/Bazaar/Referee/Referee.java#L303
- RuleBook determines winners using a Bonus Function (for no bonus, supply the identity function) -- https://github.khoury.northeastern.edu/CS4500-F24/likeable-gophers/blob/3e94c3a585d624be9d2e01da83a563b653a7200c/Bazaar/Common/RuleBook.java#L196-L198