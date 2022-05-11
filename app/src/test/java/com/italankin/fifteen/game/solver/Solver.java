package com.italankin.fifteen.game.solver;

import com.italankin.fifteen.game.Game;
import com.italankin.fifteen.game.Heuristics;
import com.italankin.fifteen.game.Move;

public class Solver {

    private final Move start;
    private final Algorithm algorithm;

    public Solver(Game start, Heuristics heuristics, Algorithm algorithm) {
        this.start = new Move(heuristics, start);
        this.algorithm = algorithm;
    }

    public Move start() {
        return start;
    }

    public Solution solve() {
        return algorithm.run(start);
    }
}
