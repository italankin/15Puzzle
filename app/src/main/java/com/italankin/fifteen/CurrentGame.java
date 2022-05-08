package com.italankin.fifteen;

import com.italankin.fifteen.game.Game;

public final class CurrentGame {

    private static Game current;

    public static Game get() {
        return current;
    }

    public static void set(Game game) {
        current = game;
    }

    private CurrentGame() {
    }
}
