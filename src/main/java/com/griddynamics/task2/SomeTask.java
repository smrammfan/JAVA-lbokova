package com.griddynamics.task2;

import java.time.LocalTime;
import java.util.Random;

public class SomeTask implements Runnable {

    private String taskName;

    public SomeTask(String taskName) {
        if (taskName == null || taskName.isEmpty()) {
            this.taskName = generateTaskName();
        } else {
            this.taskName = taskName;
        }
    }

    public SomeTask() {
        this.taskName = generateTaskName();
    }

    private String generateTaskName() {
        return String.valueOf(new Random().nextInt(50));
    }

    public String getTaskName() {
        return this.taskName;
    }

    @Override
    public void run() {
        try {
            String threadName = Thread.currentThread().getName();
            System.out.println(LocalTime.now() + " " + threadName + " Started task #" + getTaskName());
            Thread.sleep(3000);
            System.out.println(LocalTime.now() + " " + threadName + " Finished task #" + getTaskName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
