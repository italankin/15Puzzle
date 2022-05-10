package com.italankin.fifteen;

import android.content.SharedPreferences;

import java.util.Collections;
import java.util.List;

class SaveGameManager {

    static SavedGame getSavedGame() {
        String strings = Settings.prefs.getString(Settings.KEY_SAVED_GAME_ARRAY, null);
        if (strings != null) {
            List<Integer> state = Tools.getIntegerArray(strings.split("\\s*,\\s*"));
            int moves = Settings.prefs.getInt(Settings.KEY_SAVED_GAME_MOVES, 0);
            long time = Settings.prefs.getLong(Settings.KEY_SAVED_GAME_TIME, 0);
            return new SavedGame(state, moves, time);
        } else {
            return SavedGame.INVALID;
        }
    }

    static boolean hasSavedGame() {
        return Settings.prefs.contains(Settings.KEY_SAVED_GAME_ARRAY);
    }

    static void removeSavedGame() {
        if (!Settings.prefs.contains(Settings.KEY_SAVED_GAME_ARRAY)) {
            return;
        }
        Settings.prefs.edit()
                .remove(Settings.KEY_SAVED_GAME_ARRAY)
                .remove(Settings.KEY_SAVED_GAME_MOVES)
                .remove(Settings.KEY_SAVED_GAME_TIME)
                .apply();
    }

    static void saveGame(SharedPreferences.Editor editor) {
        GameState state = GameState.get();
        if (!state.isSolved()) {
            String stateAsStr = state.game.getState().toString();
            String array = stateAsStr.substring(1, stateAsStr.length() - 1);
            editor.putString(Settings.KEY_SAVED_GAME_ARRAY, array);
            editor.putInt(Settings.KEY_SAVED_GAME_MOVES, state.getMoves());
            editor.putLong(Settings.KEY_SAVED_GAME_TIME, state.time);
        } else {
            editor.remove(Settings.KEY_SAVED_GAME_ARRAY);
            editor.remove(Settings.KEY_SAVED_GAME_MOVES);
            editor.remove(Settings.KEY_SAVED_GAME_TIME);
        }
    }

    static class SavedGame {

        static final SavedGame INVALID = new SavedGame(Collections.emptyList(), 0, 0);

        final List<Integer> state;
        final int moves;
        final long time;

        SavedGame(List<Integer> state, int moves, long time) {
            this.state = state;
            this.moves = moves;
            this.time = time;
        }
    }
}
