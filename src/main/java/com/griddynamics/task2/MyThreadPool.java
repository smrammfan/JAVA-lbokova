package com.griddynamics.task2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MyThreadPool {
    private BlockingQueue<RunnableTask> tasksQueue;
    private List<Thread> threads;
    private boolean isShutdown;
    private int tasksSubmittedCount;


    public MyThreadPool(int tasksQueueMaxSize, int threadsCount) throws IllegalArgumentException {
        if (tasksQueueMaxSize < 1 || threadsCount < 1) {
            throw new IllegalArgumentException("Queue size and threads count should be more then 0");
        }
        this.tasksQueue = new BlockingQueue<>(tasksQueueMaxSize);
        this.threads = new ArrayList<>(threadsCount);
        this.isShutdown = false;
        this.tasksSubmittedCount = 0;
        String threadName = null;
        TaskExecutor taskExecutor = null;
        for (int num = 1; num <= threadsCount; num++) {
            threadName = "Thread-" + num;
            taskExecutor = new TaskExecutor(tasksQueue);
            Thread thread = new Thread(taskExecutor, threadName);
            threads.add(thread);
            thread.start();
        }
    }

    public void submit(Runnable task, long delay) throws InterruptedException, ThreadPoolException {
        if (task == null) {
            throw new IllegalArgumentException("Task can't be null!");
        }
        if (delay < 0) {
            throw new IllegalArgumentException("Delay can't be less then zero!");
        }

        if (!isShittedDown()) {
            tasksQueue.addElement(new RunnableTask(task, delay));
            this.tasksSubmittedCount++;
        } else {
            throw new ThreadPoolException("You cannot submit task when Thread Pool is shutted down!");
        }
    }

    public void waitTasksCompletion(long timeoutMilis) {
        if (!isShittedDown() && (tasksSubmittedCount >= threads.size())) {
            waitTasksCompletionBeforeShutDownWhenMoreTasksThenThreads(timeoutMilis);
            printAllThreadsState();
        } else if (!isShittedDown() && tasksSubmittedCount < threads.size()) {
            waitAllThreadsExecuteTheirTask();
            stopAllActiveThreads();
        } else if (isShittedDown()) {
            waitAllThreadsBecomeTerminated();
            stopAllActiveThreads();
        }
    }

    private void waitAllThreadsExecuteTheirTask() {
        int countFinishedThreads = calculateCountOfTerminatedThreads();
        while (countFinishedThreads != tasksSubmittedCount) {
            countFinishedThreads = sleepAndCheckCountOFTerminatedThreads(countFinishedThreads);
        }
    }

    private void waitAllThreadsBecomeTerminated() {
        int countFinishedThreads = calculateCountOfTerminatedThreads();
        while (((countFinishedThreads != threads.size() && countFinishedThreads != tasksSubmittedCount) || getCountExecutedTasks() != tasksSubmittedCount)) {
            countFinishedThreads = sleepAndCheckCountOFTerminatedThreads(countFinishedThreads);
        }
    }

    private int sleepAndCheckCountOFTerminatedThreads(int prevCountFinishedThreads) {
        System.out.println("We have " + prevCountFinishedThreads + " finished threads");
        printAllThreadsState();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return calculateCountOfTerminatedThreads();
    }

    public void waitTasksCompletionBeforeShutDownWhenMoreTasksThenThreads(long timeoutMilis) {
        threads.forEach(thread -> {
            try {
                //printThreadState(thread);
                thread.join(timeoutMilis);
            } catch (InterruptedException e) {
                System.out.println(thread.getName() + " join interrupted!");
                e.printStackTrace();
            }
        });
    }

    public void shutdown() {
        this.isShutdown = true;
        tasksQueue.stop();
    }

    public boolean isShittedDown() {
        return this.isShutdown;
    }

    public void stopAllActiveThreads() {
        threads.forEach(thread -> thread.interrupt());
    }

    public int getCountExecutedTasks() {
        return this.tasksQueue.getTasksExecutedCount();
    }

    private void printThreadState(Thread thread) {
        System.out.println(thread.getName() + " state:" + thread.getState().toString());
    }

    private void printAllThreadsState() {
        this.threads.forEach(thread -> printThreadState(thread));
    }

    private int calculateCountOfTerminatedThreads() {
        return threads.stream().filter(thread -> thread.getState().equals(Thread.State.TERMINATED)).collect(Collectors.toList()).size();
    }
}
