package com.italankin.fifteen;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class SaveGameManager {

    static SavedGame getSavedGame() {
        String array = Settings.prefs.getString(Settings.KEY_SAVED_GAME_ARRAY, null);
        if (array != null) {
            List<Integer> state = stringToState(array);
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
            String array = stateToString(state);
            editor.putString(Settings.KEY_SAVED_GAME_ARRAY, array);
            editor.putInt(Settings.KEY_SAVED_GAME_MOVES, state.getMoves());
            editor.putLong(Settings.KEY_SAVED_GAME_TIME, state.time);
        } else {
            editor.remove(Settings.KEY_SAVED_GAME_ARRAY);
            editor.remove(Settings.KEY_SAVED_GAME_MOVES);
            editor.remove(Settings.KEY_SAVED_GAME_TIME);
        }
    }

    private static String stateToString(GameState state) {
        List<Integer> puzzleState = state.game.getState();
        int size = puzzleState.size();
        StringBuilder sb = new StringBuilder(size * 3);
        sb.append(puzzleState.get(0));
        for (int i = 1; i < size; i++) {
            sb.append(',');
            sb.append(puzzleState.get(i));
        }
        return sb.toString();
    }

    private static List<Integer> stringToState(String array) {
        ArrayList<Integer> result = new ArrayList<>();
        String[] strings = array.split(",");
        for (String s : strings) {
            try {
                result.add(Integer.parseInt(s.trim()));
            } catch (NumberFormatException e) {
                Logger.e(e, "getIntegerArray: %s is not a number", s);
                return Collections.emptyList();
            }
        }
        return result;
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
