package com.italankin.fifteen.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Move {

    public final Game state;
    public final int heuristicsValue;
    private final Heuristics heuristics;

    public Move(Heuristics heuristics, Game state) {
        this.heuristics = heuristics;
        this.state = state;
        this.heuristicsValue = heuristics.calc(state);
    }

    Move newMove(int x, int y) {
        Game newState = state.copy();
        newState.move(x, y);
        return new Move(heuristics, newState);
    }

    List<Move> possibleMoves() {
        int width = state.getWidth();
        int height = state.getHeight();

        int zeroIndex = state.getGrid().indexOf(0);
        int[] coords = Util.coords(zeroIndex, width);
        int x = coords[0], y = coords[1];

        List<Move> moves = new ArrayList<>(4);
        if (x > 0) {
            moves.add(newMove(x - 1, y));
        }
        if (x < width - 1) {
            moves.add(newMove(x + 1, y));
        }
        if (y > 0) {
            moves.add(newMove(x, y - 1));
        }
        if (y < height - 1) {
            moves.add(newMove(x, y + 1));
        }
        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Move) {
            Move another = (Move) o;
            // equal positions have equal heuristics
            // optimize to avoid comparing arrays
            if (heuristicsValue != another.heuristicsValue) {
                return false;
            }
            return state.getGrid().equals(another.state.getGrid());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return state.getGrid().hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("---------------\nh=");
        sb.append(heuristicsValue);
        sb.append(" m=");
        sb.append(state.getMoves());
        sb.append(" i=");
        sb.append(state.inversions());
        sb.append("\n---------------\n");
        List<Integer> grid = state.getGrid();
        for (int i = 0, s = grid.size(); i < s; i++) {
            int number = grid.get(i);
            if (number != 0) {
                sb.append(number);
            } else {
                sb.append('-');
            }
            if ((i + 1) % state.getWidth() == 0) {
                sb.append('\n');
            } else {
                sb.append('\t');
            }
        }
        sb.append("---------------");
        return sb.toString();
    }

    /**
     * Compare moves by {@link #heuristicsValue}
     */
    public static final class HeuristicsCmp implements Comparator<Move> {
        @Override
        public int compare(Move lhs, Move rhs) {
            return Integer.compare(lhs.heuristicsValue, rhs.heuristicsValue);
        }
    }

    /**
     * Compare moves by {@link #heuristicsValue}, also considering move count.
     * <br>
     * {@link #HeuristicsMovesCmp(int)} accepts {@code tolerance} parameter, which controls the "quality" of
     * solutions.
     * Given equal {@link Move#heuristicsValue}s, a {@link Move} with lower move count will have higher priority for
     * analysis.
     * <br>
     * Generally, low {@code tolerance} values will result in more efficient solutions at the cost of
     * high memory consumption and slower solve speed.
     */
    public static final class HeuristicsMovesCmp implements Comparator<Move> {
        private final int tolerance;

        public HeuristicsMovesCmp(int tolerance) {
            if (tolerance < 1) {
                throw new IllegalArgumentException("tolerance must be >= 1, got: " + tolerance);
            }
            this.tolerance = tolerance;
        }

        @Override
        public int compare(Move lhs, Move rhs) {
            return Integer.compare(
                    lhs.heuristicsValue + lhs.state.getMoves() / tolerance,
                    rhs.heuristicsValue + rhs.state.getMoves() / tolerance);
        }
    }
}
