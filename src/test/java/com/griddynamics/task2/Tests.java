package com.griddynamics.task2;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


public class Tests {

    private Map<Runnable, Integer> tasksWithSpecifiedDelay = new LinkedHashMap<>();
    private static final List<Runnable> listWithOneTask = new ArrayList<>(Arrays.asList(new SomeTask()));
    private static final List<Runnable> listWithOneNullTask = new ArrayList<>();
    private static final List<Runnable> listWithOneTaskWithEmptyName = new ArrayList<>(Arrays.asList(new SomeTask("")));
    private static final List<Runnable> listWithOneTaskWithPositiveNumInName = new ArrayList<>(Arrays.asList(new SomeTask("12345")));
    private static final List<Runnable> listWithOneTaskWithNegativeNumInName = new ArrayList<>(Arrays.asList(new SomeTask("-12345")));
    private static final List<Runnable> listWithOneTaskWithSymbolsInName = new ArrayList<>(Arrays.asList(new SomeTask("!@_\"1")));
    private static final List<Runnable> listWith4Tasks = new ArrayList<>(Arrays.asList(new SomeTask(), new SomeTask(), new SomeTask(), new SomeTask()));
    private static final List<Runnable> listWith5Tasks = new ArrayList<>(Arrays.asList(new SomeTask(), new SomeTask(), new SomeTask(), new SomeTask(), new SomeTask()));
    private static final List<Runnable> listWith7Tasks = new ArrayList<>(Arrays.asList(new SomeTask(), new SomeTask(), new SomeTask(), new SomeTask(), new SomeTask(), new SomeTask(), new SomeTask()));
    private static final Integer TASK_DELAY_MINUS_ONE = -1;
    private static final Integer TASK_DELAY_ZERO = 0;
    private static final Integer TASK_DELAY_1000 = 1000;
    private static final int TASKS_COUNT_1 = 1;
    private static final int TASKS_COUNT_4 = 4;
    private static final int TASKS_COUNT_7 = 7;
    private static final int TASK_NUMBER_TO_SHUTDOWN_3 = 3;
    private static final int TASK_NUMBER_TO_SHUTDOWN_7 = 7;
    private static final Integer THREADS_COUNT_MINUS_ONE = -1;
    private static final Integer THREADS_COUNT_ZERO = 0;
    private static final Integer THREADS_COUNT_ONE = 1;
    private static final Integer THREADS_COUNT_FIVE = 5;
    private static final Integer MAX_QUEUE_SIZE_MINUS_ONE = -1;
    private static final Integer MAX_QUEUE_SIZE_ZERO = 0;
    private static final Integer MAX_QUEUE_SIZE_TWO = 2;

    @Before
    public void init() {
        tasksWithSpecifiedDelay.clear();
        listWithOneNullTask.clear();
    }

    //---Positive tests

    //can run 1 task at 1 thread without delay
    @Test
    public void canRun1TaskAt1ThreadWithoutDelay() throws InterruptedException {
        int threadsCount = 1;
        int tasksCount = 1;
        int delay = 0;
        Main.runTasks(threadsCount, tasksCount, delay);
    }

    //can run 1 task at 1 thread with positive delay
    @Test
    public void canRun1TaskAt1ThreadWithPositiveDelay() throws InterruptedException {
        int threadsCount = 1;
        int tasksCount = 1;
        int delay = 1000;
        Main.runTasks(threadsCount, tasksCount, delay);
    }

    //can run 5 tasks at 1 thread without delay
    @Test
    public void canRun5TasksAt1ThreadWithoutDelay() throws InterruptedException {
        int threadsCount = 1;
        int tasksCount = 5;
        int delay = 0;
        Main.runTasks(threadsCount, tasksCount, delay);
    }

    //can run 5 tasks at 1 thread with positive delay
    @Test
    public void canRun5TasksAt1ThreadWithPositiveDelay() throws InterruptedException {
        int threadsCount = 1;
        int tasksCount = 5;
        int delay = 1000;
        Main.runTasks(threadsCount, tasksCount, delay);
    }

    //can run 4 tasks at 5 threads without delay
    @Test
    public void canRun4TasksAt5ThreadsWithoutDelay() throws InterruptedException {
        int threadsCount = 5;
        int tasksCount = 4;
        int delay = 0;
        Main.runTasks(threadsCount, tasksCount, delay);
    }

    //can run 4 tasks at 5 threads with positive delay
    @Test
    public void canRun4TasksAt5ThreadsWithPositiveDelay() throws InterruptedException {
        int threadsCount = 5;
        int tasksCount = 4;
        int delay = 1000;
        Main.runTasks(threadsCount, tasksCount, delay);
    }

    //can run 7 tasks at 5 threads without delay
    @Test
    public void canRun7TasksAt5ThreadsWithoutDelay() throws InterruptedException {
        int threadsCount = 5;
        int tasksCount = 7;
        int delay = 0;
        Main.runTasks(threadsCount, tasksCount, delay);
    }

    //can run 7 tasks at 5 threads with positive delay
    @Test
    public void canRun7TasksAt5ThreadsWithPositiveDelay() throws InterruptedException {
        int threadsCount = 5;
        int tasksCount = 7;
        int delay = 1000;
        Main.runTasks(threadsCount, tasksCount, delay);
    }

    //---Negative tests

    //can't run 0 threads
    @Test(expected = IllegalArgumentException.class)
    public void canNotRunTasksWithZeroThreads() throws InterruptedException {
        int threadsCount = 0;
        int tasksCount = 7;
        int delay = 1000;
        Main.runTasks(threadsCount, tasksCount, delay);
    }

    //can't run -1 threads
    @Test(expected = IllegalArgumentException.class)
    public void canNotRunTasksWithNegativeThreads() throws InterruptedException {
        int threadsCount = -1;
        int tasksCount = 7;
        int delay = 1000;
        Main.runTasks(threadsCount, tasksCount, delay);
    }

    //can't run with negative delay
    @Test(expected = IllegalArgumentException.class)
    public void canNotRunTasksWithNegativeDelay() throws InterruptedException {
        int threadsCount = 5;
        int tasksCount = 7;
        int delay = -1000;
        Main.runTasks(threadsCount, tasksCount, delay);
    }

    //can't run with too long delay
    @Test(expected = IllegalArgumentException.class)
    public void canNotRunTasksWithLongDelay() throws InterruptedException {
        int threadsCount = 5;
        int tasksCount = 7;
        int delay = 10000000;
        Main.runTasks(threadsCount, tasksCount, delay);
    }
}
