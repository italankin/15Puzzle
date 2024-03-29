package com.italankin.fifteen.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Move {

    private static int[] zero(Game game) {
        int index = game.getState().indexOf(0);
        int width = game.getWidth();
        return new int[]{index % width, index / width};
    }

    public final Move parent;
    public final Game state;
    public final int heuristicsValue;
    public final int[] zero;
    private final Heuristics heuristics;

    public Move(Heuristics heuristics, Game state) {
        this(heuristics, null, state, zero(state));
    }

    public Move(Heuristics heuristics, Move parent, Game state, int[] zero) {
        this.heuristics = heuristics;
        this.parent = parent;
        this.state = state;
        this.zero = zero;
        this.heuristicsValue = heuristics.calc(state);
    }

    public List<Move> possibleMoves() {
        int width = state.getWidth();
        int height = state.getHeight();

        int zeroIndex = state.getState().indexOf(0);
        int x = zeroIndex % width, y = zeroIndex / width;

        List<Move> moves = new ArrayList<>(4);
        if (x > 0) {
            int newX = x - 1;
            if (include(newX, y)) {
                moves.add(newMove(newX, y));
            }
        }
        if (x < width - 1) {
            int newX = x + 1;
            if (include(newX, y)) {
                moves.add(newMove(newX, y));
            }
        }
        if (y > 0) {
            int newY = y - 1;
            if (include(x, newY)) {
                moves.add(newMove(x, newY));
            }
        }
        if (y < height - 1) {
            int newY = y + 1;
            if (include(x, newY)) {
                moves.add(newMove(x, newY));
            }
        }
        return moves;
    }

    public boolean isSolved() {
        return state.isSolved();
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
            return state.getState().equals(another.state.getState());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return state.getState().hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        int cellWidth = String.valueOf(state.getSize()).length() + 2;
        char delimiter = ' ';

        int width = state.getWidth();
        int lineLength = cellWidth * width;
        StringBuilder line = new StringBuilder(lineLength);
        for (int i = 0; i < lineLength; i++) {
            line.append('-');
        }

        sb.append(line);
        sb.append("\nh=");
        sb.append(heuristicsValue);
        sb.append(" m=");
        sb.append(state.getMoves());
        sb.append(" i=");
        sb.append(state.inversions());
        sb.append('\n');
        sb.append(line);
        sb.append('\n');
        List<Integer> puzzleState = state.getState();
        for (int i = 0, s = puzzleState.size(); i < s; i++) {
            int number = puzzleState.get(i);
            appendNum(sb, number, cellWidth, delimiter);
            if ((i + 1) % width == 0) {
                sb.append('\n');
            }
        }
        sb.append(line);
        return sb.toString();
    }

    private boolean include(int x, int y) {
        // only include moves that does not lead to the parent position
        return parent == null || parent.zero[0] != x || parent.zero[1] != y;
    }

    private Move newMove(int x, int y) {
        Game newState = state.copy();
        newState.move(x, y);
        return new Move(heuristics, this, newState, new int[]{x, y});
    }

    private static void appendNum(StringBuilder sb, int num, int cellWidth, char delimiter) {
        String s = String.valueOf(num);
        for (int i = 0, c = cellWidth - s.length(); i < c; i++) {
            sb.append(delimiter);
        }
        sb.append(num == 0 ? "-" : s);
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
