package ru.spbau.gbarto.logic;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BotEasy extends Game {

    private static List<Pair<Integer, Integer>> cells;
    static {
        cells = new ArrayList<>();
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                cells.add(new Pair<>(x, y));
            }
        }
    }

    @Override
    public void makeMove(int x, int y) {
        super.makeMove(x, y);

        if (!state.equals(GameState.PROCESS)) {
            return;
        }

        Collections.shuffle(cells);
        for (Pair p : cells) {
            int tx = (Integer) p.getKey();
            int ty = (Integer) p.getValue();
            if (field[tx][ty].equals(CellState.EMPTY)) {
                super.makeMove(tx, ty);
                break;
            }
        }
    }
}
