package com.italankin.fifteen.game.solver.impl;

import com.italankin.fifteen.game.Move;
import com.italankin.fifteen.game.solver.Algorithm;
import com.italankin.fifteen.game.solver.Solution;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicInteger;

public class IDAStar implements Algorithm {

    private static final int FOUND = -1;

    @Override
    public Solution run(Move start) {
        AtomicInteger visitedNodes = new AtomicInteger();
        int threshold = start.heuristicsValue;
        Deque<Move> path = new ArrayDeque<>(64);
        path.add(start);
        while (true) {
            int t = search(path, 0, threshold, visitedNodes);
            if (t == FOUND) {
                return new Solution(new ArrayList<>(path), visitedNodes.get());
            }
            threshold = t;
        }
    }

    private static int search(Deque<Move> path, int g, int threshold, AtomicInteger visitedNodes) {
        Move node = path.getLast();
        visitedNodes.incrementAndGet();
        int f = g + node.heuristicsValue;
        if (f > threshold) {
            return f;
        }
        if (node.isSolved()) {
            return FOUND;
        }
        int min = Integer.MAX_VALUE;
        for (Move move : node.possibleMoves()) {
            if (!path.contains(move)) {
                path.addLast(move);
                int t = search(path, g + 1, threshold, visitedNodes);
                if (t == FOUND) {
                    return FOUND;
                }
                if (min > t) {
                    min = t;
                }
                path.removeLast();
            }
        }
        return min;
    }
}
