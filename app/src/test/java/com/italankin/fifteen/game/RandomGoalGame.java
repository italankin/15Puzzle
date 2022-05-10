package com.italankin.fifteen.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomGoalGame extends BaseGame {

    public RandomGoalGame(int width, int height, boolean randomMissingTile) {
        super(width, height, generateGoal(width, height), randomMissingTile);
    }

    private RandomGoalGame(RandomGoalGame game) {
        super(game);
    }

    @Override
    public Game copy() {
        return new RandomGoalGame(this);
    }

    private static List<Integer> generateGoal(int width, int height) {
        int size = width * height;
        List<Integer> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(i);
        }
        Collections.shuffle(result, random);
        return result;
    }
}
