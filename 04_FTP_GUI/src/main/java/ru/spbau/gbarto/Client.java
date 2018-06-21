package ru.spbau.gbarto;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Allows you to communicate with Server on the network.
 */
public class Client implements Runnable {
    private static final int BUFFER_SIZE = 4096;

    private String hostName;
    private int portNumber;

    private Scanner input;
    private PrintWriter output;

    /**
     * Constructs the Client.
     *
     * @param hostName name of server host
     * @param portNumber number of server port
     * @param inputStream stream from which we can read queries
     * @param outputStream stream to which we would write responses
     */
    public Client(String hostName, int portNumber, InputStream inputStream, OutputStream outputStream) {
        this.hostName = hostName;
        this.portNumber = portNumber;

        this.input = new Scanner(inputStream);
        this.output = new PrintWriter(outputStream, true);
    }

    /**
     * Gets list of files in the directory.
     *
     * @param dataInput stream to read data
     * @throws IOException if any error occurred while reading data
     */
    private void getList(DataInputStream dataInput) throws IOException {
        int size = dataInput.readInt();

        StringBuilder list = new StringBuilder();
        byte[] buffer = new byte[BUFFER_SIZE];
        for (int length = dataInput.read(buffer); length != -1; length = dataInput.read(buffer)) {
            list.append(new String(buffer, 0, length));
        }

        output.println(size);
        output.println(list.toString());
    }

    /**
     * Gets File by the path.
     *
     * @param dataInput stream to read data
     * @param path path to the file
     * @throws IOException if any error occurred while reading data
     */
    private void getFile(DataInputStream dataInput, String path) throws IOException {
        long size = dataInput.readLong();
        if (size == 0) {
            return;
        }

        String filename = new File(path).getName();
        try (DataOutputStream dataOutput = new DataOutputStream(new FileOutputStream(new File(filename)))) {
            byte[] buffer = new byte[BUFFER_SIZE];
            for (int read = dataInput.read(buffer); read != -1; read = dataInput.read(buffer)) {
                dataOutput.write(buffer, 0, read);
            }
        } catch (FileNotFoundException e) {
            System.err.println("error writing to file");
        }
    }

    /**
     * Processes the request to the server.
     *
     * @param type of request
     * @param path to file or directory
     */
    private void processRequest(int type, String path) {
        try (Socket socket = new Socket(hostName, portNumber);
             DataInputStream dataInput = new DataInputStream(socket.getInputStream());
             DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream())) {
            dataOutput.writeInt(type);
            dataOutput.write(path.getBytes());
            dataOutput.flush();
            socket.shutdownOutput();

            switch (type) {
                case 1:
                    getList(dataInput);
                    break;
                case 2:
                    getFile(dataInput, path);
                    break;
            }
        } catch (IOException e) {
            System.err.println("error while working with server");
        }
    }

    /**
     * Runs process of communication with server.
     */
    @Override
    public void run() {
        while (input.hasNext()) {
            String type = input.next();

            if (type.equals("1") || type.equals("2")) {
                int typeInt = Integer.parseInt(type);
                String path = input.nextLine().trim();
                processRequest(typeInt, path);
            } else if (type.equals("exit")) {
                return;
            }
        }
    }

    /**
     * Runs client.
     *
     * @param args list of arguments
     */
    public static void main(String args[]) {
        if (args.length < 2) {
            System.err.println("Not enough arguments");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = 0;
        try {
            portNumber = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Incorrect input data");
            System.exit(1);
        }

        new Client(hostName, portNumber, System.in, System.out).run();
    }
}