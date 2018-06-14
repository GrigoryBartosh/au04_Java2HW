package ru.spbau.gbarto.server.architecture;

public class Metric {
    private double val = 0;
    private long time = 0;

    public synchronized void start() {
        time = System.currentTimeMillis();
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

    double get() {
        return val;
    }
}
