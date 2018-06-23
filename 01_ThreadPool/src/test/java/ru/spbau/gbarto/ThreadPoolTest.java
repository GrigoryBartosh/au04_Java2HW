package ru.spbau.gbarto;

import static java.lang.Integer.min;
import static java.lang.Integer.max;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

class Tree {
    private Integer[] tree;
    private int size;
    private int threads;

    private static class Node {
        private ThreadPool<Node> pool;
        private Queue<LightFuture<Node>> queue;

        private int v;
        private int tl, tr;
        private int l, r;
        private int val;

        private Node(ThreadPool<Node> pool, Queue<LightFuture<Node>> queue, int v, int tl, int tr, int l, int r, int val) {
            this.pool = pool;
            this.queue = queue;
            this.v = v;
            this.tl = tl;
            this.tr = tr;
            this.l = l;
            this.r = r;
            this.val = val;
        }

        private Node(Node other, int v, int tl, int tr, int l, int r) {
            this.pool = other.pool;
            this.queue = other.queue;
            this.v = v;
            this.tl = tl;
            this.tr = tr;
            this.l = l;
            this.r = r;
            this.val = other.val;
        }
    }

    private void push(int v) {
        if (tree[v] == null) {
            return;
        }

        tree[2 * v] = tree[v];
        tree[2 * v + 1] = tree[v];
        tree[v] = null;
    }

    private Integer get(int v, int tl, int tr, int pos) {
        if (tl == tr) {
            return tree[v];
        }

        push(v);

        int tm = (tl + tr) / 2;
        if (pos <= tm) return get(2 * v, tl, tm, pos);
        else           return get(2 * v + 1, tm + 1, tr, pos);
    }

    private Node setParallel(Node node) {
        ThreadPool<Node> pool = node.pool;
        int v = node.v;
        int tl = node.tl;
        int tr = node.tr;
        int l = node.l;
        int r = node.r;

        if (l > r) {
            return null;
        }

        if (l <= tl && tr <= r) {
            tree[v] = node.val;
            return null;
        }

        push(v);

        int tm = (tl + tr) / 2;
        LightFuture<Node> task1 = pool.addTask(() -> setParallel(new Node(node, 2 * v, tl, tm, l, min(tm, r))));
        LightFuture<Node> task2 = pool.addTask(() -> setParallel(new Node(node, 2 * v + 1, tm+1, tr, max(l, tm + 1), r)));

        Queue<LightFuture<Node>> queue = node.queue;
        synchronized (queue) {
            queue.add(task1);
            queue.add(task2);
        }

        return null;
    }

    Tree(int size, int threads) {
        this.size = size;
        this.threads = threads;

        tree = new Integer[4 * size];

        set(0, size - 1, 0);
    }

    Integer get(int pos) {
        return get(1, 0, size - 1, pos);
    }

    void set(int l, int r, int val) {
        ThreadPool<Node> pool = new ThreadPool<>(threads);
        Queue<LightFuture<Node>> queue = new LinkedList<>();

        LightFuture<Node> task = pool.addTask(() -> setParallel(new Node(pool, queue, 1, 0, size - 1, l, r, val)));
        synchronized (queue) {
            queue.add(task);
        }

        while (!queue.isEmpty()) {
            LightFuture<Node> t;
            synchronized (queue) {
                t = queue.remove();
            }

            try {
                t.get();
            } catch (LightFuture.LightExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    Integer[] getArray() {
        Integer[] res = new Integer[size];
        for (int i = 0; i < size; i++) {
            res[i] = get(i);
        }

        return res;
    }
}

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

    @Test
    void testMultiThreads() {
        Tree tree = new Tree(11, 4);

        tree.set(0, 1, 8);
        tree.set(2, 3, 0);
        tree.set(4, 6, 5);
        tree.set(7, 7, 3);
        tree.set(8, 8, 5);
        tree.set(9, 9, 3);
        tree.set(10, 10, 5);

        Integer[] res = tree.getArray();

        assertArrayEquals(new Integer[]{8, 8, 0, 0, 5, 5, 5, 3, 5, 3, 5}, res);
    }
}