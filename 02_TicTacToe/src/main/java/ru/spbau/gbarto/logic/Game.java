package ru.spbau.gbarto.logic;

import ru.spbau.gbarto.log.Logger;

public class Game {

    private static int dx[] = {-1, 0, 1, 1};
    private static int dy[] = { 1, 1, 1, 0};

    GameState state;
    CellState[][] field;
    private boolean xTurn;

    GameState calcState(CellState[][] field) {
        int sumFree = 0;
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (field[x][y].equals(CellState.EMPTY)) {
                    sumFree++;
                    continue;
                }

                for (int i = 0; i < 4; i++) {
                    int tx, ty;
                    tx = x + dx[i];
                    ty = y + dy[i];

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

    public Game() {
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

    public void makeMove(int x, int y) {
        if (!state.equals(GameState.PROCESS)) {
            return;
        }

        if (!field[x][y].equals(CellState.EMPTY)) {
            return;
        }

        field[x][y] = xTurn ? CellState.IS_X : CellState.IS_O;
        xTurn = !xTurn;
        state = calcState(field);



        switch (state) {
            case X_WINS:
                Logger.add(Game.class.toString(), "X wins");
                break;
            case O_WINS:
                Logger.add("hot", "O wins");
                break;
            case DRAW:
                Logger.add("hot", "draw");
                break;
        }
    }
}
