package ru.spbau.gbarto.server.architecture.nonBlocking;

import ru.spbau.gbarto.server.architecture.Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NonBlockingArchitecture extends Server{
    private static final int THREAD_POOL_THREADS = 3;
    private static final int WAIT_TTS = 60 * 1000;

    public NonBlockingArchitecture(int port, int m, int x) {
        super(port, m, x);
    }

    private void waitThread(Thread thread) {
        while (thread.isAlive()) {
            try {
                thread.join();
            } catch (InterruptedException ignored) { }
        }
    }

    private void waitThreadPool(ExecutorService threadPool) {
        threadPool.shutdown();

        while (true) {
            try {
                if (threadPool.awaitTermination(WAIT_TTS, TimeUnit.MILLISECONDS)) {
                    break;
                }
            } catch (InterruptedException ignored) { }
        }
    }

    private void collectMetrics(Client[] clients) {
        for (Client c : clients) {
            metrics.add(c.getMetrics());
        }

        for (int i = 0; i < 2; i++) {
            metrics.get(i).div(m);
        }
    }

    @Override
    public void run() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(port));

            Client[] clients = new Client[m];
            ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_THREADS);
            Reader reader = new Reader(m);
            Writer writer = new Writer(m, x);
            Thread threadReader = new Thread(reader);
            Thread threadWriter = new Thread(writer);
            threadReader.setDaemon(false);
            threadWriter.setDaemon(false);
            threadReader.start();
            threadWriter.start();

            ready = true;
            for (int i = 0; i < m; i++) {
                clients[i] = new Client(x, threadPool, writer);

                SocketChannel socketChannel = serverSocketChannel.accept();
                socketChannel.configureBlocking(false);
                reader.register(socketChannel, clients[i]);
            }

            waitThread(threadReader);
            waitThread(threadWriter);
            waitThreadPool(threadPool);
            collectMetrics(clients);
        } catch (IOException e) {
            System.err.println("NonBlockingArchitecture: Error creating server");
            System.exit(1);
        }
    }
}
