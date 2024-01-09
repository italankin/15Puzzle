package com.italankin.fifteen.game;

import java.util.List;

public interface Game {

    int DIRECTION_DEFAULT = -1;
    int DIRECTION_UP = 0;
    int DIRECTION_RIGHT = 1;
    int DIRECTION_DOWN = 2;
    int DIRECTION_LEFT = 3;

    static int direction(float dx, float dy) {
        if (dx == 0 && dy == 0) {
            return DIRECTION_DEFAULT;
        }
        if (Math.abs(dx) > Math.abs(dy)) {
            return dx > 0 ? DIRECTION_RIGHT : DIRECTION_LEFT;
        } else {
            return dy > 0 ? DIRECTION_DOWN : DIRECTION_UP;
        }
    }

    /**
     * Attempt to move a tile at {x, y}, if possible
     *
     * @return new index of the tile, or the old index, if the move cannot be made
     */
    int move(int x, int y);

    /**
     * Find tiles we can move in a given {@code direction}, starting with tile at {@code startIndex}.
     * <br>
     * Given field:
     * <pre>
     *     1 2 3
     *     4 5 6
     *     7 - 8
     * </pre>
     * and {@code startIndex} of {@code 1} (number 2), and {@link #DIRECTION_DOWN}, the result will be {@code [5, 2]}
     *
     * @return a list of possible tiles to move
     */
    List<Integer> findMovingTiles(int startIndex, int direction);

    /**
     * @return current state of the game
     */
    List<Integer> getState();

    /**
     * @return final state of the game
     */
    List<Integer> getGoal();

    int getWidth();

    int getHeight();

    /**
     * @return direction in which tile at {@code index} can be moved
     * @see Game#DIRECTION_DEFAULT
     * @see Game#DIRECTION_DOWN
     * @see Game#DIRECTION_LEFT
     * @see Game#DIRECTION_UP
     * @see Game#DIRECTION_RIGHT
     */
    int getDirection(int index);

    int getMoves();

    boolean isSolved();

    /**
     * @return number of inversions in the current state
     * @see <a href="https://en.wikipedia.org/wiki/Parity_(mathematics)">Parity</a>
     */
    int inversions();

    int getSize();

    void setCallback(Callback callback);

    Game copy();

    interface Callback {

        void onGameStateUpdated(Game game);

        void onGameSolve(Game game);
    }
}
