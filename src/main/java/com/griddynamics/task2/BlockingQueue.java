package com.griddynamics.task2;

import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Stream;

public class BlockingQueue<Type> {
    private Queue<Type> queue = new LinkedList<>();
    private int MAX_ELEMENTS_COUNT = -1;
    private boolean isNeedToStop = false;
    private int tasksExecutedCount = 0;

    public BlockingQueue(int size) {
        this.MAX_ELEMENTS_COUNT = size;
    }

    public synchronized void addElement(Type element) throws InterruptedException {
        while (this.queue.size() == this.MAX_ELEMENTS_COUNT) {
            wait();
        }
        if (this.queue.isEmpty()) {
            notifyAll();
        }
        this.queue.offer(element);
    }

    public synchronized Type getHeadElement() throws InterruptedException {
        while (this.queue.isEmpty() && !needToStop()) {
            wait();
        }
        if (this.queue.size() == this.MAX_ELEMENTS_COUNT) {
            notifyAll();
        }
        return this.queue.poll();
    }

    public boolean isEmpty() {
        return this.queue.isEmpty();
    }

    public void stop() {
        this.isNeedToStop = true;
    }

    public boolean needToStop() {
        return this.isNeedToStop;
    }

    public int getQueueSize() {
        return this.queue.size();
    }

    public synchronized void increaseTasksExecutedCount() {
        this.tasksExecutedCount++;
    }

    public int getTasksExecutedCount() {
        return this.tasksExecutedCount;
    }
}
