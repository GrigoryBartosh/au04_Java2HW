package ru.spbau.gbarto.server.architecture.blocking;

import ru.spbau.gbarto.Serializer;
import ru.spbau.gbarto.server.architecture.Algorithm;
import ru.spbau.gbarto.server.architecture.AllMetrics;
import ru.spbau.gbarto.Metric;

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

            socket.close();
        } catch (IOException e) {
            System.err.println("BlockingArchitecture: Worker: Error working with client");
            System.exit(1);
        }

        for (int i = 0; i < 2; i++) {
            metrics.get(i).div(x);
        }
    }

    AllMetrics getMetrics() {
        return metrics;
    }
}
