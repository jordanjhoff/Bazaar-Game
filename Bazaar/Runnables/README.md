# Bazaar - Runnables
This folder contains the classes used to run our executable tests.  
The test for each milestone is included as a .jar file in a top level directory of the same name as
the milestone. A corresponding executable file which runs the .jar can be found in the same directory.

### IntegrationTestFestRunner
This class will run all tests for each milestone on the corresponding Json input files and check that the outputs match 
the expected values.

### TestRunner
This interface will be implemented by each milestone's testing task.