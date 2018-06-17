package ru.spbau.gbarto.server.architecture.nonBlocking;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public class Writer implements Runnable {
    private int m;
    private int x;
    private Selector selector;
    private final Object lock = new Object();

    Writer(int m, int x) {
        this.m = m;
        this.x = x;

        try {
            selector = Selector.open();
        } catch (IOException e) {
            System.err.println("NonBlockingArchitecture: Writer: Error open selector");
            System.exit(1);
        }
    }

    void register(SocketChannel socketChannel, Client client) {
        try {
            synchronized (lock) {
                socketChannel.register(selector, SelectionKey.OP_WRITE, client);
            }
        } catch (ClosedChannelException e) {
            System.err.println("NonBlockingArchitecture: Writer: Error channel registration");
            System.exit(1);
        }
    }

    @Override
    public void run() {
        AtomicInteger processedArrayCnt = new AtomicInteger(0);

        while (processedArrayCnt.get() < m * x) {
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

                    if (client.write(socketChannel)) {
                        key.cancel();
                        processedArrayCnt.incrementAndGet();
                    }
                }
            } catch (IOException e) {
                System.err.println("NonBlockingArchitecture: Writer: Error selection");
                System.exit(1);
            }
        }
    }
}
