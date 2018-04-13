package ru.spbau.gbarto;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ForkJoinPool;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MyTest {
    private static final String RESOURCES = "./src/test/resources/";

    private static String useFJP(MD5File file, int cores) {
        ForkJoinPool pool = new ForkJoinPool(cores);
        FScanner task = new FScanner(file);
        return pool.invoke(task);
    }

    @Test
    void testRecursiveVSFJP() {
        MD5File file = new MD5File(RESOURCES);
        String h1 = useFJP(file, 1);
        String h2 = file.getMD5();

        assertEquals(h1, h2);
    }

    @Test
    void testCoreCount() {
        MD5File file = new MD5File(RESOURCES);
        String h1 = useFJP(file, 1);
        String h2 = useFJP(file, 2);
        String h3 = useFJP(file, 3);
        String h4 = useFJP(file, 4);

        assertEquals(h1, h2);
        assertEquals(h2, h3);
        assertEquals(h3, h4);
    }

    @Test
    void testEmptyFolder() {
        MD5File file = new MD5File(RESOURCES + "empty/");
        String md5 = useFJP(file, 4);

        assertEquals("A2E4822A98337283E39F7B60ACF85EC9", md5);
    }

    @Test
    void testEmptyFile() {
        MD5File file = new MD5File(RESOURCES + "empty.txt");
        String md5 = useFJP(file, 4);

        assertEquals("D41D8CD98F00B204E9800998ECF8427E", md5);
    }

    @Test
    void testNotEmptyFile() {
        MD5File file = new MD5File(RESOURCES + "not_empty.txt");
        String md5 = useFJP(file, 4);

        assertEquals("F75B8179E4BBE7E2B4A074DCEF62DE95", md5);
    }

    @Test
    void testSingleFileFolder() {
        MD5File file = new MD5File(RESOURCES + "singleFileFolder/");
        String md5 = useFJP(file, 4);

        assertEquals("46B30449A79D224F3893B3A0D66B7AD8", md5);
    }

    @Test
    void testSomeTree() {
        MD5File file = new MD5File(RESOURCES + "someTree/");
        String md5 = useFJP(file, 4);

        assertEquals("68D33EB7A61A38BA68999001B49B0600", md5);
    }

    @Test
    void testAll() {
        MD5File file = new MD5File(RESOURCES);
        String md5 = useFJP(file, 4);

        assertEquals("F2559B144AF34953008594B3F03C5775", md5);
    }
}