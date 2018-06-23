package ru.spbau.gbarto;

import javafx.application.Application;
import javafx.stage.Stage;
import ru.spbau.gbarto.ui.MenuController;

/**
 * Tic-tac-toe graphical application.
 * Supports single and hot seat playing mods.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.show();
        primaryStage.setMinWidth(300);
        primaryStage.setMinHeight(300);
        primaryStage.setWidth(300);
        primaryStage.setHeight(300);

        MenuController.init(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}