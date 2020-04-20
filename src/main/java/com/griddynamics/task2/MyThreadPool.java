package com.griddynamics.task2;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MyThreadPool {

    private volatile boolean shutdown = false;
    private BlockingQueue<Runnable> tasksQueue = new BlockingQueue<>();
    private Map<Runnable, Long> taskDelays = new HashMap<>();
    private int tasksSubmittedCount = 0;
    private int tasksExecutedCount = 0;
    private final List<Thread> threads;
    private final Object lock = new Object();
    private final String THREAD_NAME_TEMPLATE = "%s_%d";
    private final String TASK_STARTED_TEMPLATE = "%s %s: got new task!";
    private final String TASK_ENDED_TEMPLATE = "%s %s: task ended!";
    private final long MAX_ALLOWED_DELAY = 15000;



    public MyThreadPool(int threadsCount) {
        if (threadsCount <= 0) throw new IllegalArgumentException();
        this.threads = new ArrayList<>(threadsCount);
        for (int i = 0; i < threadsCount; i++) {
            Thread thread = new MyThread(String.format(THREAD_NAME_TEMPLATE, "Thread", i));
            thread.start();
            this.threads.add(thread);
        }
    }

    public void submit(Runnable task, long delayMillisec) throws IllegalArgumentException, InterruptedException {
        if (task == null) {
            throw new IllegalArgumentException("Task can't be null!");
        }
        if (delayMillisec < 0) {
            throw new IllegalArgumentException("Delay can't be less then zero!");
        }
        if (delayMillisec > MAX_ALLOWED_DELAY) {
            throw new IllegalArgumentException("Too long delay!");
        }

        synchronized (lock) {
            if (this.shutdown)
                throw new IllegalStateException();
            taskDelays.put(task, delayMillisec);
            tasksQueue.addElement(task);
            this.tasksSubmittedCount++;
        }
    }

    public void shutdown() throws InterruptedException {
        synchronized (lock) {
            this.shutdown = true;
            if(tasksSubmittedCount >= threads.size()) {
                for (Thread thread : threads) {
                    //I don't want to interrupt execution! I want wait for all tasksQueue completion
                    //thread.interrupt();
                    thread.join();
                }
            }
        }
        //to support case when tasks count less then threads
        if(tasksSubmittedCount < threads.size()) {
            waitAllThreadsBecomeTerminated();
            stopAllActiveThreads();
        }
    }

    private void waitAllThreadsBecomeTerminated() {
        int countFinishedThreads = calculateCountOfTerminatedThreads();
        while (((countFinishedThreads != threads.size() && countFinishedThreads != tasksSubmittedCount) || tasksExecutedCount != tasksSubmittedCount)) {
            countFinishedThreads = sleepAndCheckCountOFTerminatedThreads(countFinishedThreads);
        }
    }

    private int sleepAndCheckCountOFTerminatedThreads(int prevCountFinishedThreads) {
        //System.out.println("We have " + prevCountFinishedThreads + " finished threads");
        //printAllThreadsState();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return calculateCountOfTerminatedThreads();
    }

    public void stopAllActiveThreads() {
        threads.forEach(thread -> thread.interrupt());
    }

    private void printThreadState(Thread thread) {
        System.out.println(thread.getName() + " state:" + thread.getState().toString());
    }

    private void printAllThreadsState() {
        this.threads.forEach(thread -> printThreadState(thread));
    }

    private int calculateCountOfTerminatedThreads() {
        return threads.stream().filter(thread -> thread.getState().equals(Thread.State.TERMINATED))
                                .collect(Collectors.toList()).size();
    }

    private class MyThread extends Thread {

        MyThread(String threadName) {
            super(threadName);
        }

        @Override
        public void run() {
            try {
                while (!(shutdown && tasksQueue.isEmpty())) {
                    try {
                        Runnable task = tasksQueue.getHeadElement();
                        String threadName = Thread.currentThread().getName();
                        System.out.println(String.format(TASK_STARTED_TEMPLATE, LocalTime.now(), threadName));
                        TimeUnit.MILLISECONDS.sleep(taskDelays.get(task).longValue());
                        task.run();
                        System.out.println(String.format(TASK_ENDED_TEMPLATE, LocalTime.now(), threadName));
                        tasksExecutedCount++;
                    } catch (RuntimeException e) {
                        Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(this, e);
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("Tasks execution interrupted for " + Thread.currentThread().getName() + "!");
            }
        }
    }
}
