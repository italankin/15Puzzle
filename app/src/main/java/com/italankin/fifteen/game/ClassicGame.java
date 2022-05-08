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

    public ClassicGame(int width, int height, boolean hardmode, boolean randomMissingTile) {
        super(width, height, hardmode, randomMissingTile);
    }

    public ClassicGame(int width, int height, boolean hardmode, List<Integer> savedGrid, int savedMoves, long savedTime) {
        super(width, height, hardmode, savedGrid, savedMoves, savedTime);
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
        int indexOfZero = -1;
        for (int i = 0; i < size; i++) {
            int n = grid.get(i);
            if (n == 0) {
                indexOfZero = i;
                continue;
            } else if (n == 1) {
                // there's no numbers less than 1
                continue;
            }
            for (int j = i + 1; j < size; j++) {
                int m = grid.get(j);
                if (m > 0 && n > m) {
                    inversions++;
                }
            }
        }
        // for puzzles with even width
        // we need to add difference between row number (counting from down) where zero is located in the grid and
        // zero's row number in the solved position
        if (width % 2 == 0) {
            int targetRowIndex;
            if (missingTile == size) {
                // for classic variations it will always be the last row
                targetRowIndex = height - 1;
            } else {
                targetRowIndex = solvedGrid.indexOf(0) / width;
            }
            int zeroRowIndex = indexOfZero / width;
            inversions += (targetRowIndex - zeroRowIndex);
        }
        return inversions;
    }

    @Override
    public Game copy() {
        return new ClassicGame(this);
    }
}
