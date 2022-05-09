package com.italankin.fifteen.game;

import com.italankin.fifteen.Constants;
import com.italankin.fifteen.Generator;

import java.util.List;

/**
 * Snake-like pattern:
 * <pre>
 * 1 2 3
 * 6 5 4
 * 7 8 -
 * </pre>
 */
public class SnakeGame extends BaseGame {

    public SnakeGame(int width, int height, boolean hardmode, boolean randomMissingTile) {
        super(width, height, hardmode, randomMissingTile);
    }

    public SnakeGame(int width, int height, boolean hardmode, List<Integer> savedGrid, int savedMoves, long savedTime) {
        super(width, height, hardmode, savedGrid, savedMoves, savedTime);
    }

    private SnakeGame(SnakeGame game) {
        super(game);
    }

    @Override
    protected List<Integer> generateSolved() {
        return Generator.generate(width, height, Constants.TYPE_SNAKE);
    }

    @Override
    public int inversions() {
        int inversions = 0;
        int size = height * width;
        for (int i = 0; i < size; i++) {
            int n;
            if ((i / width) % 2 == 0) {
                n = grid.get(i);
            } else {
                n = grid.get(width * (1 + i / width) - i % width - 1);
            }
            if (n == 0 || n == 1) {
                continue;
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
        return inversions;
    }

    @Override
    public boolean isSolvable() {
        return inversions() % 2 == 0;
    }

    @Override
    public Game copy() {
        return new SnakeGame(this);
    }
}
