package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private static int size;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.show();

        Controller.init(primaryStage, 5);
    }

    private static int readArgs(String args[]) {
        if (args.length == 0) {
            System.err.println("Not enough arguments");
            System.exit(1);
        }

        int size = Integer.parseInt(args[0]);

        if (size % 2 == 1 || size == 0 || size > 16) {
            System.err.println("Incorrect arguments");
            System.exit(1);
        }

        return size;
    }

    public static void main(String[] args) {
        launch(args);
        size = readArgs(args);
    }
}
