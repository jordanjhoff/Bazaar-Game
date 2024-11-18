package Runnables;

import Common.converters.BadJsonException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.List;

/**
 * This interface represents classes that run a milestone test
 */
public interface TestRunner {
    /**
     * Run a single test, and writes the output to out
     * @param input the json input for the test
     * @param out the computed json output
     * @return The list of deserialized objects for integration testing
     * @throws IOException
     */
    List<Object> run(InputStreamReader input, Writer out) throws IOException, BadJsonException;
}
