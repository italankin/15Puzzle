package com.italankin.fifteen.game.solver;

import com.italankin.fifteen.game.Move;

public class Solution {
    public final Move value;
    public final int explored;

    public Solution(Move value, int explored) {
        this.value = value;
        this.explored = explored;
    }

    public int moves() {
        return value.state.getMoves();
    }
}
