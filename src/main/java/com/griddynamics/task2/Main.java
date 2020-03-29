package com.griddynamics.task2;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;

public class Main {

    public static final int THREADS_COUNT = 5;
    public static final int TASKS_COUNT = 8;//3
    public static final int TASKS_QUEUE_MAX_SIZE = 3;//6
    public static final int TASK_DELAY = 1000;
    public static final int TASK_NUMBER_TO_SHUTDOWN = 3;//2
    public static final int FOREVER = 0;
    public static final String OPTION_SHUTDOWN_AFTER_TASKS_COMPLETION = "-a";
    public static final String OPTION_SHUTDOWN_BEFORE_TASKS_COMPLETION = "-b";

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException, IllegalArgumentException, ThreadPoolException {
        String option = args.length > 0 ? args[0] : OPTION_SHUTDOWN_AFTER_TASKS_COMPLETION;
        switch (option) {
            case OPTION_SHUTDOWN_AFTER_TASKS_COMPLETION:
                runTasksInThreadPoolWithShutdownAfterTasksCompletion(TASKS_QUEUE_MAX_SIZE, THREADS_COUNT, TASKS_COUNT, TASK_DELAY);
                break;
            case OPTION_SHUTDOWN_BEFORE_TASKS_COMPLETION:
                runTasksInThreadPoolWithShutdownBeforeTasksCompletion(TASKS_QUEUE_MAX_SIZE, THREADS_COUNT, TASKS_COUNT, TASK_DELAY, TASK_NUMBER_TO_SHUTDOWN);
                break;
            default:
                LOGGER.error("Unsupported option specified: " + option);
                throw new IllegalArgumentException("Unsupported option specified: " + option);
        }
    }

    public static MyThreadPool runTasksInThreadPoolWithShutdownAfterTasksCompletion(int tasksQueueMaxSize, int threadsCount,
                                                                                    int tasksCount, int taskDelay)
            throws IllegalArgumentException {
        Map<Runnable, Integer> tasksList = new LinkedHashMap<>();
        for (int taskNumber = 1; taskNumber <= tasksCount; taskNumber++) {
            SomeTask task = new SomeTask(String.valueOf(taskNumber));
            tasksList.put(task, taskDelay * taskNumber);
        }
        return runTasksListInThreadPool(tasksList, tasksQueueMaxSize, threadsCount);
    }

    public static MyThreadPool runTasksInThreadPoolWithShutdownBeforeTasksCompletion(int tasksQueueMaxSize, int threadsCount,
                                                                                     int tasksCount, int taskDelay, int taskForShutdown)
            throws InterruptedException, IllegalArgumentException, ThreadPoolException {
        if (taskForShutdown < 1 || taskForShutdown > tasksCount) {
            throw new IllegalArgumentException("Task number for shutdown should be more then 0 and less then tasks count");
        }

        MyThreadPool threadPool = new MyThreadPool(tasksQueueMaxSize, threadsCount);
        for (int taskNumber = 1; taskNumber <= tasksCount; taskNumber++) {
            SomeTask task = new SomeTask(String.valueOf(taskNumber));
            if (taskNumber == taskForShutdown) {
                threadPool.shutdown();
                break;
            } else {
                threadPool.submit(task, taskDelay * taskNumber);
            }
        }
        threadPool.waitTasksCompletion(FOREVER);
        //threadPool.stopAllActiveThreads();
        System.out.println("____The end!____");
        System.out.println("Count of executed tasks: " + threadPool.getCountExecutedTasks());
        return threadPool;
    }

    public static MyThreadPool runTasksListInThreadPool(Map<Runnable, Integer> tasksList, int tasksQueueSize, int threadsCount) {
        MyThreadPool threadPool = new MyThreadPool(tasksQueueSize, threadsCount);
        tasksList.forEach((task, taskDelay) -> {
            try {
                threadPool.submit(task, taskDelay);
            } catch (InterruptedException e) {
                System.out.println("Execution interrupted!");
                e.printStackTrace();
            } catch (ThreadPoolException e) {
                System.out.println("Thread Pool exception!");
                e.printStackTrace();
            }
        });
        threadPool.waitTasksCompletion(FOREVER);
        threadPool.shutdown();
        System.out.println("____The end!____");
        System.out.println("Count of executed tasks: " + threadPool.getCountExecutedTasks());
        return threadPool;
    }
}
