package ru.spbau.gbarto.log;

import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows you to save the results of games.
 */
public class Logger {

    private List<Game> results = new ArrayList<>();

    /**
     * Adds result of one game in the list of all results.
     *
     * @param type type of the game
     * @param result result of the game (hwo won)
     */
    public void add(String type, String result) {
        results.add(new Game(type, result));
    }

    /**
     * Return results of all games.
     *
     * @return results of all games
     */
    public List<Game> getResults() {
        return new ArrayList<>(results);
    }

    /**
     * Contains information about one game.
     * Type of hte game and hwo won.
     */
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
