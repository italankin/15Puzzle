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
                int[] goalCoords = Util.coords(i, game.getWidth());
                int sx = goalCoords[0];
                int sy = goalCoords[1];
                int[] stateCoords = Util.coords(state.indexOf(goal.get(i)), game.getWidth());
                int gx = stateCoords[0];
                int gy = stateCoords[1];
                distance += Tools.manhattan(sx, sy, gx, gy);
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
