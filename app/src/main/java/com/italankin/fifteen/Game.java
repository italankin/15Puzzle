package com.italankin.fifteen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Game {

    private static Game instance = new Game();
    private int width;
    private int height;
    private List<Integer> grid = new ArrayList<>();
    private List<Integer> solvedGrid;

    private int zeroPos;
    private int moves;
    private long time;
    private boolean solved = false;
    private boolean paused = false;

    private boolean peeking = false;

    private Callback mCallback = null;

    public static void create(int w, int h) {
        instance.init(w, h);
    }

    public static void load(List<Integer> savedGrid, int savedMoves, long savedTime) {
        instance.grid = savedGrid;
        instance.moves = savedMoves;
        instance.time = savedTime;
        instance.zeroPos = savedGrid.indexOf(0);
    }

    public void init(int w, int h) {
        height = h;
        width = w;
        int size = height * width;

        grid.clear();

        solvedGrid = Generator.fill(w, h, Settings.gameMode);
        grid.addAll(solvedGrid);

        Collections.shuffle(grid, new Random());

        zeroPos = grid.indexOf(0);

        moves = 0;
        time = 0;
        solved = false;
        paused = false;
        peeking = false;

        if (!isSolvable()) {
            // if puzzle is not solvable
            // we swap last two digits (e.g. 14 and 15)
            Collections.swap(grid, grid.indexOf(size - 1), grid.indexOf(size - 2));
        }

        // a rare case where we have solved puzzle, create another
        if (checkSolution() && size > 3) {
            init(width, height);
        }
    }

    private boolean isSolvable() {
        int sum = 0, size = height * width;
        int n, m, s;
        switch (Settings.gameMode) {
            case Constants.MODE_CLASSIC:
                // for every number we need to count:
                // - numbers less than chosen
                // - follow chosen number (by rows)
                for (int i = 0; i < size; i++) {
                    n = grid.get(i);
                    s = 0;
                    for (int j = i + 1; j < size; j++) {
                        m = grid.get(j);
                        if (n > m && m > 0) {
                            s++;
                        }
                    }
                    sum += s;
                }
                // if we got an even number of columns
                // we need to add row number (counting from down) where zero is located
                if (width % 2 == 0) {
                    int z = height - zeroPos / width;
                    if (z % 2 == 0) {
                        return sum % 2 == 1;
                    }
                }
                // sum should be even
                return sum % 2 == 0;

            case Constants.MODE_SNAKE:
                // same as above, but for even rows we reverse the order
                for (int i = 0; i < size; i++) {
                    if ((i / width) % 2 == 0) {
                        n = grid.get(i);
                    } else {
                        n = grid.get(width * (1 + i / width) - i % width - 1);
                    }
                    s = 0;
                    for (int j = i + 1; j < size; j++) {
                        if ((j / width) % 2 == 0) {
                            m = grid.get(j);
                        } else {
                            m = grid.get(width * (1 + j / width) - j % width - 1);
                        }
                        if (n > m && m > 0) {
                            s++;
                        }
                    }
                    sum += s;
                }
                return sum % 2 == 0;
        }

        return false;
    }

    private boolean checkSolution() {
        int size = height * width;
        int v, i;

        switch (Settings.gameMode) {
            case Constants.MODE_CLASSIC:
                for (i = 0; i < size - 1; i++) {
                    if (grid.get(i) != (i + 1)) {
                        return false;
                    }
                }
                break;

            case Constants.MODE_SNAKE:
                for (i = 0; i < size - 1; i++) {
                    if ((i / width) % 2 == 0) {
                        v = i + 1;
                        if (grid.get(i) != v) {
                            return false;
                        }
                    } else {
                        v = (width * (1 + i / width) - i % width);
                        if (v == size) {
                            v = 0;
                        }
                        if (grid.get(i) != v) {
                            return false;
                        }
                    }
                }
                break;
        }
        return true;
    }

    /**
     * Move a tile on [x, y]
     *
     * @return new index of index
     */
    public static int move(int x, int y) {
        // find position in array of tile at [x, y]
        int pos = y * instance.width + x;

        if (instance.grid.get(pos) == 0) {
            return pos;
        }
        int x0 = instance.zeroPos % instance.width;
        int y0 = instance.zeroPos / instance.width;

        // if distance to zero more than 1, we cant move
        if (Tools.manhattan(x0, y0, x, y) > 1) {
            return pos;
        }

        Collections.swap(instance.grid, pos, instance.zeroPos);
        int newPos = instance.zeroPos;
        instance.zeroPos = pos;

        instance.moves++;

        if (!Settings.hardmode && instance.mCallback != null) {
            // on hard mode checkSolvedHm should be called to check if puzzle is solved
            if (instance.checkSolution()) {
                instance.solved = true;
                instance.mCallback.onGameSolve();
            }
        }

        return newPos;
    }

    /**
     * Find elements we should move in a given {@code direction} starting at {@code startIndex}
     *
     * @return numbers to move
     */
    public static List<Integer> findMovingTiles(int direction, int startIndex) {
        if (startIndex < 0) {
            // maybe we're outside the universe
            return Collections.emptyList();
        }

        int x, y;

        int x1 = startIndex % instance.width;
        int y1 = startIndex / instance.width;

        int x0 = instance.zeroPos % instance.width;
        int y0 = instance.zeroPos / instance.width;

        ArrayList<Integer> result = new ArrayList<>();

        switch (direction) {
            case Tools.DIRECTION_UP:
                // check we're moving tiles in the same column
                if (x1 != x0) {
                    break;
                }
                for (y = y0 + 1; y < Math.min(instance.height, y1 + 1); y++) {
                    result.add(y * instance.width + x0);
                }
                break;

            case Tools.DIRECTION_RIGHT:
                // check we're moving tiles in the same row
                if (y1 != y0) {
                    break;
                }
                for (x = x0 - 1; x >= Math.max(0, x1); x--) {
                    result.add(y0 * instance.width + x);
                }
                break;

            case Tools.DIRECTION_DOWN:
                // check we're moving tiles in the same column
                if (x1 != x0) {
                    break;
                }
                for (y = y0 - 1; y >= Math.max(0, y1); y--) {
                    result.add(y * instance.width + x0);
                }
                break;

            case Tools.DIRECTION_LEFT:
                // check we're moving tiles in the same row
                if (y1 != y0) {
                    break;
                }
                for (x = x0 + 1; x < Math.min(instance.width, x1 + 1); x++) {
                    result.add(y0 * instance.width + x);
                }
                break;
        }

        return result;
    }

    public static int getAt(int index) {
        return instance.grid.get(index);
    }

    /**
     * Find direction in which we should move a tile at {@code index}
     *
     * @see Tools#direction(float, float)
     */
    public static int getDirection(int index) {
        //noinspection IntegerDivisionInFloatingPointContext
        return Tools.direction(instance.zeroPos % instance.width - index % instance.width,
                instance.zeroPos / instance.width - index / instance.width);
    }

    public static int getMoves() {
        return instance.moves;
    }

    public static void incTime(long time) {
        if (instance.moves > 0) {
            instance.time += time;
        }
    }

    public static long getTime() {
        return instance.time;
    }

    public static boolean isSolved() {
        return instance.solved;
    }

    public static boolean checkSolvedHm() {
        if (instance.checkSolution()) {
            instance.solved = true;
            if (instance.mCallback != null) {
                instance.mCallback.onGameSolve();
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean isPeeking() {
        return instance.peeking;
    }

    public static void setPeeking(boolean peeking) {
        instance.peeking = peeking;
    }

    public static boolean isNotStarted() {
        return instance.moves == 0;
    }

    public static void setPaused(boolean paused) {
        instance.paused = paused;
    }

    public static void invertPaused() {
        instance.paused = !instance.paused;
    }

    public static boolean isPaused() {
        return instance.paused;
    }

    public static String getGridStr() {
        String gridAsStr = instance.grid.toString();
        return gridAsStr.substring(1, gridAsStr.length() - 1);
    }

    public static int getSize() {
        return instance.grid.size();
    }

    public static void setCallback(Callback callback) {
        instance.mCallback = callback;
    }

    public interface Callback {

        void onGameSolve();
    }
}
