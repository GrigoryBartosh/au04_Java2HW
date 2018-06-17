package ru.spbau.gbarto.server.architecture.blockingThreadPool;

import ru.spbau.gbarto.server.architecture.Algorithm;
import ru.spbau.gbarto.server.architecture.AllMetrics;
import ru.spbau.gbarto.Metric;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class Worker implements Runnable {
    private Socket socket;
    private int[] array;
    private AllMetrics metrics;
    private Metric clientOnTheServer;
    private int x;
    private AtomicInteger stepNumber;
    private ExecutorService sender;

    Worker(Socket socket, int[] array, AllMetrics metrics, Metric clientOnTheServer, int x, AtomicInteger stepNumber, ExecutorService sender) {
        this.socket = socket;
        this.array = array;
        this.metrics = metrics;
        this.clientOnTheServer = clientOnTheServer;
        this.x = x;
        this.stepNumber = stepNumber;
        this.sender = sender;
    }

    @Override
    public void run() {
        metrics.requestProcessing.add((double) Algorithm.sort(array));

        Writer writer = new Writer(socket, array, metrics, clientOnTheServer, x, stepNumber);
        sender.submit(writer);
    }
}
