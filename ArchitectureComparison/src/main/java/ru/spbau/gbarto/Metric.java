package ru.spbau.gbarto;

public class Metric {
    private long time = 0;
    private double val = 0;

    public synchronized void start() {
        time = System.currentTimeMillis();
    }

    public synchronized void set(double val) {
        this.val = val;
    }

    public synchronized void stop() {
        val = System.currentTimeMillis() - time;
    }

    public synchronized void add(double val) {
        this.val += val;
    }

    public synchronized void add(Metric other) {
        val += other.val;
    }

    public synchronized void div(double n) {
        val /= n;
    }

    public synchronized double get() {
        return val;
    }
}
