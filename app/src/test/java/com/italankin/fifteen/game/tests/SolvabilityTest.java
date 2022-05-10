package com.italankin.fifteen.game.tests;

import com.italankin.fifteen.game.ClassicGame;
import com.italankin.fifteen.game.Game;
import com.italankin.fifteen.game.Heuristics;
import com.italankin.fifteen.game.Move;
import com.italankin.fifteen.game.RandomGoalGame;
import com.italankin.fifteen.game.SnakeGame;
import com.italankin.fifteen.game.Solver;
import com.italankin.fifteen.game.SpiralGame;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@RunWith(Parameterized.class)
public class SolvabilityTest {

    private static final long TIMEOUT = 60 * 1000;
    private static final int RUNS = 100;

    private static final List<GameType> GAME_TYPES = Arrays.asList(GameType.CLASSIC, GameType.SNAKE, GameType.SPIRAL);

    private static final int WIDTH_MIN = 3;
    private static final int WIDTH_MAX = 4;

    private static final int HEIGHT_MIN = 3;
    private static final int HEIGHT_MAX = 4;

    private static final Heuristics HEURISTICS = Heuristics.MANHATTAN_DISTANCE;
    private static final Comparator<Move> MOVE_COMPARATOR = new Move.HeuristicsMovesCmp(4);

    @Parameters(name = "#{0} {1} {2}x{3} randomMissingTile={4}")
    public static Collection<Object[]> parameters() {
        List<Object[]> params = new ArrayList<>();
        for (GameType gameType : GAME_TYPES) {
            for (int w = WIDTH_MIN; w <= WIDTH_MAX; w++) {
                for (int h = HEIGHT_MIN; h <= HEIGHT_MAX; h++) {
                    for (int r = 0; r < RUNS; r++) {
                        params.add(new Object[]{r, gameType, w, h, false});
                        params.add(new Object[]{r, gameType, w, h, true});
                    }
                }
            }
        }
        return params;
    }

    private final GameType gameType;
    private final int width;
    private final int height;
    private final boolean randomMissingTile;

    public SolvabilityTest(@SuppressWarnings("unused") int run,
            GameType gameType,
            int width,
            int height,
            boolean randomMissingTile) {
        this.gameType = gameType;
        this.width = width;
        this.height = height;
        this.randomMissingTile = randomMissingTile;
    }

    @Test(timeout = TIMEOUT)
    public void checkSolvability() {
        Game game = createGame();
        Solver solver = new Solver(game, HEURISTICS, MOVE_COMPARATOR);

        System.out.println("Starting position:");
        System.out.println(solver.start());

        long startTime = System.nanoTime();
        Solver.Solution solution = solver.solve();
        long time = System.nanoTime() - startTime;

        System.out.printf("Solved %s %dx%d in %.3fms, %d moves, %d nodes explored\n",
                gameType, width, height, time / 1_000_000f, solution.moves(), solution.explored);
    }

    private Game createGame() {
        switch (gameType) {
            case CLASSIC:
                return new ClassicGame(width, height, randomMissingTile);
            case SNAKE:
                return new SnakeGame(width, height, randomMissingTile);
            case SPIRAL:
                return new SpiralGame(width, height, randomMissingTile);
            case RANDOM:
                return new RandomGoalGame(width, height, randomMissingTile);
        }
        throw new IllegalArgumentException("Unsupported type: " + gameType);
    }

    private enum GameType {
        CLASSIC,
        SNAKE,
        SPIRAL,
        RANDOM;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
}
