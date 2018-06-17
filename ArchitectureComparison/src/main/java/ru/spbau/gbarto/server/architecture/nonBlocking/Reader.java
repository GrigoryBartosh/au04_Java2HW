package ru.spbau.gbarto.server.architecture.nonBlocking;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public class Reader implements Runnable {
    private int m;
    private Selector selector;
    private final Object lock = new Object();

    Reader(int m) {
        this.m = m;

        try {
            selector = Selector.open();
        } catch (IOException e) {
            System.err.println("NonBlockingArchitecture: Reader: Error open selector");
            System.exit(1);
        }
    }

    void register(SocketChannel socketChannel, Client client) {
        try {
            synchronized (lock) {
                socketChannel.register(selector, SelectionKey.OP_READ, client);
            }
        } catch (ClosedChannelException e) {
            System.err.println("NonBlockingArchitecture: Reader: Error channel registration");
            System.exit(1);
        }
    }

    @Override
    public void run() {
        AtomicInteger processedClientsNum = new AtomicInteger(0);

        while (processedClientsNum.get() < m) {
            try {
                Iterator<SelectionKey> keyIterator;
                synchronized (lock) {
                    selector.selectNow();
                    keyIterator = selector.selectedKeys().iterator();
                }

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    Client client = (Client) key.attachment();
                    SocketChannel socketChannel = (SocketChannel) key.channel();

                    keyIterator.remove();

                    client.read(socketChannel);

                    if (client.isReadingFinished()) {
                        key.cancel();
                        processedClientsNum.incrementAndGet();
                    }
                }
            } catch (IOException e) {
                System.err.println("NonBlockingArchitecture: Reader: Error selection");
                System.exit(1);
            }
        }
    }
}
