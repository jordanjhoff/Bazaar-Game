package UnitTests;

import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import java.io.StringReader;

/**
 * A class to help with unit tests. Unfinished.
 */
public class TestingUtils {

    public static JsonElement getJsonElementString(String string) {
        JsonStreamParser p = new JsonStreamParser(new StringReader(string));
        return p.next();
    }
}
