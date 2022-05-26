package com.italankin.fifteen.game;

import com.italankin.fifteen.Tools;

import java.util.List;

public enum Heuristics {
    INVERSIONS {
        @Override
        int calc(Game game) {
            return game.inversions();
        }
    },
    MANHATTAN_DISTANCE {
        @Override
        public int calc(Game game) {
            List<Integer> goal = game.getGoal();
            List<Integer> state = game.getState();
            int distance = 0;
            for (int i = 0, s = goal.size(); i < s; i++) {
                int width = game.getWidth();
                int index = state.indexOf(goal.get(i));
                distance += Tools.manhattan(i % width, i / width, index % width, index / width);
            }
            return distance;
        }
    },
    MISPLACED_TILES {
        @Override
        int calc(Game game) {
            int outOfPlace = 0;
            List<Integer> state = game.getState();
            List<Integer> goal = game.getGoal();
            for (int i = 0; i < goal.size(); i++) {
                int actual = state.get(i);
                int expected = goal.get(i);
                if (actual != expected) {
                    outOfPlace++;
                }
            }
            return outOfPlace;
        }
    };

    abstract int calc(Game game);
}
