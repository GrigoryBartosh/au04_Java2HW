package ru.spbau.gbarto.logic;

import org.junit.jupiter.api.Test;
import ru.spbau.gbarto.Cell;
import ru.spbau.gbarto.log.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameWithBotTest {
    private List<Cell> getAllCells() {
        List<Cell> allCells = new ArrayList<>();
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                allCells.add(new Cell(x, y));
            }
        }
        Collections.shuffle(allCells);

        return allCells;
    }

    private List<CellState> getField(Game game) {
        List<CellState> field= new ArrayList<>();

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                field.add(game.getCellState(x, y));
            }
        }

        return field;
    }

    private void playGame(Game game) {
        List<Cell> allCells = getAllCells();

        for (int i = 0; game.getState().equals(GameState.PROCESS); i++) {
            int xX = allCells.get(i).getX();
            int yX = allCells.get(i).getY();

            if (!game.getCellState(xX, yX).equals(CellState.EMPTY)) {
                continue;
            }

            List<CellState> fieldBefore = getField(game);
            Cell cell = game.makeMove(xX, yX);

            if (cell == null) {
                continue;
            }

            int xO = cell.getX();
            int yO = cell.getY();
            List<CellState> fieldAfter = getField(game);

            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    int num = x * 3 + y;

                    if (x == xX && y == yX) {
                        assertEquals(CellState.EMPTY, fieldBefore.get(num));
                        assertEquals(CellState.IS_X, fieldAfter.get(num));
                        continue;
                    }

                    if (x == xO && y == yO) {
                        assertEquals(CellState.EMPTY, fieldBefore.get(num));
                        assertEquals(CellState.IS_O, fieldAfter.get(num));
                        continue;
                    }

                    assertEquals(fieldBefore.get(num), fieldAfter.get(num));
                }
            }
        }
    }

    @Test
    void testBotCellState() {
        playGame(new GameWithBotEasy(new Logger()));
        playGame(new GameWithBotHard(new Logger()));
    }

    @Test
    void testBotHardDoesNotLose() {
        for (int i = 0; i < 10; i++) {
            Game game = (new GameWithBotHard(new Logger()));

            assertNotEquals(GameState.X_WINS, game.getState());
        }
    }
}