package com.italankin.fifteen.game.solver.impl;

import com.italankin.fifteen.game.Move;
import com.italankin.fifteen.game.solver.Algorithm;
import com.italankin.fifteen.game.solver.Solution;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;

public class AStar implements Algorithm {

    private final Comparator<Move> moveComparator;

    public AStar(Comparator<Move> moveComparator) {
        this.moveComparator = moveComparator;
    }

    @Override
    public Solution run(Move start) {
        // use hashCode values to lower memory consumption
        HashSet<Integer> explored = new HashSet<>();
        int initialCapacity = 1 << (start.state.getWidth() * start.state.getHeight() - 2);
        PriorityQueue<Move> moveQueue = new PriorityQueue<>(initialCapacity, moveComparator);
        Move best = null;

        moveQueue.add(start);
        while (!moveQueue.isEmpty()) {
            Move move = moveQueue.remove();
            explored.add(move.hashCode());
            if (move.isSolved()) {
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
}
