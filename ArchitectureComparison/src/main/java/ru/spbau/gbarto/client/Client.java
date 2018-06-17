package ru.spbau.gbarto.client;

import ru.spbau.gbarto.Serializer;
import ru.spbau.gbarto.Metric;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class Client implements Runnable {
    private String host;
    private int port;
    private int n;
    private int delta;
    private int x;

    private Metric metricRequest;

    public Client(String host, int port, int n, int delta, int x) {
        this.host = host;
        this.port = port;
        this.n = n;
        this.delta = delta;
        this.x = x;

        metricRequest = new Metric();
    }

    private int[] generateArray() {
        int[] array = new int[n];
        Random random = new Random();

        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextInt();
        }

        return array;
    }

    private boolean isSorted(int[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] > array[i + 1]) {
                return false;
            }
        }

        return true;
    }

    private void sleep() {
        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() - start < delta) {
            try {
                Thread.sleep(start + delta - System.currentTimeMillis());
            } catch (InterruptedException ignored) { }
        }
    }

    @Override
    public void run() {
        metricRequest.start();

        while (true) {
            try (Socket socket = new Socket(host, port)) {
                try (DataInputStream input = new DataInputStream(socket.getInputStream());
                     DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {
                    for (int i = 0; i < x; i++) {
                        int[] array = generateArray();

                        Serializer.writeArray(output, array);
                        output.flush();

                        int[] sorted = Serializer.readArray(input);

                        if (!isSorted(sorted)) {
                            throw new IOException();
                        }

                        sleep();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Error working with server");
                    System.exit(1);
                }

                break;
            } catch (IOException ignored) { }
        }

        metricRequest.stop();
        metricRequest.div(x);
    }

    Metric getMetric() {
        return metricRequest;
    }
}
