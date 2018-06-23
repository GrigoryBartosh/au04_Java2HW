package ru.spbau.gbarto.log;

import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Logger {

    private List<Game> results = new ArrayList<>();

    public void add(String type, String result) {
        results.add(new Game(type, result));
    }

    public List<Game> getResults() {
        return new ArrayList<>(results);
    }

    public static class Game {

        private final SimpleStringProperty date;
        private final SimpleStringProperty type;
        private final SimpleStringProperty result;

        public Game(String type, String result) {
            date = new SimpleStringProperty(DateTimeFormatter.ofPattern("MM/dd HH:mm").format(LocalDateTime.now()));
            this.type = new SimpleStringProperty(type);
            this.result = new SimpleStringProperty(result);
        }

        public String getDate() {
            return date.get();
        }

        public String getType() {
            return type.get();
        }

        public String getResult() {
            return result.get();
        }
    }
}
