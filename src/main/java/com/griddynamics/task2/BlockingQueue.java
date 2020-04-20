package com.griddynamics.task2;

import java.util.LinkedList;
import java.util.Queue;

public class BlockingQueue<Type> {

    private final int capacity;
    private final Queue<Type> queue = new LinkedList<>();
    private final Object lock = new Object();

    public BlockingQueue() {
        this(Integer.MAX_VALUE);
    }

    public BlockingQueue(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException();
        this.capacity = capacity;
    }

    public void addElement(Type element) throws InterruptedException {
        synchronized (lock) {
            if (element == null) throw new NullPointerException();
            while (this.queue.size() >= capacity) {
                lock.wait();
            }
            this.queue.offer(element);
            lock.notifyAll();
        }
    }

    public Type getHeadElement() throws InterruptedException {
        synchronized (lock) {
            while (this.queue.size() == 0) {
                lock.wait();
            }
            Type element = this.queue.poll();
            lock.notifyAll();
            return element;
        }
    }

    public boolean isEmpty() {
        synchronized (lock) {
            return this.queue.isEmpty();
        }
    }
}
