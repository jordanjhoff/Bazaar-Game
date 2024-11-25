package Runnables;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Common.converters.BadJsonException;
import Server.CommunicationUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class IntegrationTestFestRunner {

    ExecutorService executor;

    @Before
    public void setup() {
        this.executor = CommunicationUtils.createDaemonExecutor();
    }

    @Test
    public void runMilestone4() throws IOException {
        StringWriter output = new StringWriter();
        new TurnTester().parallelRun(new File("4/Tests"), output, executor);
        new TurnTester().paralleltestFestRun(new File("Feedback/4/Tests"), output, executor);
        System.out.println(output);
        Assert.assertFalse(output.toString().contains("failed"));

    }

    @Test
    public void runMilestone5() throws IOException {
        StringWriter output = new StringWriter();
        new StrategyTester().parallelRun(new File("5/Tests"), output, executor);
        new StrategyTester().paralleltestFestRun(new File("Feedback/5/Tests"), output, executor);
        System.out.println(output);
        Assert.assertFalse(output.toString().contains("failed"));

    }

    @Test
    public void runMilestone6() throws IOException {
        StringWriter output = new StringWriter();
        new RulesTester().parallelRun(new File("6/Tests"), output, executor);
        new RulesTester().paralleltestFestRun(new File("Feedback/6/Tests"), output, executor);
        System.out.println(output);
        Assert.assertFalse(output.toString().contains("failed"));
    }

    @Test
    public void runMilestone7() throws IOException {
        StringWriter output = new StringWriter();
        new GamesTester().parallelRun(new File("7/Tests"), output, executor);
        new GamesTester().paralleltestFestRun(new File("Feedback/7/Tests"), output, executor);
        System.out.println(output);
        Assert.assertFalse(output.toString().contains("failed"));
    }

    @Test
    public void runMilestone8() throws IOException {
        StringWriter output = new StringWriter();
        new ObserverGamesTester().parallelRun(new File("8/Tests"), output, executor);
        new ObserverGamesTester().paralleltestFestRun(new File("Feedback/8/Tests"), output, executor);
        System.out.println(output);
        Assert.assertFalse(output.toString().contains("failed"));
    }

    @Test
    public void runMilestone9() throws IOException {
        StringWriter output = new StringWriter();
        new ResourceGamesTester().parallelRun(new File("9/Tests"), output, executor);
        new ResourceGamesTester().paralleltestFestRun(new File("Feedback/9/Tests"), output, executor);
        System.out.println(output);
        Assert.assertFalse(output.toString().contains("failed"));
    }

    @After
    public void shutdownExecutor() {
        executor.shutdown();
    }

}
