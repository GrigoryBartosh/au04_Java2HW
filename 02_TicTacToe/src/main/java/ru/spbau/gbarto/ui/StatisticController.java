package ru.spbau.gbarto.ui;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import ru.spbau.gbarto.log.Logger;

import java.io.IOException;

public class StatisticController {

    static void init(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Tic-Tac-Toe - Statistic");

        FXMLLoader loader = new FXMLLoader(GameController.class.getResource("/statistic.fxml"));
        Parent layout = loader.load();
        TableView table = (TableView)loader.getNamespace().get("table");
        Scene scene = new Scene(layout,
                primaryStage.getWidth(),
                primaryStage.getHeight());
        primaryStage.setScene(scene);

        ObservableList<Logger.Game> data = table.getItems();
        data.addAll(Logger.getResults());
    }

    public void back(ActionEvent actionEvent) throws IOException {
        MenuController.init();
    }
}
