package ru.spbau.gbarto.ui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.spbau.gbarto.logic.BotEasy;
import ru.spbau.gbarto.logic.BotHard;
import ru.spbau.gbarto.logic.Game;

import java.io.IOException;

public class MenuController {

    private static Stage primaryStage;

    static void init() throws IOException {
        primaryStage.setTitle("Tic-Tac-Toe");

        Parent layout = FXMLLoader.load(MenuController.class.getResource("/menu.fxml"));
        Scene scene = new Scene(layout,
                                primaryStage.getWidth(),
                                primaryStage.getHeight());
        primaryStage.setScene(scene);
    }

    public static void init(Stage primaryStage) throws IOException {
        MenuController.primaryStage = primaryStage;

        init();
    }

    public void hotSeat() throws IOException {
        GameController.init(primaryStage, Game.class);
    }

    public void singlePlayerEasy() throws IOException {
        GameController.init(primaryStage, BotEasy.class);
    }

    public void singlePlayerHard() throws IOException {
        GameController.init(primaryStage, BotHard.class);
    }

    public void statistic() throws IOException {
        StatisticController.init(primaryStage);
    }

    public void exit() {
        Platform.exit();
    }
}
