package com.italankin.fifteen.game;

import java.util.ArrayList;
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

    public SnakeGame(int width, int height, boolean randomMissingTile) {
        super(width, height, generateGoal(width, height), randomMissingTile);
    }

    public SnakeGame(int width, int height, List<Integer> state, int moves) {
        super(width, height, state, generateGoal(width, height), moves);
    }

    public SnakeGame(int width, int height, List<Integer> state) {
        super(width, height, state, generateGoal(width, height));
    }

    private SnakeGame(SnakeGame game) {
        super(game);
    }

    @Override
    public Game copy() {
        return new SnakeGame(this);
    }

    private static List<Integer> generateGoal(int width, int height) {
        int size = width * height;
        List<Integer> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            int row = i / width;
            int n;
            if (row % 2 == 0) {
                n = i + 1;
            } else {
                n = (width * (1 + i / width) - i % width);
            }
            result.add(n % size);
        }
        return result;
    }
}
