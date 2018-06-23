package ru.spbau.gbarto;

import org.junit.Test;

import java.util.function.Supplier;

import static org.junit.Assert.*;

public class LazyFactoryTest {
    @Test
    public void testSimpleSingleGet() {
        Lazy<Integer> lazy = LazyFactory.createLazy(() -> 8800555);
        assertEquals(8800555, lazy.get().intValue());
    }

    @Test
    public void testSimpleMultyGet() {
        Lazy<Integer> lazy = LazyFactory.createLazy(() -> 8800555);
        for (int i = 0; i < 100; i++) {
            assertEquals(8800555, lazy.get().intValue());
        }
    }

    @Test
    public void testSimpleNull() {
        Lazy<Integer> lazy = LazyFactory.createLazy(() -> null);
        assertEquals(null, lazy.get());
    }

    @Test
    public void testSimple() {
        Lazy<Integer> lazy = LazyFactory.createLazy(new Supplier<Integer>() {
            private int n = 8800555;

            @Override
            public Integer get() {
                return n++;
            }
        });

        int res = lazy.get();
        for (int i = 0; i < 100; i++) {
            assertEquals(res, lazy.get().intValue());
        }
    }

    @Test
    public void testThread() throws InterruptedException {
        Lazy<Integer> lazy = LazyFactory.createThreadLazy(() -> 8800555);

        Runnable runnable = () -> {
            for (int i = 0; i < 100; i++) {
                assertEquals(8800555, lazy.get().intValue());
            }
        };

        Thread[] thread = new Thread[8];
        for (int i = 0; i < 8; i++) {
            thread[i] = new Thread(runnable);
        }

        for (int i = 0; i < 8; i++) {
            thread[i].start();
        }

        for (int i = 0; i < 8; i++) {
            thread[i].join();
        }
    }
}