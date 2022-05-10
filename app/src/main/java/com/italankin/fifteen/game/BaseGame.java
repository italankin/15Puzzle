package com.italankin.fifteen.game;

import com.italankin.fifteen.Logger;
import com.italankin.fifteen.Tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

abstract class BaseGame implements Game {

    protected static final Random random = new Random();

    protected final int width;
    protected final int height;
    protected final List<Integer> state;
    protected final List<Integer> goal;

    protected int moves;
    protected boolean solved;

    protected Callback callback = null;

    BaseGame(int width, int height, List<Integer> goal, boolean randomMissingTile) {
        this.width = width;
        this.height = height;
        this.goal = new ArrayList<>(goal);

        int size = width * height;
        int missingTile = randomMissingTile ? (1 + random.nextInt(size)) : size;
        if (missingTile != size) {
            this.goal.set(this.goal.indexOf(0), size);
            this.goal.set(this.goal.indexOf(missingTile), 0);
        }

        List<Integer> state;
        do {
            state = initRandomState(missingTile);
        } while (state.equals(this.goal));

        this.state = state;

        Logger.d("init: inversions=%d, state=%s", inversions(), state);
    }

    BaseGame(int width,
            int height,
            List<Integer> state,
            List<Integer> goal,
            int moves) {
        this.width = width;
        this.height = height;
        this.moves = moves;
        this.goal = new ArrayList<>(goal);
        this.state = new ArrayList<>(state);

        Set<Integer> numbers = new HashSet<>(state);
        int size = width * height;
        for (int i = 0; i < size; i++) {
            int number = i + 1;
            if (!numbers.contains(number)) {
                this.goal.set(this.goal.indexOf(0), size);
                this.goal.set(this.goal.indexOf(number), 0);
                break;
            }
        }
    }

    BaseGame(int width, int height, List<Integer> state, List<Integer> goal) {
        this.width = width;
        this.height = height;
        this.goal = goal;
        this.state = new ArrayList<>(state);
        if (!isSolvable()) {
            throw new IllegalStateException(
                    String.format("goal=%s (%d inversions) is unreachable from state=%s (%d inversions)",
                            goal, inversions(goal), state, inversions(state))
            );
        }
    }

    BaseGame(BaseGame game) {
        this.width = game.width;
        this.height = game.height;
        this.state = new ArrayList<>(game.state);
        this.goal = game.goal;
        this.moves = game.moves;
        this.solved = game.solved;
        this.callback = game.callback;
    }

    @Override
    public int inversions() {
        return inversions(state);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int move(int x, int y) {
        // find position in array of tile at [x, y]
        int pos = y * width + x;

        if (state.get(pos) == 0) {
            return pos;
        }
        int zeroPos = state.indexOf(0);
        int x0 = zeroPos % width;
        int y0 = zeroPos / width;

        // if distance to zero more than 1, we cant move
        if (Tools.manhattan(x0, y0, x, y) > 1) {
            return pos;
        }

        Collections.swap(state, pos, zeroPos);
        moves++;

        solved = state.equals(goal);
        if (solved && callback != null) {
            callback.onGameSolve();
        }

        return zeroPos;
    }

    @Override
    public List<Integer> findMovingTiles(int startIndex, int direction) {
        if (startIndex < 0) {
            // maybe we're outside the universe
            return Collections.emptyList();
        }

        int x, y;

        int x1 = startIndex % width;
        int y1 = startIndex / width;

        int zeroPos = state.indexOf(0);
        int x0 = zeroPos % width;
        int y0 = zeroPos / width;

        ArrayList<Integer> result = new ArrayList<>();

        if (direction == DIRECTION_DEFAULT) {
            direction = getDirection(startIndex);
        }

        switch (direction) {
            case DIRECTION_UP:
                // check we're moving tiles in the same column
                if (x1 != x0) {
                    break;
                }
                for (y = y0 + 1; y < Math.min(height, y1 + 1); y++) {
                    result.add(y * width + x0);
                }
                break;

            case DIRECTION_RIGHT:
                // check we're moving tiles in the same row
                if (y1 != y0) {
                    break;
                }
                for (x = x0 - 1; x >= x1; x--) {
                    result.add(y0 * width + x);
                }
                break;

            case DIRECTION_DOWN:
                // check we're moving tiles in the same column
                if (x1 != x0) {
                    break;
                }
                for (y = y0 - 1; y >= y1; y--) {
                    result.add(y * width + x0);
                }
                break;

            case DIRECTION_LEFT:
                // check we're moving tiles in the same row
                if (y1 != y0) {
                    break;
                }
                for (x = x0 + 1; x < Math.min(width, x1 + 1); x++) {
                    result.add(y0 * width + x);
                }
                break;
        }

        return result;
    }

    @Override
    public List<Integer> getGrid() {
        return state;
    }

    @Override
    public List<Integer> getSolvedGrid() {
        return goal;
    }

    @Override
    public int getDirection(int index) {
        int zeroPos = state.indexOf(0);
        int dx = zeroPos % width - index % width;
        int dy = zeroPos / width - index / width;
        return Game.direction(dx, dy);
    }

    @Override
    public int getMoves() {
        return moves;
    }

    @Override
    public boolean isSolved() {
        return solved;
    }

    @Override
    public int getSize() {
        return state.size();
    }

    @Override
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private List<Integer> initRandomState(int missingTile) {
        List<Integer> result = new ArrayList<>(goal);
        Collections.shuffle(result, random);

        if (!isSolvable(result, goal, width)) {
            // if puzzle is not solvable
            // we swap last two digits (e.g. 14 and 15)

            int size = height * width;
            int last, secondLast;
            if (missingTile == size) {
                last = size - 1;
                secondLast = size - 2;
            } else {
                last = size;
                secondLast = last - 1;
                if (missingTile == secondLast) {
                    secondLast = missingTile - 1;
                }
            }
            Collections.swap(result, result.indexOf(last), result.indexOf(secondLast));
        }
        return result;
    }

    private boolean isSolvable() {
        return isSolvable(state, goal, width);
    }

    private static boolean isSolvable(List<Integer> state, List<Integer> goal, int width) {
        int stateInversions = inversions(state);
        int goalInversions = inversions(goal);
        if (width % 2 == 0) {
            int goalZeroRowIndex = goal.indexOf(0) / width;
            int startZeroRowIndex = state.indexOf(0) / width;
            // if 'goalInversions' is even
            // 'stateInversions' and difference between 'goal' zero row index and zero row index in the 'state'
            // should have the same parity
            // if 'goalInversions' is odd, 'stateInversions' and difference must have different parity

            // since we're interested only in parity, an optimization is possible
            return goalInversions % 2 == (stateInversions + goalZeroRowIndex + startZeroRowIndex) % 2;
        }
        // 'startInversions' should have the same parity as 'goalInversions'
        return stateInversions % 2 == goalInversions % 2;
    }

    /**
     * Calculate number of inversions in the {@code list}
     */
    private static int inversions(List<Integer> list) {
        int inversions = 0;
        int size = list.size();
        // for every number we need to count:
        // - numbers less than chosen
        // - follow chosen number (by rows)
        for (int i = 0; i < size; i++) {
            int n = list.get(i);
            if (n <= 1) {
                continue;
            }
            for (int j = i + 1; j < size; j++) {
                int m = list.get(j);
                if (m > 0 && n > m) {
                    inversions++;
                }
            }
        }
        return inversions;
    }
}
