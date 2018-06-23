package ru.spbau.gbarto;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Thread pool with fixed count of threads.
 */
class ThreadPool {

    private int count;
    private final Queue<Task> queue = new LinkedList<>();
    private Thread[] threads;

    /**
     * Describes the task that will be performed.
     */
    private class Task<T> implements LightFuture<T> {

        private Supplier<T> supplier;
        private boolean ready = false;
        private T result = null;
        private Exception exception = null;
        private final Object lock = new Object();

        private Task(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        @Override
        public boolean isReady() {
            synchronized (lock) {
                return ready;
            }
        }

        @Override
        public T get() throws LightExecutionException {
            synchronized (lock) {
                while (!isReady()) {
                    try {
                        lock.wait();
                    } catch (InterruptedException ignored) { }
                }

                if (exception != null) {
                    throw new LightExecutionException(exception);
                }

                return result;
            }
        }

        @Override
        public <U> LightFuture<U> thenApply(Function<? super T, U> function) {
            return ThreadPool.this.addTask(() -> {
                try {
                    return function.apply(get());
                } catch (LightExecutionException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        private void run() {
            synchronized (lock) {
                try {
                    result = supplier.get();
                } catch (Exception e) {
                    exception = e;
                }
                ready = true;

                lock.notify();
            }
        }
    }

    /**
     * Describes the process of performing tasks.
     */
    private class Worker implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                Task task = null;
                while (!Thread.currentThread().isInterrupted()) {
                    synchronized (queue) {
                        while (queue.isEmpty() && !Thread.currentThread().isInterrupted()) {
                            try {
                                queue.wait();
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }
                    }

                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }

                    synchronized (queue) {
                        if (!queue.isEmpty()) {
                            task = queue.remove();
                            break;
                        }
                    }
                }

                if (Thread.currentThread().isInterrupted()) {
                    break;
                }

                task.run();
            }
        }
    }

    /**
     * Constructs thread pool with specified number of threads.
     * 
     * @param count of parallel threads
     */
    ThreadPool(int count) {
        this.count = count;
        threads = new Thread[count];

        Worker worker = new Worker();

        for (int i = 0; i < count; i++) {
            threads[i] = new Thread(worker);
            threads[i].setDaemon(true);
            threads[i].start();
        }
    }

    /**
     * Receives a task and passes it to a free thread for processing.
     *
     * @param supplier specified task to be processed
     * @return special object that stores this task and its information
     */
    <T> LightFuture<T> addTask(Supplier<T> supplier) {
        Task<T> task = new Task<>(supplier);

        synchronized (queue) {
            queue.add(task);
            queue.notify();
        }

        return task;
    }

    /**
     * Finishes all threads in thread pool.
     */
    void shutdown() {
        for (int i = 0; i < count; i++) {
            threads[i].interrupt();
        }

        for (int i = 0; i < count; i++) {
            while (threads[i].isAlive()) {
                try {
                    threads[i].join();
                } catch (InterruptedException ignore) {
                }
            }
        }
    }
}
