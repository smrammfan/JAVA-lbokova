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
        MyThreadPool threadPool = runTasksInThreadsWithDefaultMaxQueueSize(listWithOneTask, TASK_DELAY_ZERO, THREADS_COUNT_ONE);
        assertThat(threadPool.getCountExecutedTasks()).as("Not all tasks were executed").isEqualTo(1);
    }

    //can run 1 task at 1 thread with positive delay
    @Test
    public void canRun1TaskAt1ThreadWithPositiveDelay() throws InterruptedException {
        MyThreadPool threadPool = runTasksInThreadsWithDefaultMaxQueueSize(listWithOneTask, TASK_DELAY_1000, THREADS_COUNT_ONE);
        assertThat(threadPool.getCountExecutedTasks()).as("Not all tasks were executed").isEqualTo(1);
    }

    //can run 5 tasks at 1 thread without delay
    @Test
    public void canRun5TasksAt1ThreadWithoutDelay() throws InterruptedException {
        MyThreadPool threadPool = runTasksInThreadsWithDefaultMaxQueueSize(listWith5Tasks, TASK_DELAY_ZERO, THREADS_COUNT_ONE);
        assertThat(threadPool.getCountExecutedTasks()).as("Not all tasks were executed").isEqualTo(5);
    }

    //can run 5 tasks at 1 thread with positive delay
    @Test
    public void canRun5TasksAt1ThreadWithPositiveDelay() throws InterruptedException {
        MyThreadPool threadPool = runTasksInThreadsWithDefaultMaxQueueSize(listWith5Tasks, TASK_DELAY_1000, THREADS_COUNT_ONE);
        assertThat(threadPool.getCountExecutedTasks()).as("Not all tasks were executed").isEqualTo(5);
    }

    //can run 4 tasks at 5 threads without delay
    @Test
    public void canRun4TasksAt5ThreadsWithoutDelay() throws InterruptedException {
        MyThreadPool threadPool = runTasksInThreadsWithDefaultMaxQueueSize(listWith4Tasks, TASK_DELAY_ZERO, THREADS_COUNT_FIVE);
        assertThat(threadPool.getCountExecutedTasks()).as("Not all tasks were executed").isEqualTo(4);
    }

    //can run 4 tasks at 5 threads with positive delay
    @Test
    public void canRun4TasksAt5ThreadsWithPositiveDelay() throws InterruptedException {
        MyThreadPool threadPool = runTasksInThreadsWithDefaultMaxQueueSize(listWith4Tasks, TASK_DELAY_1000, THREADS_COUNT_FIVE);
        assertThat(threadPool.getCountExecutedTasks()).as("Not all tasks were executed").isEqualTo(4);
    }

    //can run 7 tasks at 5 threads without delay
    @Test
    public void canRun7TasksAt5ThreadsWithoutDelay() throws InterruptedException {
        MyThreadPool threadPool = runTasksInThreadsWithDefaultMaxQueueSize(listWith7Tasks, TASK_DELAY_ZERO, THREADS_COUNT_FIVE);
        assertThat(threadPool.getCountExecutedTasks()).as("Not all tasks were executed").isEqualTo(7);
    }

    //can run 7 tasks at 5 threads with positive delay
    @Test
    public void canRun7TasksAt5ThreadsWithPositiveDelay() throws InterruptedException {
        MyThreadPool threadPool = runTasksInThreadsWithDefaultMaxQueueSize(listWith7Tasks, TASK_DELAY_1000, THREADS_COUNT_FIVE);
        assertThat(threadPool.getCountExecutedTasks()).as("Not all tasks were executed").isEqualTo(7);
    }

    //can run tasks with empty task name
    @Test
    public void canRunTaskWithEmptyTaskName() throws InterruptedException {
        MyThreadPool threadPool = runTasksInThreadsWithDefaultMaxQueueSize(listWithOneTaskWithEmptyName, TASK_DELAY_1000, THREADS_COUNT_ONE);
        assertThat(threadPool.getCountExecutedTasks()).as("Not all tasks were executed").isEqualTo(1);
    }

    //can run tasks with positive numbers in task name
    @Test
    public void canRunTaskWithPositiveNumbersInTaskName() throws InterruptedException {
        MyThreadPool threadPool = runTasksInThreadsWithDefaultMaxQueueSize(listWithOneTaskWithPositiveNumInName, TASK_DELAY_1000, THREADS_COUNT_ONE);
        assertThat(threadPool.getCountExecutedTasks()).as("Not all tasks were executed").isEqualTo(1);
    }

    //can run tasks with negative number in task name
    @Test
    public void canRunTaskWithNegativeNumbersInTaskName() throws InterruptedException {
        MyThreadPool threadPool = runTasksInThreadsWithDefaultMaxQueueSize(listWithOneTaskWithNegativeNumInName, TASK_DELAY_1000, THREADS_COUNT_ONE);
        assertThat(threadPool.getCountExecutedTasks()).as("Not all tasks were executed").isEqualTo(1);
    }

    //can run tasks with symbols in name
    @Test
    public void canRunTaskWithSymbolsInTaskName() throws InterruptedException {
        MyThreadPool threadPool = runTasksInThreadsWithDefaultMaxQueueSize(listWithOneTaskWithSymbolsInName, TASK_DELAY_1000, THREADS_COUNT_ONE);
        assertThat(threadPool.getCountExecutedTasks()).as("Not all tasks were executed").isEqualTo(1);
    }

    //can run tasks with queue size more then count of tasks
    @Test
    public void canRunTasksWithQueueSizeMoreThenTasksCount() throws InterruptedException {
        MyThreadPool threadPool = runTasksInThreadsWithSpecifiedMaxQueueSize(listWithOneTask, TASK_DELAY_ZERO, MAX_QUEUE_SIZE_TWO, THREADS_COUNT_ONE);
        assertThat(threadPool.getCountExecutedTasks()).as("Not all tasks were executed").isEqualTo(1);
    }

    //can run tasks with queue size less then count of tasks
    @Test
    public void canRunTasksWithQueueSizeLessThenTasksCount() throws InterruptedException {
        MyThreadPool threadPool = runTasksInThreadsWithSpecifiedMaxQueueSize(listWith4Tasks, TASK_DELAY_ZERO, MAX_QUEUE_SIZE_TWO, THREADS_COUNT_FIVE);
        assertThat(threadPool.getCountExecutedTasks()).as("Not all tasks were executed").isEqualTo(4);
    }

    //can run with shutdown before tasks completion when tasks < threads and shutdown on task < threds count
    @Test
    public void canRunWithShutdownBeforeTasksCompletionWhenTasksLessThenThreadsAndShutdownMoreThreads() throws InterruptedException, ThreadPoolException {
        int taskNumToSutdown = TASK_NUMBER_TO_SHUTDOWN_3;
        MyThreadPool threadPool = Main.runTasksInThreadPoolWithShutdownBeforeTasksCompletion(MAX_QUEUE_SIZE_TWO, THREADS_COUNT_FIVE, TASKS_COUNT_4, TASK_DELAY_ZERO, taskNumToSutdown);
        assertThat(threadPool.getCountExecutedTasks()).as("Not all tasks were executed").isEqualTo(taskNumToSutdown - 1);
    }

    //can run with shutdown before tasks completion when tasks > threads and shutdown on task > threds count
    @Test
    public void canRunWithShutdownBeforeTasksCompletionWhenTasksMoreThenThreads() throws InterruptedException, ThreadPoolException {
        int taskNumToSutdown = TASK_NUMBER_TO_SHUTDOWN_7;
        MyThreadPool threadPool = Main.runTasksInThreadPoolWithShutdownBeforeTasksCompletion(MAX_QUEUE_SIZE_TWO, THREADS_COUNT_FIVE, TASKS_COUNT_7, TASK_DELAY_ZERO, taskNumToSutdown);
        assertThat(threadPool.getCountExecutedTasks()).as("Not all tasks were executed").isEqualTo(taskNumToSutdown - 1);
    }

    //can run with shutdown before tasks completion when tasks < threads and shutdown on task < threds count
    @Test
    public void canRunWithShutdownBeforeTasksCompletionWhenTasksLessThenThreadsAndShutdownLessThreads() throws InterruptedException, ThreadPoolException {
        int taskNumToSutdown = TASK_NUMBER_TO_SHUTDOWN_3;
        MyThreadPool threadPool = Main.runTasksInThreadPoolWithShutdownBeforeTasksCompletion(MAX_QUEUE_SIZE_TWO, THREADS_COUNT_FIVE, TASKS_COUNT_4, TASK_DELAY_ZERO, taskNumToSutdown);
        assertThat(threadPool.getCountExecutedTasks()).as("Not all tasks were executed").isEqualTo(taskNumToSutdown - 1);
    }

    //can run with shutdown before tasks completion when tasks > threads and shutdown on task > threds count
    @Test
    public void canRunWithShutdownBeforeTasksCompletionWhenTasksMoreThenThreadsAndShutdownMoreThreads() throws InterruptedException, ThreadPoolException {
        int taskNumToSutdown = TASK_NUMBER_TO_SHUTDOWN_7;
        MyThreadPool threadPool = Main.runTasksInThreadPoolWithShutdownBeforeTasksCompletion(MAX_QUEUE_SIZE_TWO, THREADS_COUNT_FIVE, TASKS_COUNT_7, TASK_DELAY_ZERO, taskNumToSutdown);
        assertThat(threadPool.getCountExecutedTasks()).as("Not all tasks were executed").isEqualTo(taskNumToSutdown - 1);
    }

    //can run with shutdown after tasks completion when tasks < threads
    @Test
    public void canRunWithShutdownAfterTasksCompletionWhenTasksLessThenThreads() throws InterruptedException {
        MyThreadPool threadPool = Main.runTasksInThreadPoolWithShutdownAfterTasksCompletion(MAX_QUEUE_SIZE_TWO, THREADS_COUNT_FIVE, TASKS_COUNT_4, TASK_DELAY_ZERO);
        assertThat(threadPool.getCountExecutedTasks()).as("Not all tasks were executed").isEqualTo(TASKS_COUNT_4);
    }

    //can run with shutdown after tasks completion when tasks > threads
    @Test
    public void canRunWithShutdownAfterTasksCompletionWhenTasksMoreThenThreads() throws InterruptedException {
        MyThreadPool threadPool = Main.runTasksInThreadPoolWithShutdownAfterTasksCompletion(MAX_QUEUE_SIZE_TWO, THREADS_COUNT_FIVE, TASKS_COUNT_7, TASK_DELAY_ZERO);
        assertThat(threadPool.getCountExecutedTasks()).as("Not all tasks were executed").isEqualTo(TASKS_COUNT_7);
    }

    //---Negative tests

    //can't run 0 threads
    @Test(expected = IllegalArgumentException.class)
    public void canNotRunTasksWithZeroThreads() throws InterruptedException {
        listWithOneNullTask.add(null);
        runTasksInThreadsWithDefaultMaxQueueSize(listWithOneTask, TASK_DELAY_ZERO, THREADS_COUNT_ZERO);
    }

    //can't run -1 threads
    @Test(expected = IllegalArgumentException.class)
    public void canNotRunTasksWithNegativeThreads() throws InterruptedException {
        runTasksInThreadsWithDefaultMaxQueueSize(listWithOneTask, TASK_DELAY_ZERO, THREADS_COUNT_MINUS_ONE);
    }

    //can't run 0 queue size
    @Test(expected = IllegalArgumentException.class)
    public void canNotRunTasksWithMaxZeroTasksQueue() throws InterruptedException {
        runTasksInThreadsWithSpecifiedMaxQueueSize(listWith5Tasks, TASK_DELAY_ZERO, MAX_QUEUE_SIZE_ZERO, THREADS_COUNT_FIVE);
    }

    //can't run -1 queue size
    @Test(expected = IllegalArgumentException.class)
    public void canNotRunTasksWithNegativeZeroTasksQueue() throws InterruptedException {
        runTasksInThreadsWithSpecifiedMaxQueueSize(listWith5Tasks, TASK_DELAY_ZERO, MAX_QUEUE_SIZE_MINUS_ONE, THREADS_COUNT_FIVE);
    }

    //can't run null task
    @Test(expected = IllegalArgumentException.class)
    public void canNotRunNullTasks() throws InterruptedException {
        runTasksInThreadsWithDefaultMaxQueueSize(listWithOneNullTask, TASK_DELAY_ZERO, THREADS_COUNT_ONE);
    }

    //can't run with negative delay
    @Test(expected = IllegalArgumentException.class)
    public void canNotRunTasksWithNegativeDelay() throws InterruptedException {
        runTasksInThreadsWithDefaultMaxQueueSize(listWithOneTask, TASK_DELAY_MINUS_ONE, THREADS_COUNT_ONE);
    }

    //can't submit tasks after shutdown after waiting
    @Test(expected = ThreadPoolException.class)
    public void canNotSubmitTasksAfterShutDownAfterWaiting() throws InterruptedException, ThreadPoolException {
        MyThreadPool threadPool = Main.runTasksInThreadPoolWithShutdownAfterTasksCompletion(MAX_QUEUE_SIZE_TWO, THREADS_COUNT_FIVE, TASKS_COUNT_1, TASK_DELAY_ZERO);
        threadPool.submit(new SomeTask(), TASK_DELAY_ZERO);
    }

    //can't submit tasks after shutdown before waiting
    @Test(expected = ThreadPoolException.class)
    public void canNotSubmitTasksAfterShutDownBeforeWaiting() throws InterruptedException, ThreadPoolException {
        MyThreadPool threadPool = Main.runTasksInThreadPoolWithShutdownBeforeTasksCompletion(MAX_QUEUE_SIZE_TWO, THREADS_COUNT_FIVE, TASKS_COUNT_4, TASK_DELAY_ZERO, TASK_NUMBER_TO_SHUTDOWN_3);
        threadPool.submit(new SomeTask(), TASK_DELAY_ZERO);
    }

    //------------ Base methods

    private MyThreadPool runTasksInThreadsWithDefaultMaxQueueSize(List<Runnable> tasks, Integer taskDelay, int threadsCount) throws InterruptedException {
        int defaultTasksQueueSize = tasks.size();
        return runTasksInThreadsWithSpecifiedMaxQueueSize(tasks, taskDelay, defaultTasksQueueSize, threadsCount);
    }

    private MyThreadPool runTasksInThreadsWithSpecifiedMaxQueueSize(List<Runnable> tasks, Integer taskDelay, int tasksQueueMaxSize,
                                                                    int threadsCount) throws InterruptedException {
        tasks.forEach(task -> tasksWithSpecifiedDelay.put(task, taskDelay));
        return Main.runTasksListInThreadPool(tasksWithSpecifiedDelay, tasksQueueMaxSize, threadsCount);
    }
}
