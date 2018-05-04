package ru.spbau.gbarto;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class Server implements Runnable {
    private int portNumber;

    private class Worker implements Runnable{
        private static final int BUFFER_SIZE = 4096;

        private Socket socket;

        private Worker(Socket socket) {
            this.socket = socket;
        }

        private String readString(DataInputStream input) throws IOException {
            StringBuilder string = new StringBuilder();

            byte[] buffer = new byte[BUFFER_SIZE];
            for (int length = input.read(buffer); length != -1; length = input.read(buffer)) {
                string.append(new String(buffer, 0, length));
            }

            return string.toString();
        }

        private void sendList(DataOutputStream output, String path) throws IOException {
            File file = new File(path);

            if (file.isDirectory()) {
                File[] list = file.listFiles();
                if (list == null) {
                    throw new IOException();
                }

                Arrays.sort(list, Comparator.comparing(File::getName));

                output.writeInt(list.length);

                for (File children : list) {
                    output.write(children.getPath().getBytes());
                    output.writeBoolean(children.isDirectory());
                }
            } else {
                output.writeInt(0);
            }
        }

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
            } catch (IOException e) {
                System.err.println("error with socket operation");
            }
        }
    }

    private Server(int portNumber) {
        this.portNumber = portNumber;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (!Thread.interrupted()) {
                Socket socket = serverSocket.accept();

                Thread communication = new Thread(new Worker(socket));
                communication.setDaemon(false);
                communication.start();
            }
        } catch (IOException e) {
            System.err.println("error while creating the server");
        }
    }

    public static void main(String args[]) {
        int portNumber = Integer.parseInt(args[0]);

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
