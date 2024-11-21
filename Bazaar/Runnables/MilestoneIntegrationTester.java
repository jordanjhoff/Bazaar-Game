package Runnables;

import Common.converters.BadJsonException;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This abstract class contains methods to run all provided testfest integration tests.
 */
public abstract class MilestoneIntegrationTester {

    /**
     * Iterates over files in a single directory runs them in parallel, and writes all results to an output
     * @param testDirectory
     * @param out
     * @throws IOException
     */
    public void parallelRun(File testDirectory, Writer out, ExecutorService executor) throws IOException{
        File[] inFiles = getFiles(testDirectory, "^(\\d+)-(in)\\.json$");
        File[] outFiles = getFiles(testDirectory, "^(\\d+)-(out)\\.json$");
        List<Future<StringWriter>> futures = submitSingleTests(inFiles, outFiles, out, testDirectory.getAbsolutePath(), executor);
        retrieveResults(futures, out);
    }

    /**
     * Iterates over all testfest test directories, runs each directory in parallel,
     * and retrieves the results in sequential execution order
     * @param testFestDirectory
     * @param out
     * @param executor
     * @throws IOException
     */
    public void paralleltestFestRun(File testFestDirectory, Writer out, ExecutorService executor) throws IOException {
        if (testFestDirectory.isDirectory()) {
            File[] subDirs = testFestDirectory.listFiles(File::isDirectory);
            assert subDirs != null;
            Arrays.sort(subDirs, Comparator.comparing(File::getName));
            List<Future<StringWriter>> futures = submitDirectories(subDirs, executor);
            retrieveResults(futures, out);
        }
        else {
            writeToOut(out, "Testing failed:" + testFestDirectory.getAbsolutePath() + " is not a directory.\n");
        }
    }

    /**
     * Iterates over a single directory, and submits individual tests as executable futures.
     * @param testIn
     * @param testOut
     * @param out
     * @param testDirName
     * @param executor
     * @return a list of Futures of StringWriter results
     * @throws IOException
     */
    private List<Future<StringWriter>> submitSingleTests(File[] testIn, File[] testOut, Writer out, String testDirName, ExecutorService executor) throws IOException {
        List<Future<StringWriter>> futures = new ArrayList<>();
        String dir = "Testing: " + getClass().getSimpleName() + " in directory " + testDirName + "\n";
        writeToOut(out, dir);
        for (int i = 0; i < testIn.length; i++) {
            final int index = i;
            Future<StringWriter> future = executor.submit(() -> runOneTest(new InputStreamReader(new FileInputStream(testIn[index])),
                    new InputStreamReader(new FileInputStream(testOut[index])),
                    new InputStreamReader(new FileInputStream(testOut[index])), testIn[index].getName()));
            futures.add(future);
        }
        return futures;
    }

    /**
     * Given a list of subdirectories containing tests, this method submits them as executable futures
     * @param subDirs
     * @param executor
     * @return the list of Futures of StringWriter directory results
     */
    private List<Future<StringWriter>> submitDirectories(File[] subDirs, ExecutorService executor) {
        List<Future<StringWriter>> futures = new ArrayList<>();
        for (File subDir : subDirs) {
            Future<StringWriter> future = executor.submit(() -> {
                StringWriter buffer = new StringWriter();
                parallelRun(subDir, buffer, executor);
                return buffer;
            });
            futures.add(future);
        }

        return futures;
    }

    /**
     * Retrieves the list of results from Future StringWriters, and writes the results to out
     * @param results
     * @param out
     * @throws IOException
     */
    private void retrieveResults(List<Future<StringWriter>> results, Writer out) throws IOException {
        for (Future<StringWriter> future : results) {
            try {
                StringWriter dirResult = future.get();
                writeToOut(out, dirResult.toString());
            } catch (ExecutionException | InterruptedException e) {
                writeToOut(out,"Failed processing directories: " + e.getMessage() + "\n");
            }
        }
    }

    /**
     * Method to synchronized write to output
     * @param out
     * @param message
     * @throws IOException
     */
    private void writeToOut(Writer out, String message) throws IOException {
        synchronized (out) {
            out.write(message);
            out.flush();
        }
    }

    /**
     * Executes a single test, and returns the output as a string
     * @param testInput
     * @param expectedTestOutput
     * @param testName
     * @return the string output, with information about the failure if the test failed
     * @throws IOException
     * @throws BadJsonException
     */
    private String executeAndCompare(InputStreamReader testInput, InputStreamReader expectedTestOutput, String testName) throws IOException, BadJsonException {
        long startTime;
        long endTime;
        try {
            StringWriter executedOutput = new StringWriter();
            startTime = System.currentTimeMillis();
            List<Object> list1 = runTest(testInput, executedOutput);
            List<Object> list2 = jsonResultToObjects(expectedTestOutput);
            compareResults(list1, list2, executedOutput.toString());
            endTime = System.currentTimeMillis();
        }
        catch (Exception e) {
            return "Test " + testName + " failed by exception: " + e.getMessage() + "\n";
        }
        int elapseTime = (int)(endTime - startTime);
        return "Test " + testName + " passed in " + elapseTime + "ms\n";
    }

    /**
     * Abstract method to run a single test, which writes the output to a writer. This method is overridden depending on
     * the milestone's integration test
     * @param testInput
     * @param testOutput
     * @return additional result of test as a List of Bazaar game objects
     * @throws IOException
     * @throws BadJsonException
     */
    abstract List<Object> runTest(InputStreamReader testInput, StringWriter testOutput) throws IOException, BadJsonException;

    /**
     * Abstract method to read a JSON into Bazaar game objects. Used to serialize expected testcase results.
     * This method is overridden depending on the milestone's integration test
     * @param input
     * @return
     * @throws BadJsonException
     */
    abstract List<Object> jsonResultToObjects(InputStreamReader input) throws BadJsonException;

    /**
     * Takes an (n)-in.json and an (n)-out.json file, and compares them. Takes an additional copy of the expected output
     * to write in case of failure.
     * @param testInput
     * @param expectedOutput
     * @param testName
     * @return a string of the result
     * @throws IOException
     */
    private StringWriter runOneTest(InputStreamReader testInput, InputStreamReader expectedOutput, InputStreamReader expectedTestOutput2, String testName) throws IOException, BadJsonException {
        StringWriter testOutput = new StringWriter();
        String testResult = executeAndCompare(testInput, expectedOutput, testName);
        if (testResult.contains("failed")) {
            testOutput.append(testResult);
            testOutput.append("Expected Result: \n");
            String expectedResult = new BufferedReader(expectedTestOutput2)
                    .lines().collect(Collectors.joining("\n")) + "\n\n";
            testOutput.append(expectedResult);
        }
        else {
            testOutput.append(testResult);
        }
        return testOutput;
    }

    /**
     * A method to compare two list of Bazaar objects, used to compare executed test result to expected test result.
     * Throws if the test fails
     * @param list1
     * @param list2
     * @param executedOutput
     */
    private void compareResults(List<Object> list1, List<Object> list2, String executedOutput) {
        StringBuilder validityString = new StringBuilder();
        boolean testFailed = false;
        for (int i = 0; i < Objects.requireNonNull(list1).size(); i++) {
            assert list2 != null;
            if (!list1.get(i).equals(list2.get(i))) {
                testFailed = true;
                validityString.append(i).append(", ");
            }
        }
        if (testFailed) {
            validityString.append("\nOutput of test:\n ");
            validityString.append(executedOutput).append("\n");
            throw new IllegalStateException("Inequality with JSON Elements " + validityString);
        }
    }

    /**
     * Retrieves files based on a regex pattern
     * @param parentDir
     * @param regexPattern
     * @return
     */
    private File[] getFiles(File parentDir, String regexPattern) {
        Pattern pattern = Pattern.compile(regexPattern);
        File[] files = parentDir.listFiles((dir, name) -> pattern.matcher(name).matches());
        assert files != null;
        Arrays.sort(files, Comparator.comparing(File::getName));
        return files;
    }

//    @Deprecated
//    public void testFestRun(File testFestDirectory, Writer out) throws IOException, BadJsonException {
//        if (testFestDirectory.isDirectory()) {
//            File[] subDirs = testFestDirectory.listFiles(File::isDirectory);
//            assert subDirs != null;
//            Arrays.sort(subDirs, Comparator.comparing(File::getName));
//            for (File subDir : subDirs) {
//                runAllTests(subDir, out);
//            }
//        } else {
//            writeToOut(out,"Testing failed: " + testFestDirectory.getAbsolutePath() + " is not a directory.\n");
//        }
//    }
//
//    /**
//     * Iterates over files in a single directory, and writes all results and failures
//     * @param testDirectory
//     * @param out
//     * @throws IOException
//     */
//    @Deprecated
//    public void runAllTests(File testDirectory, Writer out) throws IOException, BadJsonException {
//        File[] inFiles = getFiles(testDirectory, "^(\\d+)-(in)\\.json$");
//        File[] outFiles = getFiles(testDirectory, "^(\\d+)-(out)\\.json$");
//        String dir = "Testing: " + getClass().getSimpleName() + " in directory " + testDirectory.getAbsolutePath() + "\n";
//        writeToOut(out, dir);
//        for (int i = 0; i < inFiles.length; i++) {
//            String testResult = runOneTest(new InputStreamReader(new FileInputStream(inFiles[i])), new InputStreamReader(new FileInputStream(outFiles[i])),
//                    new InputStreamReader(new FileInputStream(outFiles[i])), inFiles[i].getName(), dir);
//            writeToOut(out, testResult);
//        }
//    }
}