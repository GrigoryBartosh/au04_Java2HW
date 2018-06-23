package ru.spbau.gbarto.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import ru.spbau.gbarto.logic.CellState;
import ru.spbau.gbarto.logic.Game;

import java.io.IOException;

public class GameController {

    private static Stage primaryStage;
    private static Class<?> gameClass;

    private static final BackgroundSize imgSize = new BackgroundSize(100,100,
            true,true, true, false);

    private static final BackgroundImage imgX = new BackgroundImage(
            new Image(GameController.class.getResourceAsStream("/cross.png")),
            BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER, imgSize);
    private static final BackgroundImage imgO = new BackgroundImage(
            new Image(GameController.class.getResourceAsStream("/nought.png")),
            BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER, imgSize);

    static private void update(Button[][] buttons, Game game) {
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (game.getCellState(x, y).equals(CellState.EMPTY)) {
                    continue;
                }

                boolean isX = game.getCellState(x, y).equals(CellState.IS_X);
                buttons[x][y].setBackground(new Background(isX ? imgX : imgO));
            }
        }

        switch (game.getState()) {
            case PROCESS:
                primaryStage.setTitle("Tic-Tac-Toe - Game");
                break;
            case X_WINS:
                primaryStage.setTitle("Tic-Tac-Toe - Game - X wins");
                break;
            case O_WINS:
                primaryStage.setTitle("Tic-Tac-Toe - Game - O wins");
                break;
            case DRAW:
                primaryStage.setTitle("Tic-Tac-Toe - Game - draw");
                break;
        }
    }

    private static void init() throws IOException {
        primaryStage.setTitle("Tic-Tac-Toe - Game");

        FXMLLoader loader = new FXMLLoader(GameController.class.getResource("/game.fxml"));
        Parent layout = loader.load();
        GridPane grid = (GridPane)loader.getNamespace().get("grid");
        Scene scene = new Scene(layout,
                primaryStage.getWidth(),
                primaryStage.getHeight());
        primaryStage.setScene(scene);

        Game game;
        try {
            game = (Game) gameClass.newInstance();
        } catch (Exception e) {
            System.err.println("Couldn't create the game");
            MenuController.init();
            return;
        }

        Button[][] buttons = new Button[3][3];
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                buttons[x][y] = new Button("");
                buttons[x][y].setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

                int fx = x;
                int fy = y;
                buttons[x][y].setOnAction(actionEvent -> {
                    synchronized (buttons) {
                        game.makeMove(fx, fy);
                        update(buttons, game);
                    }
                });

                grid.add(buttons[x][y], x, y);
            }
        }
    }

    static void init(Stage primaryStage, Class<?> gameClass) throws IOException {
        GameController.primaryStage = primaryStage;
        GameController.gameClass = gameClass;

        init();
    }

    public void back() throws IOException {
        MenuController.init();
    }

    public void restart() throws IOException {
        init();
    }
}
