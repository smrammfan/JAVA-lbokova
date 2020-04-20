package com.griddynamics.task2;

public class Main {

    private static final int THREADS_COUNT = 8;
    private static final int TASKS_COUNT = 3;
    private static final int TASK_DELAY = 1000;

    public static void main(String[] args) throws InterruptedException, IllegalArgumentException {
        runTasks(THREADS_COUNT, TASKS_COUNT, TASK_DELAY);
    }

    public static void runTasks(int threadsCount, int tasksCount, int delay) throws InterruptedException, IllegalArgumentException {
        MyThreadPool myThreadPool = new MyThreadPool(threadsCount);
        for (int taskNumber = 1; taskNumber <= tasksCount; taskNumber++) {
            SomeTask task = new SomeTask(String.valueOf(taskNumber));
            myThreadPool.submit(task, delay * taskNumber);
        }
        myThreadPool.shutdown();
        System.out.println("____The end!____");
    }
}
