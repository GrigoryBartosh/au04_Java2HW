package ru.spbau.gbarto;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class Serializer {
    public static void writeArray(DataOutputStream output, int[] array) throws IOException {
        ArrayMessage.Array.Builder builder = ArrayMessage.Array.newBuilder();

        for (int x : array) {
            builder.addData(x);
        }

        builder.build().writeDelimitedTo(output);
    }

    private static int[] toArray(List<Integer> list) {
        Integer[] arrayInteger = list.toArray(new Integer[list.size()]);
        int[] array = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = arrayInteger[i];
        }

        return array;
    }

    public static int[] readArray(DataInputStream input) throws IOException {
        List<Integer> list = ArrayMessage.Array.parseDelimitedFrom(input).getDataList();
        return toArray(list);
    }
}
