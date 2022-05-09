package com.italankin.fifteen.game;

import com.italankin.fifteen.Constants;
import com.italankin.fifteen.Generator;

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
        super(width, height, randomMissingTile);
    }

    public SpiralGame(int width, int height, List<Integer> savedGrid, int savedMoves) {
        super(width, height, savedGrid, savedMoves);
    }

    private SpiralGame(SpiralGame game) {
        super(game);
    }

    @Override
    protected List<Integer> generateSolved() {
        return Generator.generate(width, height, Constants.TYPE_SPIRAL);
    }

    @Override
    public int inversions() {
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
        return inversions;
    }

    @Override
    public boolean isSolvable() {
        return inversions() % 2 == 0;
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

    @Override
    public Game copy() {
        return new SpiralGame(this);
    }
}
