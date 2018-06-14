package ru.spbau.gbarto.client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class ClientMain {
    private static int PORT;
    private static String SERVER_HOST;
    private static int SERVER_PORT;

    private static int n;
    private static int m;
    private static int delta;
    private static int x;

    private static void readConfig() {
        try (InputStream input = new FileInputStream("./src/main/resources/config.properties")) {
            Properties prop = new Properties();
            prop.load(input);

            PORT = Integer.parseInt(prop.getProperty("client.port"));
            SERVER_HOST = prop.getProperty("server.host");
            SERVER_PORT = Integer.parseInt(prop.getProperty("server.port"));
        } catch (IOException e) {
            System.err.println("Could not read config");
            System.exit(1);
        }
    }

    private static void waitParameters() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            Socket socket = serverSocket.accept();

            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            n = input.readInt();
            m = input.readInt();
            delta = input.readInt();
            x = input.readInt();

            output.writeInt(0);

            output.flush();
            socket.shutdownOutput();
            socket.close();
        } catch (IOException e) {
            System.err.println("Failed to accept settings from server");
            System.exit(1);
        }
    }

    private static void doRequests() {
        Thread[] thread = new Thread[m];
        for (int i = 0; i < m; i++) {
            thread[i] = new Thread(new Client(SERVER_HOST, SERVER_PORT, n, delta, x));
        }

        for (int i = 0; i < m; i++) {
            thread[i].start();
        }

        for (int i = 0; i < m; i++) {
            while (thread[i].isAlive()) {
                try {
                    thread[i].join();
                } catch (InterruptedException ignored) { }
            }
        }
    }

    public static void main(String[] args) {
        readConfig();

        while (true) {
            waitParameters();
            doRequests();
        }
    }
}
