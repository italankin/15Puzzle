package com.italankin.fifteen.game.tests;

import com.italankin.fifteen.game.ClassicGame;
import com.italankin.fifteen.game.Game;
import com.italankin.fifteen.game.Heuristics;
import com.italankin.fifteen.game.solver.Solution;
import com.italankin.fifteen.game.solver.Solver;
import com.italankin.fifteen.game.solver.impl.IDAStar;

import org.junit.Test;

import java.util.Arrays;

public class IDAStarTest {

    private static final long TIMEOUT = 60 * 1000;

    @Test(timeout = TIMEOUT)
    public void checkSolution() {
        Game game = new ClassicGame(4, 4, Arrays.asList(
                6, 10, 3, 0,
                13, 8, 11, 4,
                14, 1, 2, 12,
                5, 9, 7, 15));
        Solver solver = new Solver(game, Heuristics.MANHATTAN_DISTANCE, new IDAStar());

        System.out.println("Starting position:");
        System.out.println(solver.start());

        long startTime = System.nanoTime();
        Solution solution = solver.solve();
        long time = System.nanoTime() - startTime;

        System.out.printf("Solved %dx%d in %.3fms, %d moves, %d nodes explored\nMoves: %s\n",
                game.getWidth(), game.getHeight(), time / 1_000_000f, solution.moves(),
                solution.explored, solution.movesNumbers());
    }
}
