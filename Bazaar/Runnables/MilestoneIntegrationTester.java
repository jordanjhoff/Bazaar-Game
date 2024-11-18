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
     * Iterates over files in a single directory, and writes all results and failures
     * @param testDirectory
     * @param out
     * @param failures
     * @throws IOException
     */
    public void runAllTests(File testDirectory, Writer out, Writer failures) throws IOException, BadJsonException {
        File[] inFiles = getFiles(testDirectory, "^(\\d+)-(in)\\.json$");
        File[] outFiles = getFiles(testDirectory, "^(\\d+)-(out)\\.json$");
        String dir = "Testing: " + getClass().getSimpleName() + " in directory " + testDirectory.getAbsolutePath() + "\n";
        out.write(dir);
        for (int i = 0; i < inFiles.length; i++) {
            String testResult = runOneTest(new InputStreamReader(new FileInputStream(inFiles[i])), new InputStreamReader(new FileInputStream(outFiles[i])),
                    new InputStreamReader(new FileInputStream(outFiles[i])), inFiles[i].getName());
            out.write(testResult);
            if (testResult.contains("failed")) {
                failures.write(dir);
                failures.write(testResult);
            }
        }
        failures.flush();
        out.flush();
    }

    /**
     * Iterates over files in a single directory runs them in parallel, and writes all results and failures
     * @param testDirectory
     * @param out
     * @param failures
     * @throws IOException
     */
    public void parallelRun(File testDirectory, Writer out, Writer failures, ExecutorService executor) throws IOException, InterruptedException {
        File[] inFiles = getFiles(testDirectory, "^(\\d+)-(in)\\.json$");
        File[] outFiles = getFiles(testDirectory, "^(\\d+)-(out)\\.json$");

        List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < inFiles.length; i++) {
            final int index = i;
            Future<String> future = executor.submit(() -> runOneTest(new InputStreamReader(new FileInputStream(inFiles[index])),
                        new InputStreamReader(new FileInputStream(outFiles[index])),
                        new InputStreamReader(new FileInputStream(outFiles[index])), inFiles[index].getName()));
            futures.add(future);
        }
        synchronized (out) {
            String dir = getClass().getSimpleName() + " in directory " + testDirectory.getAbsolutePath() + ":\n";
            out.write(dir);
            for (Future<String> future : futures) {
                try {
                    String result = future.get();
                    out.write(result);
                    if (result.contains("failed")) {
                        failures.write(dir);
                        failures.write(result);
                    }
                } catch (ExecutionException | InterruptedException e) {
                    failures.write(e.getMessage());
                }
            }
        }
        executor.shutdown();
    }

    public void testFestRun(File testFestDirectory, Writer out, Writer failures) throws IOException, BadJsonException {
        if (testFestDirectory.isDirectory()) {
            File[] subDirs = testFestDirectory.listFiles(File::isDirectory);
            assert subDirs != null;
            Arrays.sort(subDirs, Comparator.comparing(File::getName));
            for (File subDir : subDirs) {
                runAllTests(subDir, out, failures);
            }
        } else {
            String message = "Testing failed: " + testFestDirectory.getAbsolutePath() + " is not a directory.\n";
            out.write(message);
            failures.write(message);
        }
        out.flush();
    }

    public void paralleltestFestRun(File testFestDirectory, Writer out, Writer failures, ExecutorService executor) throws IOException, BadJsonException {
        if (testFestDirectory.isDirectory()) {
            File[] subDirs = testFestDirectory.listFiles(File::isDirectory);
            assert subDirs != null;

            List<Future<Void>> futures = new ArrayList<>();
            for (File subDir : subDirs) {
                Future<Void> future = executor.submit(() -> {
                    parallelRun(subDir, out, failures, executor);
                    return null;
                });
                futures.add(future);
            }

            for (Future<Void> future : futures) {
                try {
                    future.get();
                } catch (ExecutionException | InterruptedException e) {
                    synchronized (out) {
                        failures.write("Error processing directories: " + e.getMessage() + "\n");
                    }
                }
            }

            executor.shutdown();
        } else {
            String message = "Testing failed: " + testFestDirectory.getAbsolutePath() + " is not a directory.\n";
            synchronized (out) {
                out.write(message);
            }
            synchronized (failures) {
                failures.write(message);
            }
        }

        // Ensure all data is flushed at the end
        synchronized (out) {
            out.flush();
        }
        synchronized (failures) {
            failures.flush();
        }
    }

    private String executeAndCompare(InputStreamReader testInput, InputStreamReader expectedTestOutput, String testName) throws IOException, BadJsonException {
        try {
            StringWriter executedOutput = new StringWriter();
            List<Object> list1 = runTest(testInput, executedOutput);
            List<Object> list2 = jsonResultToObjects(expectedTestOutput);
            compareResults(list1, list2, executedOutput.toString());
        }
        catch (Exception e) {
            return "Test " + testName + " failed by exception: " + e.getMessage() + "\n";
        }
        return "Test " + testName + " passed\n";
    }

    abstract List<Object> runTest(InputStreamReader testInput, StringWriter testOutput) throws IOException, BadJsonException;

    public abstract List<Object> jsonResultToObjects(InputStreamReader input) throws BadJsonException;

    /**
     * Takes an (n)-in.json and an (n)-out.json file, and compares them. Takes an additional copy of the expected output
     * to write in case of failure.
     * @param testInput
     * @param expectedOutput
     * @param testName
     * @return a string of the result
     * @throws IOException
     */
    private String runOneTest(InputStreamReader testInput, InputStreamReader expectedOutput, InputStreamReader expectedTestOutput2, String testName) throws IOException, BadJsonException {
        StringBuilder testOutput = new StringBuilder();
        String testResult = executeAndCompare(testInput, expectedOutput, testName);
        testOutput.append(testResult);

        if (testResult.contains("failed")) {
            testOutput.append("Expected Result: \n");

            String expectedResult = new BufferedReader(expectedTestOutput2)
                    .lines().collect(Collectors.joining("\n")) + "\n\n";
            testOutput.append(expectedResult);
        }
        return testOutput.toString();
    }

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

    private File[] getFiles(File parentDir, String regexPattern) {
        Pattern pattern = Pattern.compile(regexPattern);
        File[] files = parentDir.listFiles((dir, name) -> pattern.matcher(name).matches());
        assert files != null;
        Arrays.sort(files, Comparator.comparing(File::getName));
        return files;
    }

}