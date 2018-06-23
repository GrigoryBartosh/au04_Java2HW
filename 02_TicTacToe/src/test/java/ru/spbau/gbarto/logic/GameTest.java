package ru.spbau.gbarto.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.spbau.gbarto.log.Logger;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private Game game;

    @BeforeEach
    void init() {
        Logger logger = new Logger();
        game = new Game(logger);
    }

    /**
     *   012
     * 0 xo.
     * 1 xo.
     * 2 ...
     */
    @Test
    void testCellState() {
        game.makeMove(0, 0);
        game.makeMove(0, 1);
        game.makeMove(1, 0);
        game.makeMove(1, 1);

        assertEquals(CellState.IS_X, game.getCellState(0, 0));
        assertEquals(CellState.IS_O, game.getCellState(0, 1));
        assertEquals(CellState.IS_X, game.getCellState(1, 0));
        assertEquals(CellState.IS_O, game.getCellState(1, 1));
        assertEquals(CellState.EMPTY, game.getCellState(2, 0));
        assertEquals(CellState.EMPTY, game.getCellState(2, 1));
    }

    /**
     *   012
     * 0 ...
     * 1 ...
     * 2 ...
     */
    @Test
    void testGameStateEmpty() {
        assertEquals(GameState.PROCESS, game.getState());
    }

    /**
     *   012
     * 0 xo.
     * 1 xo.
     * 2 x..
     */
    @Test
    void testGameStateXColumn() {
        game.makeMove(0, 0);
        game.makeMove(0, 1);
        game.makeMove(1, 0);
        game.makeMove(1, 1);
        game.makeMove(2, 0);

        assertEquals(GameState.X_WINS, game.getState());
    }

    /**
     *   012
     * 0 ...
     * 1 oo.
     * 2 xxx
     */
    @Test
    void testGameStateXLine() {
        game.makeMove(2, 0);
        game.makeMove(1, 0);
        game.makeMove(2, 1);
        game.makeMove(1, 1);
        game.makeMove(2, 2);

        assertEquals(GameState.X_WINS, game.getState());
    }

    /**
     *   012
     * 0 x.o
     * 1 .o.
     * 2 oxx
     */
    @Test
    void testGameStateODiagonal() {
        game.makeMove(0, 0);
        game.makeMove(1, 1);
        game.makeMove(2, 2);
        game.makeMove(0, 2);
        game.makeMove(1, 2);
        game.makeMove(2, 0);

        assertEquals(GameState.O_WINS, game.getState());
    }

    /**
     *   012
     * 0 xox
     * 1 xoo
     * 2 oxx
     */
    @Test
    void testGameStateDraw() {
        game.makeMove(0, 0);
        game.makeMove(1, 1);
        game.makeMove(2, 2);
        game.makeMove(0, 2);
        game.makeMove(2, 0);
        game.makeMove(1, 0);
        game.makeMove(1, 2);
        game.makeMove(2, 1);
        game.makeMove(0, 1);

        assertEquals(GameState.DRAW, game.getState());
    }
}