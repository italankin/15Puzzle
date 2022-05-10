package com.italankin.fifteen.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Clockwise inwards:
 * <pre>
 * 1 2 3
 * 8 - 4
 * 7 6 5
 * </pre>
 */
public class SpiralGame extends BaseGame {

    public SpiralGame(int width, int height, boolean randomMissingTile) {
        super(width, height, generateGoal(width, height), randomMissingTile);
    }

    public SpiralGame(int width, int height, List<Integer> state, int moves) {
        super(width, height, state, generateGoal(width, height), moves);
    }

    public SpiralGame(int width, int height, List<Integer> state) {
        super(width, height, state, generateGoal(width, height));
    }

    private SpiralGame(SpiralGame game) {
        super(game);
    }

    @Override
    public Game copy() {
        return new SpiralGame(this);
    }

    private static List<Integer> generateGoal(int width, int height) {
        int h = height, w = width, size = width * height;
        int number = 1;
        int r = 0, c = 0; // start row and column
        int[][] array = new int[h][w];
        while (r < h && c < w) {
            for (int i = c; i < w; i++) {
                array[r][i] = number++ % size;
            }
            r++;
            for (int i = r; i < h; i++) {
                array[i][w - 1] = number++ % size;
            }
            w--;
            if (r < h) {
                for (int i = w - 1; i >= c; i--) {
                    array[h - 1][i] = number++ % size;
                }
                h--;
            }
            if (c < w) {
                for (int i = h - 1; i >= r; i--) {
                    array[i][c] = number++ % size;
                }
                c++;
            }
        }
        List<Integer> result = new ArrayList<>(size);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                result.add(array[i][j]);
            }
        }
        return result;
    }
}
