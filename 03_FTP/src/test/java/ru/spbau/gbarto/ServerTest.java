package ru.spbau.gbarto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import org.apache.commons.io.FileUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

class ServerTest {
    private static final String hostName = "localhost";
    private static final int portNumber = 8002;
    private Thread thread;

    @BeforeEach
    void runServer() throws InterruptedException {
        thread = new Thread(new Server(portNumber));
        thread.setDaemon(true);
        thread.start();

        Thread.sleep(100);
    }

    @AfterEach
    void stopServer() throws InterruptedException {
        while (!thread.getState().equals(Thread.State.TERMINATED)) {
            thread.interrupt();
            Thread.sleep(100);
        }
    }

    @Test
    void testList() throws InterruptedException {
        String request = "1 src" + File.separator + "test" + File.separator + "resources";

        ByteArrayInputStream input = new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        new Client(hostName, portNumber, input, output).run();

        String ans =    "5\n" +
                        "src" + File.separator + "test" + File.separator + "resources" + File.separator + "empty.txt false\n" +
                        "src" + File.separator + "test" + File.separator + "resources" + File.separator + "file.txt false\n" +
                        "src" + File.separator + "test" + File.separator + "resources" + File.separator + "not_empty.txt false\n" +
                        "src" + File.separator + "test" + File.separator + "resources" + File.separator + "singleFileFolder true\n" +
                        "src" + File.separator + "test" + File.separator + "resources" + File.separator + "someTree true";

        assertEquals(ans.trim(), output.toString().trim());
    }

    @Test
    void testGetFile() throws IOException {
        String request = "2 src" + File.separator + "test" + File.separator + "resources" + File.separator + "file.txt";

        ByteArrayInputStream input = new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        new Client(hostName, portNumber, input, output).run();

        assertTrue(new File("file.txt").exists());
        assertTrue(FileUtils.contentEquals(new File("file.txt"),
                new File("src" + File.separator + "test" + File.separator + "resources" + File.separator + "file.txt")));

        assertTrue(new File("file.txt").delete());
    }
}