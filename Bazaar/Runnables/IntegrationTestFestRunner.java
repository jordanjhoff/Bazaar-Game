package Runnables;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Common.converters.BadJsonException;

public class IntegrationTestFestRunner {
    public static void main(String[] args) throws IOException, BadJsonException, InterruptedException {
        StringWriter milestone = new StringWriter();
        StringWriter testfest = new StringWriter();
        runMilestoneTests(milestone);
        runTestfestTests(testfest);

        //View all test results
        //System.out.println(milestone);
       // System.out.println(testfest);
        milestone.close();
        testfest.close();
    }

    public static void runMilestoneTests(Writer output) throws IOException, BadJsonException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        StringWriter failures = new StringWriter();
        new TurnTester().runAllTests(new File("4/Tests"), output, failures);
        new StrategyTester().runAllTests(new File("5/Tests"), output, failures);
        new RulesTester().runAllTests(new File("6/Tests"), output, failures);
        new GamesTester().runAllTests(new File("7/Tests"), output, failures);
        new ObserverGamesTester().runAllTests(new File("8/Tests"), output, failures);
        new ResourceGamesTester().parallelRun(new File("9/Tests"), output, failures, executor);
        if (output.toString().contains("failed")) {
            System.out.println("--------------Milestone Tests Failed------------------");
            System.out.println(failures);
        }
        else {
            System.out.println("-------------------All Milestone Tests Passed--------------------");
        }
    }

    public static void runTestfestTests(Writer output) throws IOException, BadJsonException {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        StringWriter failures = new StringWriter();
        new TurnTester().testFestRun(new File("Feedback/4/Tests"), output, failures);
        new StrategyTester().testFestRun(new File("Feedback/5/Tests"), output, failures);
        new RulesTester().testFestRun(new File("Feedback/6/Tests"), output, failures);
        new GamesTester().testFestRun(new File("Feedback/7/Tests"), output, failures);
        new ObserverGamesTester().testFestRun(new File("Feedback/8/Tests"), output, failures);
        new ResourceGamesTester().paralleltestFestRun(new File("Feedback/9/Tests"), output, failures, executor);
        if (failures.toString().contains("failed")) {
            System.out.println("----------------------TestFest Tests Failed---------------------------");
            System.out.println(failures);
        }
        else {
            System.out.println("---------------------- All TestFest Tests Passed------------------------");
        }
    }
}
