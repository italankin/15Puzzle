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
        super(width, height, generateGoal(width, height), randomMissingTile);
    }

    public ClassicGame(int width, int height, List<Integer> state, int moves) {
        super(width, height, state, generateGoal(width, height), moves);
    }

    public ClassicGame(int width, int height, List<Integer> state) {
        super(width, height, state, generateGoal(width, height));
    }

    private ClassicGame(ClassicGame game) {
        super(game);
    }

    @Override
    public Game copy() {
        return new ClassicGame(this);
    }

    private static List<Integer> generateGoal(int width, int height) {
        int size = width * height;
        List<Integer> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add((i + 1) % size);
        }
        return result;
    }
}
