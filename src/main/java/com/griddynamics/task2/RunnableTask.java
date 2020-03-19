package com.griddynamics.task2;

public class RunnableTask implements Runnable {

    private String taskName;
    private long delay;
    private Runnable someTask;

    public RunnableTask(Runnable task, long delay) {
        this.delay = delay;
        this.someTask = task;
    }

    public long getDelay() {
        return this.delay;
    }

    public String getTaskName() {
        return this.taskName;
    }

    @Override
    public void run() {
        this.someTask.run();
    }
}
