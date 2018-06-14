package ru.spbau.gbarto.server.architecture.blocking;

import ru.spbau.gbarto.server.architecture.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BlockingArchitecture extends Server{
    public BlockingArchitecture(int port, int m, int x) {
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

    private void collectMetrics(Worker[] workers) {
        for (Worker w : workers) {
            metrics.add(w.getMetrics());
        }

        for (int i = 0; i < 3; i++) {
            metrics.get(i).div(m);
        }
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Worker[] workers = new Worker[m];
            Thread[] threads = new Thread[m];

            ready = true;
            for (int i = 0; i < m; i++) {
                Socket socket = serverSocket.accept();

                workers[i] = new Worker(socket, x);
                threads[i] = new Thread(workers[i]);
                threads[i].setDaemon(false);
                threads[i].start();
            }

            waitThreads(threads);
            collectMetrics(workers);
        } catch (IOException e) {
            System.err.println("BlockingArchitecture: Error creating server");
            System.exit(1);
        }
    }
}
