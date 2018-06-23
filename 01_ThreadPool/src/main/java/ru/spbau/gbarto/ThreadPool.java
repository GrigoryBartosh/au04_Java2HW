package ru.spbau.gbarto;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Thread pool with fixed count of threads.
 *
 * @param <T> type of tasks result
 */
class ThreadPool<T> {

    private int count;
    private Queue<Task> queue;
    private Thread[] threads;

    /**
     * Describes the task that will be performed.
     */
    private class Task implements LightFuture<T> {

        private Supplier<T> supplier;
        private boolean ready = false;
        private Object result;

        private Task(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        @Override
        public boolean isReady() {
            return ready;
        }

        @Override
        public T get() throws LightExecutionException {
            while (!isReady()) {
                Thread.yield();
            }

            if (result instanceof Exception) {
                throw new LightExecutionException((Exception) result);
            }

            return (T) result;
        }

        @Override
        public LightFuture<T> thenApply(Function<T, T> function) {
            return ThreadPool.this.addTask(() -> {
                try {
                    return function.apply(get());
                } catch (LightExecutionException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        private void run() {
            try {
                result = supplier.get();
            } catch (Exception e) {
                result = e;
            }
            ready = true;
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
                    while (queue.isEmpty() && !Thread.currentThread().isInterrupted()) {
                        Thread.yield();
                    }

                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }

                    synchronized (ThreadPool.this) {
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
        queue = new LinkedList<>();
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
    synchronized LightFuture<T> addTask(Supplier<T> supplier) {
        Task task = new Task(supplier);
        queue.add(task);
        return task;
    }

    /**
     * Finishes all threads in thread pool.
     */
    void shutdown() {
        for (int i = 0; i < count; i++) {
            threads[i].interrupt();
        }
    }
}
