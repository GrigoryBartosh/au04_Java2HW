package ru.spbau.gbarto;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Activity that starts game.
 */
public class Main extends Application {
    private static int size;
    private static int cellSize;

    private static Button[][] buttons;

    /**
     * Reads size of field from input, and checks input.
     *
     * @param args input arguments
     * @return size of field
     */
    private static int readSize(String args[]) {
        if (args.length == 0) {
            System.err.println("Not enough arguments");
            System.exit(1);
        }

        int size = Integer.parseInt(args[0]);

        if (size % 2 == 1 || size == 0 || size > 8) {
            System.err.println("Incorrect arguments");
            System.exit(1);
        }

        return size;
    }

    public static void main(String[] args) {
        size = readSize(args);
        cellSize = 300 / size;

        launch(args);
    }

    /**
     * Configures GridPane.
     *
     * @param grid GridPane which will be configured
     */
    private static void configGrid(GridPane grid) {
        grid.setPadding(new Insets(10));
        grid.setHgap(0);
        grid.setVgap(0);
    }

    /**
     * Opens pair of cells until the end of the game.
     *
     * @param firstPos position of first cell
     * @param secondPos position of second cell
     */
    private static void openPair(Logic.Position firstPos, Logic.Position secondPos) {
        Logic.setOpen();
        buttons[firstPos.x][firstPos.y].setDisable(true);
        buttons[secondPos.x][secondPos.y].setDisable(true);
    }

    /**
     * Closes pair of different cells.
     *
     * @param firstPos position of first cell
     * @param secondPos position of second cell
     */
    private static void closePair(Logic.Position firstPos, Logic.Position secondPos) {
        new Timeline(new KeyFrame(Duration.millis(500), task -> {
            if (!Logic.isOpen(firstPos)) {
                buttons[firstPos.x][firstPos.y].setText("");
            }
            if (!Logic.isOpen(secondPos)) {
                buttons[secondPos.x][secondPos.y].setText("");
            }
        })).play();
    }

    /**
     * Configures one button.
     *
     * @param grid GridPane which will be configured
     * @param x coordinate of cell
     * @param y coordinate of cell
     */
    private static void configButton(GridPane grid, int x, int y) {
        buttons[x][y] = new Button("");
        buttons[x][y].setMinWidth(cellSize);
        buttons[x][y].setMinHeight(cellSize);

        buttons[x][y].setOnAction(actionEvent -> {
            Button button = buttons[x][y];
            Logic.Position pos = new Logic.Position(x, y);

            if (Logic.isOpen(pos)) {
                return;
            }

            Logic.open(pos);
            button.setText(String.valueOf(Logic.getNum(pos)));

            if (Logic.isBothOpen()) {
                Logic.Position firstPos = Logic.getFirstPos();
                Logic.Position secondPos = Logic.getSecondPos();

                if (Logic.isSingleNum()) {
                    openPair(firstPos, secondPos);
                } else {
                    closePair(firstPos, secondPos);
                }

                Logic.close();
            }

            if (Logic.isFinish()) {
                new Timeline(new KeyFrame(Duration.millis(1500), task -> System.exit(0))).play();
            }
        });

        grid.add(buttons[x][y], x, y);
    }

    /**
     * Configures Scene and shows PrimaryStage.
     *
     * @param primaryStage which will be shown
     * @param grid GridPane which will be put on scene
     */
    private static void configScene(Stage primaryStage, GridPane grid) {
        Scene scene = new Scene(grid, 320, 320);

        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Starts game.
     *
     * @param primaryStage specified primary stage
     * @throws Exception if something goes wrong
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Logic.init(size);

        buttons = new Button[size][size];

        GridPane grid = new GridPane();
        configGrid(grid);

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                configButton(grid, x, y);
            }
        }

        configScene(primaryStage, grid);
    }
}
