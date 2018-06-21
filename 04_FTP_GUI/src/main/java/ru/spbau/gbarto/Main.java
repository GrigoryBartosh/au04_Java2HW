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
public class Main extends Application{
    static String hostName;
    static int portNumber;
    static Stage primaryStage;

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

        String hostName = args[0];
        int portNumber = 0;
        try {
            portNumber = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Incorrect input data");
            System.exit(1);
        }

        launch(args);
    }
}