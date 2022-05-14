package com.italankin.fifteen.game.solver;

import com.italankin.fifteen.game.Move;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public final List<Move> moves;
    public final int explored;

    public Solution(List<Move> moves, int explored) {
        this.moves = moves;
        this.explored = explored;
    }

    public Move end() {
        return moves.get(moves.size() - 1);
    }

    public List<Integer> movesNumbers() {
        List<Integer> result = new ArrayList<>(moves.size());
        for (int i = 1, movesSize = moves.size(); i < movesSize; i++) {
            Move move = moves.get(i);
            int index = move.parent.zero[1] * move.state.getWidth() + move.parent.zero[0];
            result.add(move.state.getState().get(index));
        }
        return result;
    }

    public int moves() {
        return moves.size() - 1;
    }
}
