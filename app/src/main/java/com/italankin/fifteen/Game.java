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
        if (checkSolution()) {
            init(width, height);
        }
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

    public static int indexOfSolved(int number) {
        return instance.solvedGrid.indexOf(number);
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

    private boolean checkSolution() {
        return grid.equals(solvedGrid);
    }

    private boolean isSolvable() {
        switch (Settings.gameMode) {
            case Constants.MODE_CLASSIC:
                return checkSolvableClassic();

            case Constants.MODE_SNAKE:
                return checkSolvableSnake();

            case Constants.MODE_SPIRAL:
                return checkSolvableSpiral();

            default:
                throw new IllegalStateException("Unknown gameMode=" + Settings.gameMode);
        }
    }

    private boolean checkSolvableClassic() {
        int inversions = 0;
        int size = height * width;
        // for every number we need to count:
        // - numbers less than chosen
        // - follow chosen number (by rows)
        for (int i = 0; i < size; i++) {
            int n = grid.get(i);
            for (int j = i + 1; j < size; j++) {
                int m = grid.get(j);
                if (m > 0 && n > m) {
                    inversions++;
                }
            }
        }
        Tools.log("inversions=" + inversions);
        // we need to add row number (counting from down) where zero is located
        if (width % 2 == 0) {
            // if we got an even width
            int z = height - zeroPos / width;
            if (z % 2 == 0) {
                // and row number where zero is even
                // number of inversions must be odd
                return inversions % 2 == 1;
            }
            // else number of inversions must be even
        }
        // inversions should be even
        return inversions % 2 == 0;
    }

    private boolean checkSolvableSnake() {
        int inversions = 0;
        int size = height * width;
        for (int i = 0; i < size; i++) {
            int n;
            if ((i / width) % 2 == 0) {
                n = grid.get(i);
            } else {
                n = grid.get(width * (1 + i / width) - i % width - 1);
            }
            for (int j = i + 1; j < size; j++) {
                int m;
                if ((j / width) % 2 == 0) {
                    m = grid.get(j);
                } else {
                    m = grid.get(width * (1 + j / width) - j % width - 1);
                }
                if (m > 0 && n > m) {
                    inversions++;
                }
            }
        }
        Tools.log("inversions=" + inversions);
        return inversions % 2 == 0;
    }

    private boolean checkSolvableSpiral() {
        int inversions = 0;
        List<Integer> list = traverseSpiral();
        int size = list.size();
        int max = size - 1; // for last number count will be always 0
        for (int i = 0; i < max; i++) {
            int n = list.get(i);
            for (int j = i + 1; j < size; j++) {
                int m = list.get(j);
                if (m > 0 && m < n) {
                    inversions++;
                }
            }
        }
        Tools.log("inversions=" + inversions);
        return inversions % 2 == 0;
    }

    /**
     * Return game grid in spiral order.
     * For example, if we have grid in this form:
     * <pre>
     * 1 2 3
     * 8 0 4
     * 7 6 5
     * </pre>
     * then resulting list will be {@code [1, 2, 3, 4, 5, 6, 7, 8, 0]}
     */
    private List<Integer> traverseSpiral() {
        int h = height, w = width;
        int r = 0, c = 0;
        List<Integer> result = new ArrayList<>(width * height);
        while (r < h && c < w) {
            for (int i = c; i < w; i++) {
                result.add(grid.get(r * width + i));
            }
            r++;
            for (int i = r; i < h; i++) {
                result.add(grid.get(i * width + w - 1));
            }
            w--;
            if (r < h) {
                for (int i = w - 1; i >= c; i--) {
                    result.add(grid.get((h - 1) * width + i));
                }
                h--;
            }
            if (c < w) {
                for (int i = h - 1; i >= r; i--) {
                    result.add(grid.get(i * width + c));
                }
                c++;
            }
        }
        return result;
    }

    public interface Callback {

        void onGameSolve();
    }
}
