package com.italankin.fifteen.game;

import java.util.ArrayList;
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
        int size = width * height;
        List<Integer> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add((i + 1) % size);
        }
        return result;
    }

    @Override
    public Game copy() {
        return new ClassicGame(this);
    }
}
