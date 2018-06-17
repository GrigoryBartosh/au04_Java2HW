package ru.spbau.gbarto.server.ui;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class ServerMain extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Controller.initialize(primaryStage);
    }

    public static void main(String[] args) throws IOException {
        launch(args);
    }
}

