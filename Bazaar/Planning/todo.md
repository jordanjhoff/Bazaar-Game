__Memorandum__

TO: The CEOs  
FROM: Educational Parrots 
DATE: Monday Oct 28, 2024  
SUBJECT: Todo planning for Bazaar.

Below is a list of changes we wish to add to our codebase, ranked by highest priority first.

EDIT: As we have progressed through this milestone, we have identified more changed we wish to make. The completed changes are added in the Completed Section.

~~1. Fix glitch in `Mechanism/Strategy` regarding requestDrawPebble~~ \
~~2. Refactor rendering with tests~~\
~~3. Refactor `Referee` constructors~~\
~~4. Make `Referee` request methods only try-catch on the single player method~~\
~~5. Remove `Mechanism` `RuleBook` field~~

Completed
1. Fix glitch in `Mechanism/Strategy` regarding requestDrawPebble 
   - https://github.khoury.northeastern.edu/CS4500-F24/educational-parrots/commit/307032c43fa995d40a742815230c5b0463011f30
2. Refactor rendering with tests 
   - https://github.khoury.northeastern.edu/CS4500-F24/educational-parrots/commit/3dafbdd078e97f501ad3c89d30580a6c8d923b3f
3. Refactor `Referee` constructors 
   - https://github.khoury.northeastern.edu/CS4500-F24/educational-parrots/commit/350cce37aff1fa0b553ab8b08b4a07ecf558fd9c
4. Make `Referee` request methods only try-catch on the single player method 
   - https://github.khoury.northeastern.edu/CS4500-F24/educational-parrots/commit/c0e8ad1db475ddf7fbefbe48a1b53e3ccdd1d8de
5. Remove `Mechanism` `RuleBook` field 
   - https://github.khoury.northeastern.edu/CS4500-F24/educational-parrots/commit/6cbc6274abc0e09d3f2635873f71ca5434786154
6. Added `IntegrationTestFestRunner` to run all provided Feedback tests
   - https://github.khoury.northeastern.edu/CS4500-F24/educational-parrots/commit/9d1145be07ab59009aa5c84cb3d722a83067bbbe
7. Moved generation from `Referee` to `GameObjectGenerator`
   - https://github.khoury.northeastern.edu/CS4500-F24/educational-parrots/commit/a2826b1028c9c46b448504d07048e78432239bb1
8. Fixed `Strategy` bug that was not returning proper `CardPurchaseSequence`
   - https://github.khoury.northeastern.edu/CS4500-F24/educational-parrots/commit/a2826b1028c9c46b448504d07048e78432239bb1
9. Added exception catches in `Referee` for all `Player` request methods
   - https://github.khoury.northeastern.edu/CS4500-F24/educational-parrots/commit/a2826b1028c9c46b448504d07048e78432239bb1
10. Updated documentation for packages, classes, and methods
   - https://github.khoury.northeastern.edu/CS4500-F24/educational-parrots/commit/a584db15f361c0814c10d59ab2dc0e73096a1c4f

