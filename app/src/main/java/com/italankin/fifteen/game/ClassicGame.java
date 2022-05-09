package com.italankin.fifteen.game;

import com.italankin.fifteen.Constants;
import com.italankin.fifteen.Generator;

import java.util.List;

/**
 * Classic variation:
 * <pre>
 * 1 2 3
 * 4 5 6
 * 7 8 -
 * </pre>
 */
public class ClassicGame extends BaseGame {

    public ClassicGame(int width, int height, boolean randomMissingTile) {
        super(width, height, randomMissingTile);
    }

    public ClassicGame(int width, int height, List<Integer> savedGrid, int savedMoves) {
        super(width, height, savedGrid, savedMoves);
    }

    private ClassicGame(ClassicGame game) {
        super(game);
    }

    @Override
    protected List<Integer> generateSolved() {
        return Generator.generate(width, height, Constants.TYPE_CLASSIC);
    }

    @Override
    public int inversions() {
        int inversions = 0;
        int size = height * width;
        // for every number we need to count:
        // - numbers less than chosen
        // - follow chosen number (by rows)
        for (int i = 0; i < size; i++) {
            int n = grid.get(i);
            if (n <= 1) {
                continue;
            }
            for (int j = i + 1; j < size; j++) {
                int m = grid.get(j);
                if (m > 0 && n > m) {
                    inversions++;
                }
            }
        }
        return inversions;
    }

    @Override
    public boolean isSolvable() {
        int inversions = inversions();
        // for puzzles with even width
        // we need to add difference between row number where zero is located in the grid and
        // zero's row number in the solved position
        if (width % 2 == 0) {
            int targetRowIndex;
            if (missingTile == width * height) {
                // for classic variations it will always be the last row
                targetRowIndex = height - 1;
            } else {
                targetRowIndex = solvedGrid.indexOf(0) / width;
            }
            int zeroRowIndex = grid.indexOf(0) / width;
            // since we're only interested in parity, we can add indices instead of subtraction
            return (inversions % 2) == (targetRowIndex + zeroRowIndex) % 2;
        }
        return inversions % 2 == 0;
    }

    @Override
    public Game copy() {
        return new ClassicGame(this);
    }
}
