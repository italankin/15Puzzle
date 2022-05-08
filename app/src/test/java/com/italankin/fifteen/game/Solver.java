package com.italankin.fifteen.game;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;

public class Solver {

    private final Move start;
    private final Comparator<Move> moveComparator;

    public Solver(Game start, Heuristics heuristics, Comparator<Move> moveComparator) {
        this.start = new Move(heuristics, start);
        this.moveComparator = moveComparator;
    }

    public Move start() {
        return start;
    }

    public Solution solve() {
        // use hashCode values to lower memory consumption
        HashSet<Integer> explored = new HashSet<>();
        int initialCapacity = 1 << (start.state.getWidth() * start.state.getHeight() - 2);
        PriorityQueue<Move> moveQueue = new PriorityQueue<>(initialCapacity, moveComparator);
        Move best = null;

        moveQueue.add(start);
        while (!moveQueue.isEmpty()) {
            Move move = moveQueue.remove();
            explored.add(move.hashCode());
            if (move.state.isSolved()) {
                return new Solution(move, explored.size());
            }
            if (best == null || move.heuristicsValue <= best.heuristicsValue) {
                best = move;
            }
            for (Move possibleMove : move.possibleMoves()) {
                if (!explored.contains(possibleMove.hashCode())) {
                    moveQueue.add(possibleMove);
                }
            }
        }
        throw new IllegalStateException("No solution! " +
                explored.size() + " nodes explored.\nBest solution found:\n" + best);
    }

    public static class Solution {
        public final Move value;
        public final int explored;

        Solution(Move value, int explored) {
            this.value = value;
            this.explored = explored;
        }

        public int moves() {
            return value.state.getMoves();
        }
    }
}
