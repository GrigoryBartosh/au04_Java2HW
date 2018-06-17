package ru.spbau.gbarto.server.architecture;

public abstract class Server implements Runnable {
    protected int port;
    protected int m;
    protected int x;
    protected boolean ready = false;

    protected AllMetrics metrics = new AllMetrics();

    public Server(int port, int m, int x) {
        this.port = port;
        this.m = m;
        this.x = x;
    }

    public boolean isReady() {
        return ready;
    }

    public void setMetricRequest(double val) {
        metrics.request.set(val);
    }

    public double[] getMetrics() {
        return metrics.toArray();
    }
}
