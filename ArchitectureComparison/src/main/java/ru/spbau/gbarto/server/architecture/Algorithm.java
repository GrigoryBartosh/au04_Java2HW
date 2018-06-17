package ru.spbau.gbarto.server.architecture;

public class Algorithm {
    private static long bubbleSort(int[] array) {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array.length - 1; j++) {
                if (array[j] > array[j + 1]) {
                    array[j] ^= array[j + 1];
                    array[j + 1] ^= array[j];
                    array[j] ^= array[j + 1];
                }
            }
        }

        return System.currentTimeMillis() - startTime;
    }

    public static long sort(int[] array) {
        return bubbleSort(array);
    }
}
