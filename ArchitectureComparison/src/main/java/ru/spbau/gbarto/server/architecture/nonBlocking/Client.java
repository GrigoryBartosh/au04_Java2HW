package ru.spbau.gbarto.server.architecture.nonBlocking;

import ru.spbau.gbarto.Metric;
import ru.spbau.gbarto.Serializer;
import ru.spbau.gbarto.server.architecture.AllMetrics;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

public class Client {
    private static class PairIntMetric {
        private int bytesCnt;
        private Metric clientOnTheServer;

        private PairIntMetric(int bytesCnt, Metric clientOnTheServer) {
            this.bytesCnt = bytesCnt;
            this.clientOnTheServer = clientOnTheServer;
        }
    }

    private static final int BUFFER_SIZE = 1000000;

    private int x;
    private ExecutorService threadPool;
    private Writer writer;
    private int readArrayCnt = 0;
    private int writeArrayCnt = 0;

    private boolean readingNow = false;
    private int sizeToRead;
    private final ByteBuffer bufferRead = ByteBuffer.allocate(BUFFER_SIZE);

    private boolean writingNow = false;
    private ByteBuffer bufferWritingNow;
    private Queue<PairIntMetric> messages = new LinkedList<>();
    private final ByteBuffer bufferWrite = ByteBuffer.allocate(BUFFER_SIZE);

    private AllMetrics metrics;

    Client(int x, ExecutorService threadPool, Writer writer) {
        this.x = x;
        this.threadPool = threadPool;
        this.writer = writer;

        bufferRead.clear();
        bufferWrite.clear();
        bufferWrite.flip();

        metrics = new AllMetrics();
    }

    void read(SocketChannel socketChannel) {
        Serializer.readToBuffer(socketChannel, bufferRead);
        bufferRead.flip();

        while (bufferRead.hasRemaining()) {
            if (!readingNow) {
                sizeToRead = Serializer.readSize(bufferRead);
                if (sizeToRead == -1) {
                    break;
                }

                readingNow = true;
            }

            if (bufferRead.remaining() < sizeToRead) {
                break;
            }

            Metric clientOnTheServer = new Metric();
            clientOnTheServer.start();

            int[] array = Serializer.readArray(bufferRead, sizeToRead);
            Worker worker = new Worker(this, array, socketChannel, clientOnTheServer);
            threadPool.submit(worker);

            readingNow = false;
            readArrayCnt++;
        }

        bufferRead.compact();
    }

    boolean isReadingFinished() {
        return readArrayCnt == x;
    }

    Writer getWriter() {
        return writer;
    }

    void setToWrite(int[] array, Metric clientOnTheServer) {
        synchronized (bufferWrite) {
            bufferWrite.compact();
            int bytesCnt = Serializer.writeArray(bufferWrite, array);
            messages.add(new PairIntMetric(bytesCnt, clientOnTheServer));
            bufferWrite.flip();
        }
    }

    private boolean isWritingFinished() {
        return writeArrayCnt == x;
    }

    private void checkWritingFinished() {
        if (isWritingFinished()) {
            for (int i = 0; i < 2; i++) {
                metrics.get(i).div(x);
            }
        }
    }

    boolean write(SocketChannel socketChannel) {
        synchronized (bufferWrite) {
            if (!writingNow) {
                PairIntMetric pair = messages.poll();
                Metric clientOnTheServer = pair.clientOnTheServer;
                clientOnTheServer.stop();
                metrics.clientOnTheServer.add(clientOnTheServer);

                int bytesCnt = pair.bytesCnt;
                bufferWritingNow = ByteBuffer.allocate(bytesCnt);
                bufferWritingNow.clear();
                for (int i = 0; i < bytesCnt; i++) {
                    byte b = bufferWrite.get();
                    bufferWritingNow.put(b);
                }
                bufferWritingNow.flip();

                writingNow = true;
            }

            Serializer.writeFromBuffer(socketChannel, bufferWritingNow);
            if (bufferWritingNow.hasRemaining()) {
                return false;
            }

            writingNow = false;
            writeArrayCnt++;
            checkWritingFinished();
            return true;
        }
    }

    void updateRequestProcessing(Metric requestProcessing) {
        metrics.requestProcessing.add(requestProcessing);
    }

    AllMetrics getMetrics() {
        return metrics;
    }
}
