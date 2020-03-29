package com.griddynamics.task2;

import java.time.LocalTime;

public class TaskExecutor implements Runnable {

    private BlockingQueue<RunnableTask> tasksQueue;

    public TaskExecutor(BlockingQueue<RunnableTask> tasksQueue) {
        this.tasksQueue = tasksQueue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String threadName = Thread.currentThread().getName();
                RunnableTask task = tasksQueue.getHeadElement();
                if (task != null) {
                    System.out.println(LocalTime.now() + " " + threadName + ": got new task!");
                    Thread.currentThread().sleep(task.getDelay());
                    task.run();
                    System.out.println(LocalTime.now() + " " + threadName + ": task ended!");
                    tasksQueue.increaseTasksExecutedCount();
                }
                if (tasksQueue.isEmpty() || (tasksQueue.needToStop() && tasksQueue.isEmpty())) {
                    return;
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Tasks execution interrupted for " + Thread.currentThread().getName() + "!");
            //e.printStackTrace();
        }
    }
}
