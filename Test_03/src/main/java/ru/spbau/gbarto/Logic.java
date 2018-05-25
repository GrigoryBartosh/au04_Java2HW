package ru.spbau.gbarto;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Class describes logic of the game.
 */
class Logic {
    private static int[][] numbers;
    private static boolean[][] opened;

    private static Position firstPos;
    private static Position secondPos;

    /**
     * Class describes position of cell.
     */
    static private class Position {
        int x;
        int y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Shuffles the array.
     *
     * @param arr array which will be shuffled
     */
    private static void shuffleArray(int[] arr)
    {
        Random rnd = ThreadLocalRandom.current();
        for (int i = arr.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            int a = arr[index];
            arr[index] = arr[i];
            arr[i] = a;
        }
    }

    /**
     * Generates random numbers on the field.
     *
     * @param size of the field
     */
    private static void generateAlignment(int size) {
        int[] arr = new int[size * size];
        for (int i = 0; i < size * size; i += 2) {
            arr[i] = i / 2;
            arr[i+1] = i / 2;
        }

        shuffleArray(arr);

        numbers = new int[size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                numbers[x][y] = arr[y * size + x];
            }
        }
    }

    /**
     * Initializes the game.
     *
     * @param size of the field
     */
    static void init(int size) {
        opened = new boolean[size][size];

        generateAlignment(size);

        firstPos = null;
        secondPos = null;
    }

    /**
     * Returns true if the cell with the specified is open, false otherwise.
     *
     * @param pos position of the cell
     * @return true if the cell with the specified is open, false otherwise
     */
    static boolean isOpen(Position pos) {
        return (firstPos != null && firstPos.equals(pos)) || (secondPos != null && secondPos.equals(pos));
    }

    /**
     * Returns true if both cell with the specified is open, false otherwise.
     *
     * @return true if both cell with the specified is open, false otherwise
     */
    static boolean isBothOpen() {
        return firstPos != null && secondPos != null;
    }

    /**
     * Returns number of the specified cell.
     *
     * @param pos position of the cell
     * @return number of the specified cell
     */
    static int getNum(Position pos) {
        return numbers[pos.x][pos.y];
    }

    /**
     * Returns position of the first cell.
     *
     * @return position of the first cell
     */
    static Position getFirstPos() {
        return firstPos;
    }

    /**
     * Returns position of the second cell.
     *
     * @return position of the second cell
     */
    static Position getSecondPos() {
        return secondPos;
    }

    /**
     * Returns true if numbers of first and second cells are equals, false otherwise.
     *
     * @return true if numbers of first and second cells are equals, false otherwise
     */
    static boolean isSingleNum() {
        return firstPos != null && secondPos != null && getNum(firstPos) == getNum(secondPos);
    }

    /**
     * Marks pair of cells sa opened.
     */
    static void setOpen() {
        opened[firstPos.x][firstPos.y] = true;
        opened[secondPos.x][secondPos.y] = true;
    }

    /**
     * Opens the cell with the specified.
     *
     * @param pos position of the cell
     */
    static void open(Position pos) {
        if (opened[pos.x][pos.y]) {
            return;
        }

        if (firstPos == null) {
            firstPos = pos;
            return;
        }

        if (!firstPos.equals(secondPos)) {
            secondPos = pos;
        }
    }

    /**
     * Closes pair of points.
     */
    static void close() {
        firstPos = null;
        secondPos = null;
    }

    /**
     * Checks if game is finished.
     *
     * @return true if game is finished, false otherwise
     */
    static boolean isFinish() {
        for (boolean[] call : opened) {
            for (boolean f : call) {
                if (!f) {
                    return false;
                }
            }
        }
        return true;
    }
}
