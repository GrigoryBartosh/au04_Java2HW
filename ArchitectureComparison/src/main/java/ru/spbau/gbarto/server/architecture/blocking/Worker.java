package ru.spbau.gbarto.server.architecture.blocking;

import ru.spbau.gbarto.Serializer;
import ru.spbau.gbarto.server.architecture.Algorithm;
import ru.spbau.gbarto.server.architecture.AllMetrics;
import ru.spbau.gbarto.server.architecture.Metric;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Worker implements Runnable {
    private Socket socket;
    private int x;

    private AllMetrics metrics = new AllMetrics();

    Worker(Socket socket, int x) {
        this.socket = socket;
        this.x = x;

        metrics.request.start();
    }

    @Override
    public void run() {
        try (DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {
            for (int i = 0; i < x; i++) {
                int[] array = Serializer.readArray(input);

                Metric clientOnTheServer = new Metric();
                clientOnTheServer.start();

                metrics.requestProcessing.add(Algorithm.sort(array));

                clientOnTheServer.stop();
                metrics.clientOnTheServer.add(clientOnTheServer);

                Serializer.writeArray(output, array);
                output.flush();
            }
        } catch (IOException e) {
            System.err.println("BlockingArchitecture: Error working with client");
            System.exit(1);
        }

        metrics.request.stop();

        for (int i = 0; i < 3; i++) {
            metrics.get(i).div(x);
        }
    }

    AllMetrics getMetrics() {
        return metrics;
    }
}
