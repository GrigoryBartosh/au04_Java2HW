package ru.spbau.gbarto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.*;

class ThreadPoolTest {

    @Test
    void testSingleThreadSingleTask() throws LightExecutionException {
        ThreadPool pool = new ThreadPool(1);
        LightFuture<Integer> task = pool.addTask(() -> 8800555);
        assertEquals(8800555, task.get().intValue());
        pool.shutdown();
    }

    @Test
    void testSingleThreadMultiTask() throws LightExecutionException {
        ThreadPool pool = new ThreadPool(1);
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
    void testMultiThreadMultiTask() throws LightExecutionException {
        ThreadPool pool = new ThreadPool(4);
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
    void testThenApply() throws LightExecutionException {
        ThreadPool pool = new ThreadPool(4);

        List<LightFuture<Integer>> tasks = new ArrayList<>();

        LightFuture<Integer> startTask = pool.addTask(() -> 1);
        tasks.add(startTask);
        for (int i = 1; i < 10; i++) {
            LightFuture<Integer> task = tasks.get(i - 1);
            task = task.thenApply(x -> x * 2);
            tasks.add(task);
        }

        for (int i = 0; i < 10; i++) {
            double expected = ((Double)Math.pow(2, i)).intValue();
            double actual = (double) tasks.get(i).get();
            assertEquals(expected, actual);
        }

        pool.shutdown();
    }

    @Test
    void testMultiThreads() throws InterruptedException {
        ThreadPool pool = new ThreadPool(4);

        Thread[] threads = new Thread[10];
        boolean[] results = new boolean[10];
        for (int i = 0; i < 10; i++) {
            final int threadNum = i;
            threads[threadNum] = new Thread(() -> {
                Random random = new Random();

                List<LightFuture<Integer>> tasks = new ArrayList<>();
                int[] val = new int[10];
                for (int j = 0; j < 10; j++) {
                    final int testNum = j;
                    val[testNum] = random.nextInt();
                    LightFuture<Integer> task = pool.addTask(() -> val[testNum] * val[testNum]);
                    tasks.add(task);
                }

                for (int j = 0; j < 10; j++) {
                    try {
                        int expected = val[j] * val[j];
                        assertEquals(expected, tasks.get(j).get().intValue());
                    } catch (LightExecutionException ignored) {}
                }

                results[threadNum] = true;
            });
        }

        for (int i = 0; i < 10; i++) {
            threads[i].start();
        }

        for (int i = 0; i < 10; i++) {
            threads[i].join();
            assertTrue(results[i]);
        }
    }

    @Test
    void testLightExecutionException() {
        ThreadPool pool = new ThreadPool(1);

        LightFuture<Integer> task = pool.addTask(() -> {
            throw new RuntimeException();
        });

        assertThrows(LightExecutionException.class, task::get);
    }
}