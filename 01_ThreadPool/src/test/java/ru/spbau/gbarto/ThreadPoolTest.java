package ru.spbau.gbarto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ThreadPoolTest {

    @Test
    void testSingleThreadSingleTask() throws LightFuture.LightExecutionException {
        ThreadPool<Integer> pool = new ThreadPool<>(1);
        LightFuture<Integer> task = pool.addTask(() -> 8800555);
        assertEquals(8800555, task.get().intValue());
        pool.shutdown();
    }

    @Test
    void testSingleThreadMultiTask() throws LightFuture.LightExecutionException {
        ThreadPool<Integer> pool = new ThreadPool<>(1);
        LightFuture<Integer> task1 = pool.addTask(() -> 8);
        LightFuture<Integer> task2 = pool.addTask(() -> 800);
        LightFuture<Integer> task3 = pool.addTask(() -> 555);
        LightFuture<Integer> task4 = pool.addTask(() -> 35);
        LightFuture<Integer> task5 = pool.addTask(() -> 35);

        assertEquals(8, task1.get().intValue());
        assertEquals(800, task2.get().intValue());
        assertEquals(555, task3.get().intValue());
        assertEquals(35, task4.get().intValue());
        assertEquals(35, task5.get().intValue());

        pool.shutdown();
    }

    @Test
    void testMultiThreadMultiTask() throws LightFuture.LightExecutionException {
        ThreadPool<Integer> pool = new ThreadPool<>(4);
        LightFuture<Integer> task1 = pool.addTask(() -> 8);
        LightFuture<Integer> task2 = pool.addTask(() -> 800);
        LightFuture<Integer> task3 = pool.addTask(() -> 555);
        LightFuture<Integer> task4 = pool.addTask(() -> 35);
        LightFuture<Integer> task5 = pool.addTask(() -> 35);

        assertEquals(8, task1.get().intValue());
        assertEquals(800, task2.get().intValue());
        assertEquals(555, task3.get().intValue());
        assertEquals(35, task4.get().intValue());
        assertEquals(35, task5.get().intValue());

        pool.shutdown();
    }

    @Test
    void testThenApply() throws LightFuture.LightExecutionException {
        ThreadPool<Integer> pool = new ThreadPool<>(4);

        LightFuture<Integer>[] tasks = new LightFuture[10];

        tasks[0] = pool.addTask(() -> 1);
        for (int i = 1; i < 10; i++) {
            tasks[i] = tasks[i - 1].thenApply(x -> x * 2);
        }

        for (int i = 0; i < 10; i++) {
            assertEquals(((Double)Math.pow(2, i)).intValue(), tasks[i].get().intValue());
        }

        pool.shutdown();
    }

    @Test
    void testLightExecutionException() {
        ThreadPool<Integer> pool = new ThreadPool<>(1);

        LightFuture<Integer> task = pool.addTask(() -> {
            throw new RuntimeException();
        });

        assertThrows(LightFuture.LightExecutionException.class, task::get);
    }
}