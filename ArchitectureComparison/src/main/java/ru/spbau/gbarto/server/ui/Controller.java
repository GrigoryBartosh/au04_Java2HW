package ru.spbau.gbarto.server.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import ru.spbau.gbarto.server.architecture.Server;
import ru.spbau.gbarto.server.architecture.blocking.BlockingArchitecture;
import ru.spbau.gbarto.server.architecture.blockingThreadPool.BlockingThreadPoolArchitecture;
import ru.spbau.gbarto.server.architecture.nonBlocking.NonBlockingArchitecture;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Controller {
    private static final int WAIT_SERVER_TTS = 10;

    private static final String DEFAULT_N = "2000";
    private static final String DEFAULT_N_FROM = "100";
    private static final String DEFAULT_N_TO = "5000";
    private static final String DEFAULT_N_STEP = "100";
    private static final String DEFAULT_M = "10";
    private static final String DEFAULT_M_FROM = "1";
    private static final String DEFAULT_M_TO = "25";
    private static final String DEFAULT_M_STEP = "1";
    private static final String DEFAULT_dT = "0";
    private static final String DEFAULT_dT_FROM = "0";
    private static final String DEFAULT_dT_TO = "100";
    private static final String DEFAULT_dT_STEP = "5";

    private static final String FOLDER = "results";

    private static int SERVER_PORT;
    private static String CLIENT_HOST;
    private static int CLIENT_PORT;

    private static Stage primaryStage;
    @FXML private ChoiceBox choiceArchitecture;
    @FXML private TextField fieldX;
    @FXML private ChoiceBox choiceParameter;
    @FXML private TextField fieldFrom;
    @FXML private TextField fieldTo;
    @FXML private TextField fieldStep;
    @FXML private Label labelParameter1;
    @FXML private TextField fieldParameter1;
    @FXML private Label labelParameter2;
    @FXML private TextField fieldParameter2;
    @FXML private ChoiceBox choiceMetric;
    @FXML private ImageView imageView;

    private static void readConfig() {
        try (InputStream input = new FileInputStream("./src/main/resources/config.properties")) {
            Properties prop = new Properties();
            prop.load(input);

            SERVER_PORT = Integer.parseInt(prop.getProperty("server.port"));
            CLIENT_HOST = prop.getProperty("client.host");
            CLIENT_PORT = Integer.parseInt(prop.getProperty("client.port"));
        } catch (IOException e) {
            System.err.println("Could not read config");
            System.exit(1);
        }
    }

    static void initialize(Stage primaryStage) throws IOException {
        Controller.primaryStage = primaryStage;
        Parent root = FXMLLoader.load(Controller.class.getResource("/menu.fxml"));

        primaryStage.setTitle("Server");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        readConfig();
    }

    @FXML
    public void variableChanged() {
        switch (choiceParameter.getValue().toString()) {
            case "N":
                fieldFrom.setText(DEFAULT_N_FROM);
                fieldTo.setText(DEFAULT_N_TO);
                fieldStep.setText(DEFAULT_N_STEP);
                labelParameter1.setText("M");
                fieldParameter1.setText(DEFAULT_M);
                labelParameter2.setText("dT");
                fieldParameter2.setText(DEFAULT_dT);
                break;

            case "M":
                fieldFrom.setText(DEFAULT_M_FROM);
                fieldTo.setText(DEFAULT_M_TO);
                fieldStep.setText(DEFAULT_M_STEP);
                labelParameter1.setText("N");
                fieldParameter1.setText(DEFAULT_N);
                labelParameter2.setText("dT");
                fieldParameter2.setText(DEFAULT_dT);
                break;

            case "dT":
                fieldFrom.setText(DEFAULT_dT_FROM);
                fieldTo.setText(DEFAULT_dT_TO);
                fieldStep.setText(DEFAULT_dT_STEP);
                labelParameter1.setText("N");
                fieldParameter1.setText(DEFAULT_N);
                labelParameter2.setText("M");
                fieldParameter2.setText(DEFAULT_M);
                break;
        }

        showGraph();
    }

    private void notifyUser(String s) {
        primaryStage.setTitle(s);
        System.out.println(s);
    }

    private int configureParameters(String parameterName, int from, int parameter1, int parameter2, int[] parameterSet) {
        switch (parameterName) {
            case "N":
                parameterSet[0] = from;
                parameterSet[1] = parameter1;
                parameterSet[2] = parameter2;
                return 0;

            case "M":
                parameterSet[0] = parameter1;
                parameterSet[1] = from;
                parameterSet[2] = parameter2;
                return 1;

            default:                          //dT
                parameterSet[0] = parameter1;
                parameterSet[1] = parameter2;
                parameterSet[2] = from;
                return 2;
        }
    }

    @NotNull
    private Server selectServer(String architecture, int[] parameterSet, int x) {
        switch (architecture) {
            case "Blocking":
                return new BlockingArchitecture(SERVER_PORT, parameterSet[1], x);

            case "Blocking (ThreadPool)":
                return new BlockingThreadPoolArchitecture(SERVER_PORT, parameterSet[1], x);

            default:                                                                  //"Non-Blocking"
                return new NonBlockingArchitecture(SERVER_PORT, parameterSet[1], x);
        }
    }

    private void startClients(int[] parameterSet, int x) {
        while (true) {
            try (Socket socket = new Socket(CLIENT_HOST, CLIENT_PORT)) {
                try (DataInputStream input = new DataInputStream(socket.getInputStream());
                     DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {

                    for (int i = 0; i < 3; i++) {
                        output.writeInt(parameterSet[i]);
                    }
                    output.writeInt(x);
                    output.flush();
                    socket.shutdownOutput();

                    if (input.readInt() != 0) {
                        throw new IOException();
                    }
                } catch (IOException e) {
                    System.err.println("Could not send config");
                    System.exit(1);
                }

                break;
            } catch (IOException ignored) { }
        }
    }

    private double waitMetricRequest() {
        double metricRequest = 0;
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            Socket socket = serverSocket.accept();

            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            metricRequest = input.readDouble();

            output.writeInt(0);

            output.flush();
            socket.shutdownOutput();
            socket.close();
        } catch (IOException e) {
            System.err.println("Failed to accept metric request from client");
            System.exit(1);
        }

        return metricRequest;
    }

    private void runServer(Server server, int[] parameterSet, int x) {
        Thread threadServer = new Thread(server);
        threadServer.setDaemon(false);
        threadServer.start();

        while (!server.isReady()) {
            try {
                Thread.sleep(WAIT_SERVER_TTS);
            } catch (InterruptedException ignored) { }
        }

        startClients(parameterSet, x);

        while (threadServer.isAlive()) {
            try {
                threadServer.join();
            } catch (InterruptedException ignored) { }
        }

        double metricRequest = waitMetricRequest();
        server.setMetricRequest(metricRequest);
    }

    @FXML
    public void start() {
        String architecture = choiceArchitecture.getValue().toString();
        String parameterName = choiceParameter.getValue().toString();
        int x, from, to, step, parameter1, parameter2;
        try {
            x = Integer.parseInt(fieldX.getText());
            from = Integer.parseInt(fieldFrom.getText());
            to = Integer.parseInt(fieldTo.getText());
            step = Integer.parseInt(fieldStep.getText());
            parameter1 = Integer.parseInt(fieldParameter1.getText());
            parameter2 = Integer.parseInt(fieldParameter2.getText());
        } catch (NumberFormatException e) {
            notifyUser("Incorrect field content");
            return;
        }

        String[] metricsNames = new String[]{"Request processing", "Client on the server", "Request"};
        List<MetricsList> metrics = new ArrayList<>();
        for (String name : metricsNames) {
            metrics.add(new MetricsList(architecture, parameterName, name, x, from, to, step, parameter1, parameter2));
        }

        int[] parameterSet = new int[3];
        int variableParameterNum = configureParameters(parameterName, from, parameter1, parameter2, parameterSet);
        if (parameterSet[0] == 0 || parameterSet[1] == 0) {      // N == 0 || M == 0
            notifyUser("Incorrect parameters value");
            return;
        }
        int cnt = (int)Math.ceil((double)(to - from + 1) / step);
        for (int stepNum = 0; parameterSet[variableParameterNum] <= to; parameterSet[variableParameterNum] += step, stepNum++) {
            Server server = selectServer(architecture, parameterSet, x);

            runServer(server, parameterSet, x);

            double[] nextMetrics = server.getMetrics();
            for (int i = 0; i < metrics.size(); i++) {
                metrics.get(i).add(nextMetrics[i]);
            }

            notifyUser(architecture + ", parameter: " + parameterName + ", Done: " + (stepNum+1) + " of " + cnt);
        }
        notifyUser("Done");

        metrics.forEach(MetricsList::save);
        rebuildGraphs();
        showGraph();
    }

    private void rebuildGraphs() {
        File dir = new File(FOLDER);
        File[] list = dir.listFiles();

        if (list == null) {
            return;
        }

        for (File file : list) {
            if (file.isDirectory()) {
                continue;
            }

            String name = file.getName();
            if (!name.endsWith(".txt")) {
                continue;
            }

            MetricsList metrics = new MetricsList(file.getAbsolutePath());
            GraphBuilder graph = new GraphBuilder();
            graph.add(metrics);

            String graphPath = "./" + FOLDER + "/" + name.replaceAll(".txt", "");
            graph.save(graphPath);
        }
    }

    @FXML
    public void showGraph() {
        String architecture = choiceArchitecture.getValue().toString();
        String parameterName = choiceParameter.getValue().toString();
        String metricsName = choiceMetric.getValue().toString();

        String name = "file:" + FOLDER + "/" + architecture + "_" + parameterName + "_" + metricsName + ".png";
        Image image = new Image(name);
        if (image.isError()) {
            image = new Image("emptyGraph.png");
        }
        imageView.setImage(image);
    }
}
