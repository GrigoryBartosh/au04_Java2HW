package ru.spbau.gbarto.server.architecture.nonBlocking;

import ru.spbau.gbarto.Metric;
import ru.spbau.gbarto.server.architecture.Algorithm;

import java.nio.channels.SocketChannel;

public class Worker implements Runnable {
    private Client client;
    private int[] array;
    private SocketChannel socketChannel;
    private Metric clientOnTheServer;

    Worker(Client client, int[] array, SocketChannel socketChannel, Metric clientOnTheServer) {
        this.client = client;
        this.array = array;
        this.socketChannel = socketChannel;
        this.clientOnTheServer = clientOnTheServer;
    }

    @Override
    public void run() {
        Metric requestProcessing = new Metric();
        requestProcessing.add(Algorithm.sort(array));
        client.updateRequestProcessing(requestProcessing);

        client.setToWrite(array, clientOnTheServer);

        Writer writer = client.getWriter();
        writer.register(socketChannel, client);
    }
}
