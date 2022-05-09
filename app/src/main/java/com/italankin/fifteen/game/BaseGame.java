package com.italankin.fifteen.game;

import com.italankin.fifteen.Logger;
import com.italankin.fifteen.Tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

abstract class BaseGame implements Game {

    protected static final Random random = new Random();

    protected final int width;
    protected final int height;
    protected final List<Integer> grid;
    protected List<Integer> solvedGrid;

    protected int moves;
    protected boolean solved;
    protected final int missingTile;

    protected Callback callback = null;

    BaseGame(int width, int height, boolean randomMissingTile) {
        this.width = width;
        this.height = height;
        int size = width * height;
        this.missingTile = randomMissingTile ? (1 + random.nextInt(size)) : size;
        grid = new ArrayList<>(size);

        do {
            initGrid();
        } while (grid.equals(solvedGrid));

        Logger.d("init: inversions%d, grid=%s", inversions(), grid);
    }

    BaseGame(int width,
            int height,
            List<Integer> savedGrid,
            int savedMoves) {
        this.width = width;
        this.height = height;
        moves = savedMoves;
        solvedGrid = generateSolved();
        grid = new ArrayList<>(savedGrid);

        int size = width * height;
        int missingTile = -1;
        for (int i = 0; i < size; i++) {
            int number = i + 1;
            if (!grid.contains(number)) {
                missingTile = number;
                solvedGrid.set(solvedGrid.indexOf(0), size);
                solvedGrid.set(solvedGrid.indexOf(number), 0);
                break;
            }
        }
        this.missingTile = missingTile;
    }

    BaseGame(BaseGame game) {
        this.width = game.width;
        this.height = game.height;
        this.missingTile = game.missingTile;
        this.grid = new ArrayList<>(game.grid);
        this.solvedGrid = game.solvedGrid;
        this.moves = game.moves;
        this.solved = game.solved;
        this.callback = game.callback;
    }

    protected abstract List<Integer> generateSolved();

    protected abstract boolean isSolvable();

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

        if (grid.get(pos) == 0) {
            return pos;
        }
        int zeroPos = grid.indexOf(0);
        int x0 = zeroPos % width;
        int y0 = zeroPos / width;

        // if distance to zero more than 1, we cant move
        if (Tools.manhattan(x0, y0, x, y) > 1) {
            return pos;
        }

        Collections.swap(grid, pos, zeroPos);
        moves++;

        solved = grid.equals(solvedGrid);
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

        int zeroPos = grid.indexOf(0);
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
        return grid;
    }

    @Override
    public List<Integer> getSolvedGrid() {
        return solvedGrid;
    }

    @Override
    public int getDirection(int index) {
        int zeroPos = grid.indexOf(0);
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
        return grid.size();
    }

    @Override
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private void initGrid() {
        solvedGrid = generateSolved();
        grid.clear();
        grid.addAll(solvedGrid);
        Collections.shuffle(grid, random);

        int size = height * width;
        int last = size - 1;
        int secondLast = size - 2;

        if (missingTile != size) {
            last = size;
            solvedGrid.set(solvedGrid.indexOf(0), last);
            solvedGrid.set(solvedGrid.indexOf(missingTile), 0);
            grid.set(grid.indexOf(0), last);
            grid.set(grid.indexOf(missingTile), 0);

            secondLast = last - 1;
            if (missingTile == secondLast) {
                secondLast = missingTile - 1;
            }
        }

        if (!isSolvable()) {
            // if puzzle is not solvable
            // we swap last two digits (e.g. 14 and 15)
            Collections.swap(grid, grid.indexOf(last), grid.indexOf(secondLast));
        }
    }
}
