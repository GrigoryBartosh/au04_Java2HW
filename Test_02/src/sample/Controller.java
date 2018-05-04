package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Controller {
    private static boolean firstMarked;
    private static int firstX;
    private static int firstY;

    private static int[][] numbers;
    private static Button[][] buttons;
    private static boolean[][] opened;

    private static void shuffleArray(int[] arr)
    {
        Random rnd = ThreadLocalRandom.current();
        for (int i = arr.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            int a = arr[index];
            arr[index] = arr[i];
            arr[i] = a;
        }
    }

    private static void generateAlignment(int size) {
        int[] arr = new int[size * size];
        for (int i = 0; i < size * size / 2; i += 2) {
            arr[i] = arr[i+1] = i / 2;
        }

        shuffleArray(arr);

        numbers = new int[size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                numbers[x][y] = arr[y * size + x];
            }
        }
    }

    private static void openCell(int x, int y) {
        opened[x][y] = true;
        buttons[x][y].setText(Integer.toString(numbers[x][y]));
    }

    private static void createButtons(GridPane grid, int size) {
        buttons = new Button[size][size];
        opened = new boolean[size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                buttons[x][y] = new Button("");
                buttons[x][y].setPrefSize(50, 50);

                int fx = x;
                int fy = y;
                buttons[x][y].setOnAction(actionEvent -> {
                    if (opened[fx][fy]) {
                        return;
                    }

                    if (firstMarked) {
                        openCell(firstX, firstY);
                        openCell(fx, fy);
                    } else {
                        firstMarked = true;
                        firstX = fx;
                        firstY = fy;
                    }
                });

                grid.add(buttons[x][y], x, y);
            }
        }
    }

    static void init(Stage primaryStage, int size) throws IOException {
        FXMLLoader loader = new FXMLLoader(Controller.class.getResource("sample.fxml"));
        Parent layout = loader.load();

        GridPane grid = (GridPane)loader.getNamespace().get("grid");

        Scene scene = new Scene(layout, 50 * size, 50 * size);
        primaryStage.setScene(scene);

        generateAlignment(size);
        createButtons(grid, size);
    }
}
