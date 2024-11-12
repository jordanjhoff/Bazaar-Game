package Runnables;

import Common.converters.BadJsonException;

import java.io.*;
import java.nio.charset.StandardCharsets;
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
    public void run(File testDirectory, Writer out, Writer failures) throws IOException, BadJsonException {
        File[] inFiles = getFiles(testDirectory, "^(\\d+)-(in)\\.json$");
        File[] outFiles = getFiles(testDirectory, "^(\\d+)-(out)\\.json$");
        String dir = "Testing: " + getClass().getSimpleName() + " in directory " + testDirectory.getAbsolutePath() + "\n";
        out.write(dir);
        for (int i = 0; i < inFiles.length; i++) {
            InputStreamReader testInput = new InputStreamReader(new FileInputStream(inFiles[i]));
            InputStreamReader expectedOutput = new InputStreamReader(new FileInputStream(outFiles[i]));
            String testResult = result(testInput, expectedOutput, inFiles[i].getName());
            out.write(testResult);
            if (testResult.contains("failed")) {
                failures.write(dir);
                failures.write(testResult);
                out.write("Expected Result: \n");
                String expectedResult = new BufferedReader(new InputStreamReader(new FileInputStream(outFiles[i])))
                        .lines().collect(Collectors.joining("\n")) + "\n\n";
                out.write(expectedResult);
                failures.write("Expected Result: \n");
                failures.write(expectedResult);
            }
        }
        out.flush();
    }

    public void parallelRun(File testDirectory, Writer out, Writer failures) throws IOException, InterruptedException {
        File[] inFiles = getFiles(testDirectory, "^(\\d+)-(in)\\.json$");
        File[] outFiles = getFiles(testDirectory, "^(\\d+)-(out)\\.json$");
        String dir = "Testing: " + getClass().getSimpleName() + " in directory " + testDirectory.getAbsolutePath() + "\n";
        out.write(dir);

        int threadCount = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < inFiles.length; i++) {
            final int index = i;
            Future<String> future = executor.submit(() -> {
                StringBuilder testOutput = new StringBuilder();
                InputStreamReader testInput = new InputStreamReader(new FileInputStream(inFiles[index]));
                InputStreamReader expectedOutput = new InputStreamReader(new FileInputStream(outFiles[index]));
                String testResult = result(testInput, expectedOutput, inFiles[index].getName());
                testOutput.append(testResult);

                if (testResult.contains("failed")) {
                    testOutput.append(dir);
                    testOutput.append(testResult);
                    testOutput.append("Expected Result: \n");

                    String expectedResult = new BufferedReader(new InputStreamReader(new FileInputStream(outFiles[index])))
                            .lines().collect(Collectors.joining("\n")) + "\n\n";
                    testOutput.append(expectedResult);
                }
                return testOutput.toString();
            });
            futures.add(future);
        }

        for (Future<String> future : futures) {
            try {
                String result = future.get();
                out.write(result);
                if (result.contains("failed")) {
                    failures.write(result);
                }
            }
            catch (ExecutionException | InterruptedException e) {
                failures.write(e.getMessage());
            }
        }
        executor.shutdown();
    }

    public void testFestRun(File testFestDirectory, Writer out, Writer failures) throws IOException, BadJsonException {
        if (testFestDirectory.isDirectory()) {
            File[] subDirs = testFestDirectory.listFiles(File::isDirectory);
            assert subDirs != null;
            Arrays.sort(subDirs, Comparator.comparing(file -> parseIntOrDefault(file.getName())));
            for (File subDir : subDirs) {
                run(subDir, out, failures);
            }
        } else {
            String message = "Testing failed: " + testFestDirectory.getAbsolutePath() + " is not a directory.\n";
            out.write(message);
            failures.write(message);
        }
        out.flush();
    }

    /**
     * Takes an (n)-in.json and an (n)-out.json file, and compares them.
     * @param testInput
     * @param expectedTestOutput
     * @param testName
     * @return a string of the result
     * @throws IOException
     */
    public String result(InputStreamReader testInput, InputStreamReader expectedTestOutput, String testName) throws IOException, BadJsonException {
        try {
            StringWriter actualTestOutput = new StringWriter();
            List<Object> list1 = jsonResultToObjects(testResultToNewReader(testInput, actualTestOutput));
            List<Object> list2 = jsonResultToObjects(expectedTestOutput);
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
                validityString.append(actualTestOutput).append("\n");
                throw new IllegalStateException("Inequality with JSON Elements " + validityString);
            }
        }
        catch (Exception e) {
            return "Test " + testName + " failed by exception: " + e.getMessage() + "\n";
        }
        return "Test " + testName + " passed\n";
    }

    private File[] getFiles(File parentDir, String regexPattern) {
        Pattern pattern = Pattern.compile(regexPattern);
        File[] files = parentDir.listFiles((dir, name) -> pattern.matcher(name).matches());
        assert files != null;
        Arrays.sort(files, Comparator.comparing(File::getName));
        return files;
    }

    /*
    Takes an input, runs the test, and writes the result to out. Additionally, returns the output as a new reader for processing.
     */
    private InputStreamReader testResultToNewReader(InputStreamReader testInput, Writer testOut) throws IOException, BadJsonException {
        StringWriter outputOftest = new StringWriter();
        runTest(testInput, outputOftest);
        testOut.write(outputOftest.toString());
        return getInputStreamReaderFromWriter(outputOftest);
    }

    abstract void runTest(InputStreamReader testInput, StringWriter testOutput) throws IOException, BadJsonException;

    private InputStreamReader getInputStreamReaderFromWriter(Writer writer) {
        StringWriter stringWriter = (StringWriter) writer;
        String content = stringWriter.toString();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        return new InputStreamReader(byteArrayInputStream, StandardCharsets.UTF_8);
    }

    public abstract List<Object> jsonResultToObjects(InputStreamReader input) throws BadJsonException;

    private static int parseIntOrDefault(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

}