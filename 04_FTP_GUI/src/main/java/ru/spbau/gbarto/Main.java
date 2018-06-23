package ru.spbau.gbarto;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Applications main class.
 * Allows you to go to view and download files.
 */
public class Main extends Application {
    private static String hostName;
    private static int portNumber;
    private static Stage primaryStage;

    static String getHostName() {
        return hostName;
    }

    static int getPortNumber() {
        return portNumber;
    }

    static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Updates title of primaryStage.
     *
     * @param str new title.
     */
    static void updateTitle(String str) {
        primaryStage.setTitle(str);
    }

    /**
     * Starts the application.
     *
     * @param primaryStage a stage to display content
     * @throws Exception if something went wrong
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("/menu.fxml"));
        primaryStage.setMinHeight(300);
        primaryStage.setMinWidth(400);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Reads hostname and port of the server.
     *
     * @param args arguments contents hostname and ports
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Not enough arguments");
            System.exit(1);
        }

        hostName = args[0];
        portNumber = 0;
        try {
            portNumber = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Incorrect input data");
            System.exit(1);
        }

        launch(args);
    }
}