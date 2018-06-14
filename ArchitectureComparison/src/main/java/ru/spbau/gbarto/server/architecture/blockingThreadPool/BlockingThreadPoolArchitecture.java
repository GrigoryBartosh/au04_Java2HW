package ru.spbau.gbarto.server.architecture.blockingThreadPool;

import ru.spbau.gbarto.server.architecture.AllMetrics;
import ru.spbau.gbarto.server.architecture.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BlockingThreadPoolArchitecture extends Server {
    private static final int THREAD_POOL_THREADS = 3;
    private static final int WAIT_TTS = 60 * 1000;

    public BlockingThreadPoolArchitecture(int port, int m, int x) {
        super(port, m, x);
    }

    private void waitThreads(Thread[] threads) {
        for (Thread t : threads) {
            while (t.isAlive()) {
                try {
                    t.join();
                } catch (InterruptedException ignored) { }
            }
        }
    }

    private void waitExecutor(ExecutorService executor) {
        try {
            while (true) {
                if (executor.awaitTermination(WAIT_TTS, TimeUnit.MILLISECONDS)) {
                    break;
                }
            }
        } catch (InterruptedException ignored) { }
    }

    private void waitThreadPool(ExecutorService threadPool) {
        threadPool.shutdown();
        waitExecutor(threadPool);
    }

    private void waitSenders(ExecutorService[] senders) {
        for (ExecutorService s : senders) {
            waitExecutor(s);
        }
    }

    private void collectMetrics(AllMetrics[] allMetrics) {
        for (AllMetrics m : allMetrics) {
            metrics.add(m);
        }

        for (int i = 0; i < 3; i++) {
            metrics.get(i).div(m);
        }
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            AllMetrics[] allMetrics = new AllMetrics[m];
            Thread[] threads = new Thread[m];
            ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_THREADS);
            ExecutorService[] senders = new ExecutorService[m];

            ready = true;
            for (int i = 0; i < m; i++) {
                allMetrics[i] = new AllMetrics();
                allMetrics[i].request.start();
                senders[i] = Executors.newSingleThreadExecutor();

                Socket socket = serverSocket.accept();

                Reader reader = new Reader(socket, allMetrics[i], x, threadPool, senders[i]);
                threads[i] = new Thread(reader);
                threads[i].setDaemon(false);
                threads[i].start();
            }

            waitThreads(threads);
            waitThreadPool(threadPool);
            waitSenders(senders);
            collectMetrics(allMetrics);
        } catch (IOException e) {
            System.err.println("BlockingThreadPoolArchitecture: Failed to accept settings from server");
            System.exit(1);
        }
    }
}
