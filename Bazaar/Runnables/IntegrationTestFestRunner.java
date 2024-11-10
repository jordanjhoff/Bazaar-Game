package Runnables;

import Common.converters.BadJsonException;

import java.io.*;

public class IntegrationTestFestRunner {
    public static void main(String[] args) throws IOException, BadJsonException {
        StringWriter milestone = new StringWriter();
        StringWriter testfest = new StringWriter();
        runMilestoneTests(milestone);
        runTestfestTests(testfest);

        //View all test results
        System.out.println(milestone);
        System.out.println(testfest);
        milestone.close();
        testfest.close();
    }

    public static void runMilestoneTests(Writer output) throws IOException, BadJsonException {
        StringWriter failures = new StringWriter();
        new TurnTester().run(new File("4/Tests"), output, failures);
        new StrategyTester().run(new File("5/Tests"), output, failures);
        new RulesTester().run(new File("6/Tests"), output, failures);
        new GamesTester().run(new File("7/Tests"), output, failures);
        new ObserverGamesTester().run(new File("8/Tests"), output, failures);
        if (output.toString().contains("failed")) {
            System.out.println("--------------Milestone Tests Failed------------------");
            System.out.println(failures);
        }
        else {
            System.out.println("-------------------All Milestone Tests Passed--------------------");
        }
    }

    public static void runTestfestTests(Writer output) throws IOException, BadJsonException {
        StringWriter failures = new StringWriter();
        new TurnTester().testFestRun(new File("Feedback/4/Tests"), output, failures);
        new StrategyTester().testFestRun(new File("Feedback/5/Tests"), output, failures);
        new RulesTester().testFestRun(new File("Feedback/6/Tests"), output, failures);
        new GamesTester().testFestRun(new File("Feedback/7/Tests"), output, failures);
        // new ObserverGamesTester().testFestRun(new File("Feedback/8/Tests"), output, failures);
        if (output.toString().contains("failed")) {
            System.out.println("----------------------TestFest Tests Failed---------------------------");
            System.out.println(failures);
        }
        else {
            System.out.println("------------------------All TestFest Tests Passed--------------------------");
        }
    }
}
