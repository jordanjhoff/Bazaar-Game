package Runnables;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;

/**
 * This interface represents classes that run a milestone test
 */
public interface TestRunner {
    /**
     * Run a single test
     * @param input the json input for the test
     * @param out the computed json output
     * @throws IOException
     */
    void run(InputStreamReader input, Writer out) throws IOException;
}
