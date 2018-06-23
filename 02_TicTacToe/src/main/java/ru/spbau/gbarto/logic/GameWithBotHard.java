package ru.spbau.gbarto.logic;

import ru.spbau.gbarto.Cell;
import ru.spbau.gbarto.log.Logger;

public class GameWithBotHard extends Game {
    private static final String GAME_TYPE = "bot hard";

    public GameWithBotHard(Logger logger) {
        super(logger);

        setGameType(GAME_TYPE);
    }

    private Cell getWay() {
        if (calcState().equals(GameState.X_WINS)) {
            return null;
        }
        if (calcState().equals(GameState.DRAW)) {
            return new Cell(-1, -1);
        }

        for (int xO = 0; xO < 3; xO++) {
            for (int yO = 0; yO < 3; yO++) {
                if (!getCellState(xO, yO).equals(CellState.EMPTY)) {
                    continue;
                }

                setCellState(xO, yO, CellState.IS_O);

                if (calcState().equals(GameState.O_WINS)) {
                    setCellState(xO, yO, CellState.EMPTY);
                    return new Cell(xO, yO);
                }

                boolean canLose = false;
                for (int xX = 0; xX < 3 && !canLose; xX++) {
                    for (int yX = 0; yX < 3 && !canLose; yX++) {
                        if (!getCellState(xX, yX).equals(CellState.EMPTY)) {
                            continue;
                        }

                        setCellState(xX, yX, CellState.IS_X);
                        Cell way = getWay();
                        setCellState(xX, yX, CellState.EMPTY);

                        if (way == null) {
                            canLose = true;
                        }
                    }
                }

                setCellState(xO, yO, CellState.EMPTY);

                if (!canLose) {
                    return new Cell(xO, yO);
                }
            }
        }

        return null;
    }

    @Override
    public Cell makeMove(int x, int y) {
        super.makeMove(x, y);

        if (!getState().equals(GameState.PROCESS)) {
            return null;
        }

        Cell ans = getWay();
        if (ans == null) {
            throw new RuntimeException();
        }

        super.makeMove(ans.getX(), ans.getY());

        return ans;
    }
}
