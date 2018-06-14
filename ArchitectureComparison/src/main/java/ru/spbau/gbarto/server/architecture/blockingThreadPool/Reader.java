package ru.spbau.gbarto.server.architecture.blockingThreadPool;

import ru.spbau.gbarto.Serializer;
import ru.spbau.gbarto.server.architecture.AllMetrics;
import ru.spbau.gbarto.server.architecture.Metric;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class Reader implements Runnable {
    private Socket socket;
    private AllMetrics metrics;
    private ExecutorService threadPool;
    private int x;
    private ExecutorService sender;

    Reader(Socket socket, AllMetrics metrics, int x, ExecutorService threadPool, ExecutorService sender) {
        this.socket = socket;
        this.metrics = metrics;
        this.x = x;
        this.threadPool = threadPool;
        this.sender = sender;
    }

    @Override
    public void run() {
        try {
            DataInputStream input = new DataInputStream(socket.getInputStream());

            AtomicInteger stepNumber = new AtomicInteger(0);
            for (int i = 0; i < x; i++) {
                int[] array = Serializer.readArray(input);

                Metric clientOnTheServer = new Metric();
                clientOnTheServer.start();

                Worker worker = new Worker(socket, array, metrics, clientOnTheServer, x, stepNumber, sender);
                threadPool.submit(worker);
            }
        } catch (IOException e) {
            System.err.println("BlockingThreadPoolArchitecture: Reader: Error working with client");
            System.exit(1);
        }
    }
}
