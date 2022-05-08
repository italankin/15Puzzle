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
            List<Integer> solvedGrid = game.getSolvedGrid();
            List<Integer> grid = game.getGrid();
            int distance = 0;
            for (int i = 0, s = solvedGrid.size(); i < s; i++) {
                int[] solvedCoords = Util.coords(i, game.getWidth());
                int sx = solvedCoords[0];
                int sy = solvedCoords[1];
                int[] gridCoords = Util.coords(grid.indexOf(solvedGrid.get(i)), game.getWidth());
                int gx = gridCoords[0];
                int gy = gridCoords[1];
                distance += Tools.manhattan(sx, sy, gx, gy);
            }
            return distance;
        }
    },
    MISPLACED_TILES {
        @Override
        int calc(Game game) {
            int outOfPlace = 0;
            List<Integer> grid = game.getGrid();
            List<Integer> solvedGrid = game.getSolvedGrid();
            for (int i = 0; i < solvedGrid.size(); i++) {
                int actual = grid.get(i);
                int expected = solvedGrid.get(i);
                if (actual != expected) {
                    outOfPlace++;
                }
            }
            return outOfPlace;
        }
    };

    abstract int calc(Game game);
}
