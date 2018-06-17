package ru.spbau.gbarto.client;

import ru.spbau.gbarto.Metric;

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

    private static double doRequests() {
        Client[] clients = new Client[m];
        Thread[] threads = new Thread[m];
        for (int i = 0; i < m; i++) {
            clients[i] = new Client(SERVER_HOST, SERVER_PORT, n, delta, x);
            threads[i] = new Thread(clients[i]);
            threads[i].setDaemon(false);
        }

        for (int i = 0; i < m; i++) {
            threads[i].start();
        }

        Metric metricRequest = new Metric();
        for (int i = 0; i < m; i++) {
            while (threads[i].isAlive()) {
                try {
                    threads[i].join();
                } catch (InterruptedException ignored) { }
            }

            metricRequest.add(clients[i].getMetric());
        }
        metricRequest.div(m);

        return metricRequest.get();
    }

    private static void sendMetricRequest(double metricRequest) {
        while (true) {
            try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT)) {
                try (DataInputStream input = new DataInputStream(socket.getInputStream());
                     DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {

                    output.writeDouble(metricRequest);
                    output.flush();
                    socket.shutdownOutput();

                    if (input.readInt() != 0) {
                        throw new IOException();
                    }
                } catch (IOException e) {
                    System.err.println("Could not send metricRequest");
                    System.exit(1);
                }

                break;
            } catch (IOException ignored) { }
        }
    }

    public static void main(String[] args) {
        readConfig();

        while (true) {
            waitParameters();
            double metricRequest = doRequests();
            sendMetricRequest(metricRequest);
        }
    }
}
