package ru.spbau.gbarto.logic;

import javafx.util.Pair;

public class BotHard extends Game {

    private Pair<Integer, Integer> getWay(CellState[][] field) {
        if (calcState(field).equals(GameState.X_WINS)) {
            return null;
        }
        if (calcState(field).equals(GameState.DRAW)) {
            return new Pair<>(-1,-1);
        }

        for (int xO = 0; xO < 3; xO++) {
            for (int yO = 0; yO < 3; yO++) {
                if (!field[xO][yO].equals(CellState.EMPTY)) {
                    continue;
                }

                field[xO][yO] = CellState.IS_O;

                if (calcState(field).equals(GameState.O_WINS)) {
                    field[xO][yO] = CellState.EMPTY;
                    return new Pair<>(xO, yO);
                }

                boolean can_lose = false;
                for (int xX = 0; xX < 3 && !can_lose; xX++) {
                    for (int yX = 0; yX < 3 && !can_lose; yX++) {
                        if (!field[xX][yX].equals(CellState.EMPTY)) {
                            continue;
                        }

                        field[xX][yX] = CellState.IS_X;
                        Pair way = getWay(field);
                        field[xX][yX] = CellState.EMPTY;

                        if (way == null) {
                            can_lose = true;
                        }
                    }
                }

                field[xO][yO] = CellState.EMPTY;

                if (!can_lose) {
                    return new Pair<>(xO, yO);
                }
            }
        }

        return null;
    }

    @Override
    public void makeMove(int x, int y) {
        super.makeMove(x, y);

        if (!state.equals(GameState.PROCESS)) {
            return;
        }

        Pair p = getWay(field);
        super.makeMove((Integer) p.getKey(), (Integer) p.getValue());
    }
}
