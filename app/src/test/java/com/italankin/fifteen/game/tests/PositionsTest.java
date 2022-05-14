package com.italankin.fifteen.game.tests;

import com.italankin.fifteen.game.ClassicGame;
import com.italankin.fifteen.game.Game;
import com.italankin.fifteen.game.Heuristics;
import com.italankin.fifteen.game.Move;
import com.italankin.fifteen.game.solver.Algorithm;
import com.italankin.fifteen.game.solver.Solution;
import com.italankin.fifteen.game.solver.Solver;
import com.italankin.fifteen.game.solver.impl.AStar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class PositionsTest {

    private static final long TIMEOUT = 60 * 1000;

    private static final Heuristics HEURISTICS = Heuristics.MANHATTAN_DISTANCE;
    private static final Algorithm ALGORITHM = new AStar(new Move.HeuristicsMovesCmp(2));

    private static final Game[] GAMES = new Game[]{
            new ClassicGame(4, 4, Arrays.asList(
                    0, 12, 9, 13,
                    15, 11, 10, 14,
                    3, 7, 2, 5,
                    4, 8, 6, 1
            )),
            new ClassicGame(4, 4, Arrays.asList(
                    0, 15, 9, 13,
                    11, 12, 10, 14,
                    3, 7, 6, 2,
                    4, 8, 5, 1
            )),
            new ClassicGame(4, 4, Arrays.asList(
                    0, 12, 9, 13,
                    15, 11, 10, 14,
                    7, 8, 6, 2,
                    4, 3, 5, 1
            )),
            new ClassicGame(4, 4, Arrays.asList(
                    1, 10, 15, 4,
                    13, 6, 3, 8,
                    2, 9, 12, 7,
                    14, 5, 0, 11
            ))
    };

    @Parameterized.Parameters
    public static Collection<Object[]> parameters() {
        List<Object[]> params = new ArrayList<>();
        for (Game game : GAMES) {
            params.add(new Object[]{game});
        }
        return params;
    }

    private final Game game;

    public PositionsTest(Game game) {
        this.game = game;
    }

    @Test(timeout = TIMEOUT)
    public void checkPosition() {
        Solver solver = new Solver(game, HEURISTICS, ALGORITHM);

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
