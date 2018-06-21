package ru.spbau.gbarto;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

/**
 * Server. Allows you to receive and respond to requests on the network.
 */
public class Server implements Runnable {
    private int portNumber;

    /**
     * Interacts with a particular client.
     */
    private class Worker implements Runnable{
        private static final int BUFFER_SIZE = 4096;

        private Socket socket;

        private Worker(Socket socket) {
            this.socket = socket;
        }

        /**
         * Reads String transmitted over the network.
         *
         * @param input stream of clients data
         * @return the read String
         * @throws IOException if any error occurred while reading String
         */
        private String readString(DataInputStream input) throws IOException {
            StringBuilder string = new StringBuilder();

            byte[] buffer = new byte[BUFFER_SIZE];
            for (int length = input.read(buffer); length != -1; length = input.read(buffer)) {
                string.append(new String(buffer, 0, length));
            }

            return string.toString();
        }

        /**
         * Sends list of files in the directory.
         *
         * @param output stream to write data
         * @param path to the directory
         * @throws IOException if any error occurred while writing data
         */
        private void sendList(DataOutputStream output, String path) throws IOException {
            File file = new File(path);

            if (file.isDirectory()) {
                File[] list = file.listFiles();
                if (list == null) {
                    throw new IOException();
                }

                Arrays.sort(list, Comparator.comparing(File::getName));

                StringBuilder string = new StringBuilder();
                for (File children : list) {
                    string.append(children.getPath());
                    string.append(' ');
                    string.append(children.isDirectory());
                    string.append('\n');
                }

                output.writeInt(list.length);
                output.write(string.toString().getBytes());
            } else {
                output.writeInt(0);
            }
        }

        /**
         * Sends File by the path.
         *
         * @param output stream to write data
         * @param path to the file
         * @throws IOException if any error occurred while writing data
         */
        private void sendFile(DataOutputStream output, String path) throws IOException {
            File file = new File(path);

            if (file.isFile()) {
                output.writeLong(file.length());

                try (DataInputStream input = new DataInputStream(new FileInputStream(file))) {
                    byte[] buffer = new byte[BUFFER_SIZE];
                    for (int length = input.read(buffer); length != -1; length = input.read(buffer)) {
                        output.write(buffer, 0, length);
                    }
                }
            } else {
                output.writeLong(0);
            }
        }

        /**
         * Runs process of communication with client.
         */
        @Override
        public void run() {
            try (DataInputStream input = new DataInputStream(socket.getInputStream());
                 DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {
                int type = input.readInt();
                String path = readString(input);

                switch (type) {
                    case 1: sendList(output, path);
                            break;
                    case 2: sendFile(output, path);
                            break;
                    default: return;
                }

                output.flush();
                socket.close();
            } catch (IOException e) {
                System.err.println("error with socket operation");
            }
        }
    }

    /**
     * Constructs the Server.
     *
     * @param portNumber number of port for listening
     */
    public Server(int portNumber) {
        this.portNumber = portNumber;
    }

    /**
     * Runs server on the specified port and listens for connections.
     */
    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            serverSocket.setSoTimeout(2000);

            while (!Thread.interrupted()) {
                try {
                    Socket socket = serverSocket.accept();
                    Thread communication = new Thread(new Worker(socket));
                    communication.setDaemon(false);
                    communication.start();
                } catch (SocketTimeoutException ignored) {
                }
            }
        } catch (IOException e) {
            System.err.println("error while creating the server");
        }
    }

    /**
     * Runs server on the specified port.
     * Allows you to stop server.
     *
     * @param args list of arguments
     */
    public static void main(String args[]) {
        if (args.length < 1) {
            System.err.println("Not enough arguments");
            System.exit(1);
        }

        int portNumber = 0;
        try {
            portNumber = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Incorrect input data");
            System.exit(1);
        }

        Thread thread = new Thread(new Server(portNumber));
        thread.setDaemon(false);
        thread.start();

        Scanner input = new Scanner(System.in);
        while (!thread.getState().equals(Thread.State.TERMINATED)) {
            String command =  input.nextLine();
            if (command.equals("exit")) {
                break;
            } else {
                System.out.println("\"" + command + "\" is unknown command");
            }
        }
        thread.interrupt();
        System.out.println("server is off");
    }
}