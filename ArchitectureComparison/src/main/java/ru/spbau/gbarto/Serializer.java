package ru.spbau.gbarto;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;

public class Serializer {
    private static int[] toArray(List<Integer> list) {
        Integer[] arrayInteger = list.toArray(new Integer[list.size()]);
        int[] array = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = arrayInteger[i];
        }

        return array;
    }

    private static int[] toIntArray(byte[] data) {
        List<Integer> list = null;
        try {
            list = ArrayMessage.Array.parseFrom(data).getDataList();
        } catch (IOException e) {
            System.err.println("Serializer: Error parse from byte array");
            System.exit(1);
        }

        return toArray(list);
    }

    private static byte[] toByteArray(int[] array) {
        ArrayMessage.Array.Builder builder = ArrayMessage.Array.newBuilder();

        for (int x : array) {
            builder.addData(x);
        }

        return builder.build().toByteArray();
    }

    public static int[] readArray(DataInputStream input) {
        byte[] data = null;
        try {
            int size = input.readInt();
            data = new byte[size];

            for (int i = 0; i < size; i++) {
                data[i] = input.readByte();
            }
        } catch (IOException e) {
            System.err.println("Serializer: Error read array from DataInputStream");
            System.exit(1);
        }

        return toIntArray(data);
    }

    public static void writeArray(DataOutputStream output, int[] array) {
        try {
            byte[] data = toByteArray(array);
            output.writeInt(data.length);
            output.write(data);
        } catch (IOException e) {
            System.err.println("Serializer: Error write array to DataOutputStream");
            System.exit(1);
        }
    }

    public static void readToBuffer(SocketChannel socketChannel, ByteBuffer buffer) {
        try {
            int bytesCnt;
            do {
                bytesCnt = socketChannel.read(buffer);
            } while (bytesCnt > 0);
        } catch (IOException e) {
            System.err.println("Serializer: Error read to buffer");
            System.exit(1);
        }
    }

    public static int readSize(ByteBuffer buffer) {
        if (buffer.remaining() < 4) {
            return -1;
        }

        return buffer.getInt();
    }

    public static int[] readArray(ByteBuffer buffer, int size) {
        byte[] data = new byte[size];

        for (int i = 0; i < size; i++) {
            data[i] = buffer.get();
        }

        return toIntArray(data);
    }

    public static int writeArray(ByteBuffer buffer, int[] array) {
        byte[] data = toByteArray(array);
        buffer.putInt(data.length);
        buffer.put(data);

        return data.length + 4;
    }

    public static void writeFromBuffer(SocketChannel socketChannel, ByteBuffer buffer) {
        try {
            int bytesCnt;
            do {
                bytesCnt = socketChannel.write(buffer);
            } while (bytesCnt > 0);
        } catch (IOException e) {
            System.err.println("Serializer: Error write from buffer");
            System.exit(1);
        }
    }
}
