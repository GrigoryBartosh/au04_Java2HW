package ru.spbau.gbarto;

import java.util.concurrent.ForkJoinPool;

public class Main {
    private static final String DEFAULT_PATH = ".";

    private static String useFJP(MD5File file) {
        ForkJoinPool pool = new ForkJoinPool(4);
        FScanner task = new FScanner(file);
        return pool.invoke(task);
    }

    private static void test() {
        MD5File file = new MD5File(DEFAULT_PATH);

        long t = System.currentTimeMillis();
        useFJP(file);
        System.out.print("FJP time = ");
        System.out.println(System.currentTimeMillis() - t);

        t = System.currentTimeMillis();
        useFJP(file);
        System.out.print("Recursive time = ");
        System.out.println(System.currentTimeMillis() - t);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            test();
        } else {
            long t = System.currentTimeMillis();

            MD5File file = new MD5File(args[0]);
            String md5 = useFJP(file);

            System.out.println("MD5 = " + md5);
            System.out.print("time = ");
            System.out.println(System.currentTimeMillis() - t);
        }
    }
}
