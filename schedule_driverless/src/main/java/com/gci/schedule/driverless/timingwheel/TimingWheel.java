package com.gci.schedule.driverless.timingwheel;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class TimingWheel {

    /**
     * ring buffer size
     */
    private Object[] buckets;

    private int wheelSize;

    /**
     * business thread pool
     */
    private ExecutorService executorService;

    private volatile int size = 0;

    /***
     * task stop sign
     */
    private volatile boolean stop = false;

    /**
     * task start sign
     */
    private volatile AtomicBoolean start = new AtomicBoolean(false);

    /**
     * total tick times
     */
    private AtomicInteger tick = new AtomicInteger();

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    /**
     * Create a new TimingWheel ring buffer by custom buffer size
     *
     * @param executorService the business thread pool
     * @param wheelSize      custom buffer size
     */
    public TimingWheel(ExecutorService executorService, int wheelSize) {
        if (!powerOf2(wheelSize)) {
            throw new RuntimeException("wheelSize=[" + wheelSize + "] must be a power of 2");
        }
        this.executorService = executorService;
        this.wheelSize = wheelSize;
        this.buckets = new Object[wheelSize];
    }

    /**
     * Add a task into the ring buffer(thread safe)
     *
     * @param task business task extends {@link Task}
     */
    public void addTask(Task task) {
        int delayTime = task.getDelayTime();

        try {
            lock.lock();
            int index = mod(delayTime, wheelSize);
            task.setIndex(index);
            Set<Task> tasks = get(index);

            if (tasks != null) {
                int cycleNum = cycleNum(delayTime, wheelSize);
                task.setCycleNum(cycleNum);
                tasks.add(task);
            } else {
                int cycleNum = cycleNum(delayTime, wheelSize);
                task.setIndex(index);
                task.setCycleNum(cycleNum);
                Set<Task> sets = new HashSet<>();
                sets.add(task);
                put(delayTime, sets);
            }
            size++;
        } finally {
            lock.unlock();
        }
        start();
    }

    /**
     * Thread safe
     *
     * @return the size of ring buffer
     */
    public int taskSize() {
        return size;
    }

    /**
     * Start background thread to consumer wheel timer, it will always run until you call method {@link #stop}
     */
    public void start() {
        if (!start.get()) {
            if (start.compareAndSet(start.get(), true)) {
                log.info("TimingWheel is starting");
                Thread job = new Thread(new TriggerJob());
                job.setName("TimingWheel thread");
                job.start();
                start.set(true);
            }

        }
    }

    /**
     * Stop consumer ring buffer thread
     *
     * @param force True will force close consumer thread and discard all pending tasks
     *              otherwise the consumer thread waits for all tasks to completes before closing.
     */
    public void stop(boolean force) {
        if (force) {
            log.info("TimingWheel is forced stop");
            stop = true;
            executorService.shutdownNow();
        } else {
            log.info("TimingWheel is stopping");
            if (taskSize() > 0) {
                try {
                    lock.lock();
                    condition.await();
                    stop = true;
                } catch (InterruptedException e) {
                    log.error("InterruptedException", e);
                } finally {
                    lock.unlock();
                }
            }
            executorService.shutdown();
        }


    }


    private Set<Task> get(int index) {
        return (Set<Task>) buckets[index];
    }

    private void put(int delayTime, Set<Task> tasks) {
        int index = mod(delayTime, wheelSize);
        buckets[index] = tasks;
    }

    private Set<Task> remove(int delayTime) {
        Set<Task> tempTask = new HashSet<>();
        Set<Task> result = new HashSet<>();

        Set<Task> tasks = (Set<Task>) buckets[delayTime];
        if (tasks == null) {
            return result;
        }

        for (Task task : tasks) {
            if (task.getCycleNum() == 0) {
                result.add(task);

                size2Notify();
            } else {
                // decrement 1 cycle number and update origin data
                task.setCycleNum(task.getCycleNum() - 1);
                tempTask.add(task);
            }
        }

        //update origin data
        buckets[delayTime] = tempTask;

        return result;
    }

    private void size2Notify() {
        try {
            lock.lock();
            size--;
            if (size == 0) {
                condition.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    private boolean powerOf2(int target) {
        if (target < 0) {
            return false;
        }
        int value = target & (target - 1);
        if (value != 0) {
            return false;
        }

        return true;
    }

    private int mod(int target, int mod) {
        // equals target % mod
        target = target + tick.get();
        return target & (mod - 1);
    }

    private int cycleNum(int target, int mod) {
        // equals target/mod
        return target >> Integer.bitCount(mod - 1);
    }

    /**
     * An abstract class used to implement business.
     */
    @Data
    public abstract static class Task extends Thread {

        private int index;

        private int cycleNum;

        private int delayTime;

        @Override
        public void run() {
        }
    }


    private class TriggerJob implements Runnable {

        @Override
        public void run() {
            int index = 0;
            while (!stop) {
                try {
                    Set<Task> tasks = remove(index);
                    for (Task task : tasks) {
                        executorService.submit(task);
                    }

                    if (++index > wheelSize - 1) {
                        index = 0;
                    }

                    //Total tick number of records
                    tick.incrementAndGet();
                    TimeUnit.SECONDS.sleep(1);

                } catch (Exception e) {
                    log.error("Exception", e);
                }

            }
            log.info("TimingWheel is stopped");
        }
    }
}
