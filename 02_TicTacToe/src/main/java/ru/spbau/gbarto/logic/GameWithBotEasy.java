package ru.spbau.gbarto.logic;

import ru.spbau.gbarto.log.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameWithBotEasy extends Game {
    private static final String GAME_TYPE = "bot easy";

    private static List<Cell> cells;
    static {
        cells = new ArrayList<>();
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                cells.add(new Cell(x, y));
            }
        }
    }

    public GameWithBotEasy(Logger logger) {
        super(logger);

        setGameType(GAME_TYPE);
    }

    @Override
    public void makeMove(int x, int y) {
        super.makeMove(x, y);

        if (!getState().equals(GameState.PROCESS)) {
            return;
        }

        Collections.shuffle(cells);
        for (Cell p : cells) {
            int tx = p.getX();
            int ty = p.getY();
            if (getCellState(tx, ty).equals(CellState.EMPTY)) {
                super.makeMove(tx, ty);
                break;
            }
        }
    }
}
