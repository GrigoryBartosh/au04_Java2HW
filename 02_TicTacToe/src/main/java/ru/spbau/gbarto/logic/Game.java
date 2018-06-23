package ru.spbau.gbarto.logic;

import ru.spbau.gbarto.Cell;
import ru.spbau.gbarto.log.Logger;

public class Game {
    private static final String GAME_TYPE = "hot seat";

    private static int dx[] = {-1, 0, 1, 1};
    private static int dy[] = { 1, 1, 1, 0};

    private String gameType = GAME_TYPE;

    private GameState state;
    private CellState[][] field;
    private boolean xTurn;

    private Logger logger;

    void setGameType(String type) {
        gameType = type;
    }

    GameState calcState() {
        int sumFree = 0;
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (field[x][y].equals(CellState.EMPTY)) {
                    sumFree++;
                    continue;
                }

                for (int i = 0; i < 4; i++) {
                    int tx = x + dx[i];
                    int ty = y + dy[i];

                    if (tx < 0 || tx > 2 || ty < 0 || ty > 2) {
                        continue;
                    }
                    if (!field[x][y].equals(field[tx][ty])) {
                        continue;
                    }

                    tx = x - dx[i];
                    ty = y - dy[i];

                    if (tx < 0 || tx > 2 || ty < 0 || ty > 2) {
                        continue;
                    }
                    if (!field[x][y].equals(field[tx][ty])) {
                        continue;
                    }

                    return field[x][y].equals(CellState.IS_X) ? GameState.X_WINS : GameState.O_WINS;
                }
            }
        }

        if (sumFree == 0) {
            return GameState.DRAW;
        } else {
            return GameState.PROCESS;
        }
    }

    public Game(Logger logger) {
        this.logger = logger;

        field = new CellState[3][3];
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                field[x][y] = CellState.EMPTY;
            }
        }
        state = GameState.PROCESS;
        xTurn = true;
    }

    public GameState getState() {
        return state;
    }

    public CellState getCellState(int x, int y) {
        return field[x][y];
    }

    void setCellState(int x, int y, CellState cellState) {
        field[x][y] = cellState;
    }

    public Cell makeMove(int x, int y) {
        if (!state.equals(GameState.PROCESS)) {
            return null;
        }

        if (!field[x][y].equals(CellState.EMPTY)) {
            return null;
        }

        field[x][y] = xTurn ? CellState.IS_X : CellState.IS_O;
        xTurn = !xTurn;
        state = calcState();

        switch (state) {
            case X_WINS:
                logger.add(gameType, "X wins");
                break;
            case O_WINS:
                logger.add(gameType, "O wins");
                break;
            case DRAW:
                logger.add(gameType, "draw");
                break;
        }

        return new Cell(x, y);
    }
}
