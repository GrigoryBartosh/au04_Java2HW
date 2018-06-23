package ru.spbau.gbarto.ui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.spbau.gbarto.log.Logger;
import ru.spbau.gbarto.logic.GameWithBotEasy;
import ru.spbau.gbarto.logic.GameWithBotHard;
import ru.spbau.gbarto.logic.Game;

import java.io.IOException;

public class MenuController {

    private static Stage primaryStage;
    private static Logger logger = new Logger();

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
        GameController.init(primaryStage, Game.class, logger);
    }

    public void singlePlayerEasy() throws IOException {
        GameController.init(primaryStage, GameWithBotEasy.class, logger);
    }

    public void singlePlayerHard() throws IOException {
        GameController.init(primaryStage, GameWithBotHard.class, logger);
    }

    public void statistic() throws IOException {
        StatisticController.init(primaryStage, logger);
    }

    public void exit() {
        Platform.exit();
    }
}
