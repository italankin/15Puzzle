package com.italankin.fifteen;

import com.italankin.fifteen.game.Game;

public final class GameState {

    public static GameState get() {
        return current;
    }

    public static GameState set(Game game, boolean hardmode) {
        return current = new GameState(game, hardmode);
    }

    private static GameState current;

    public final Game game;
    public final boolean hardmode;
    public boolean hardmodeSolved;
    public boolean paused;
    public boolean peeking;
    public boolean help;
    public long time;

    private GameState(Game game, boolean hardmode) {
        this.game = game;
        this.hardmode = hardmode;
        this.paused = game.getMoves() > 0;
    }

    public int getMoves() {
        return game.getMoves();
    }

    public boolean isNotStarted() {
        return game.getMoves() == 0;
    }

    public void invertPaused() {
        paused = !paused;
    }

    public boolean isSolved() {
        return (!hardmode || hardmodeSolved) && game.isSolved();
    }
}
