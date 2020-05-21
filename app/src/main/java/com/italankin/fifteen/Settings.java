package com.italankin.fifteen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import java.text.DateFormat;

public class Settings {

    private static final String KEY_GAME_WIDTH = "puzzle_width";
    private static final String KEY_GAME_HEIGHT = "puzzle_height";
    static final String KEY_GAME_ARRAY = "puzzle_prev";
    static final String KEY_GAME_MOVES = "puzzle_prev_moves";
    static final String KEY_GAME_TIME = "puzzle_prev_time";
    private static final String KEY_GAME_SAVE = "savegame";
    private static final String KEY_GAME_TILE_COLOR = "tile_color";
    private static final String KEY_GAME_BG_COLOR = "bg_color";
    private static final String KEY_GAME_MODE = "mode";
    private static final String KEY_GAME_BF = "blind";
    private static final String KEY_GAME_ANTI_ALIAS = "antialias";
    private static final String KEY_GAME_ANIMATION = "animation";
    private static final String KEY_MULTI_COLOR = "multi_color";
    private static final String KEY_NEW_GAME_DELAY = "new_game_delay";
    private static final String KEY_INGAME_INFO = "ingame_info";
    private static final String KEY_TIME_FORMAT = "time_format";
    private static final String KEY_STATS = "stats";

    public static int gameWidth = Defaults.GAME_WIDTH;
    public static int gameHeight = Defaults.GAME_HEIGHT;
    public static boolean hardmode = Defaults.HARD_MODE;
    static boolean saveGame = Defaults.HARD_MODE;
    public static boolean animations = Defaults.ANIMATIONS;
    public static boolean antiAlias = Defaults.ANTI_ALIAS;
    static long tileAnimDuration = Defaults.ANIMATION_DURATION;
    public static long screenAnimDuration = Defaults.ANIMATION_DURATION;
    public static int tileColor = 0;
    public static int multiColor = Defaults.MULTI_COLOR;
    public static int colorMode = Defaults.COLOR_MODE;
    public static int gameMode = Defaults.GAME_MODE;
    public static Typeface typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
    public static boolean newGameDelay = Defaults.NEW_GAME_DELAY;
    public static int ingameInfo = Defaults.INGAME_INFO;
    public static int timeFormat = Defaults.TIME_FORMAT;
    public static boolean stats = Defaults.STATS;

    static SharedPreferences prefs;
    public static DateFormat dateFormat;

    static SharedPreferences getPreferences(Context context) {
        String preferencesName = context.getPackageName() + "_preferences";
        return context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
    }

    static void load(Context context) {
        int w = prefs.getInt(KEY_GAME_WIDTH, Defaults.GAME_WIDTH);
        if (w >= Constants.MIN_GAME_WIDTH && w < Constants.MAX_GAME_WIDTH) {
            gameWidth = w;
        }
        int h = prefs.getInt(KEY_GAME_HEIGHT, Defaults.GAME_HEIGHT);
        if (h >= Constants.MIN_GAME_HEIGHT && h < Constants.MAX_GAME_HEIGHT) {
            gameHeight = h;
        }
        saveGame = prefs.getBoolean(KEY_GAME_SAVE, Defaults.SAVE_GAME);
        tileColor = prefs.getInt(KEY_GAME_TILE_COLOR, Defaults.TILE_COLOR);
        colorMode = prefs.getInt(KEY_GAME_BG_COLOR, Defaults.COLOR_MODE);
        gameMode = prefs.getInt(KEY_GAME_MODE, Defaults.GAME_MODE);
        antiAlias = prefs.getBoolean(KEY_GAME_ANTI_ALIAS, Defaults.ANTI_ALIAS);
        animations = prefs.getBoolean(KEY_GAME_ANIMATION, Defaults.ANIMATIONS);
        hardmode = prefs.getBoolean(KEY_GAME_BF, Defaults.HARD_MODE);
        dateFormat = android.text.format.DateFormat.getDateFormat(context);
        multiColor = prefs.getInt(KEY_MULTI_COLOR, Defaults.MULTI_COLOR);
        newGameDelay = prefs.getBoolean(KEY_NEW_GAME_DELAY, Defaults.NEW_GAME_DELAY);
        ingameInfo = prefs.getInt(KEY_INGAME_INFO, Defaults.INGAME_INFO);
        timeFormat = prefs.getInt(KEY_TIME_FORMAT, Defaults.TIME_FORMAT);
        stats = prefs.getBoolean(KEY_STATS, Defaults.STATS);
    }

    public static void save() {
        save(false);
    }

    @SuppressLint("ApplySharedPref")
    static void save(boolean sync) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_GAME_WIDTH, gameWidth);
        editor.putInt(KEY_GAME_HEIGHT, gameHeight);
        if (!Game.isSolved() && saveGame) {
            String string_array = Game.getGridStr();
            editor.putString(KEY_GAME_ARRAY, string_array);
            editor.putInt(KEY_GAME_MOVES, Game.getMoves());
            editor.putLong(KEY_GAME_TIME, Game.getTime());
        } else {
            editor.remove(KEY_GAME_ARRAY);
            editor.remove(KEY_GAME_MOVES);
            editor.remove(KEY_GAME_TIME);
        }
        editor.putBoolean(KEY_GAME_SAVE, saveGame);
        editor.putInt(KEY_GAME_TILE_COLOR, tileColor);
        editor.putInt(KEY_GAME_BG_COLOR, colorMode);
        editor.putInt(KEY_GAME_MODE, gameMode);
        editor.putBoolean(KEY_GAME_ANTI_ALIAS, antiAlias);
        editor.putBoolean(KEY_GAME_ANIMATION, animations);
        editor.putBoolean(KEY_GAME_BF, hardmode);
        editor.putInt(KEY_MULTI_COLOR, multiColor);
        editor.putBoolean(KEY_NEW_GAME_DELAY, newGameDelay);
        editor.putInt(KEY_INGAME_INFO, ingameInfo);
        editor.putInt(KEY_TIME_FORMAT, timeFormat);
        editor.putBoolean(KEY_STATS, stats);
        if (sync) {
            editor.commit();
        } else {
            editor.apply();
        }
    }

    static boolean useMultiColor() {
        return multiColor != Constants.MULTI_COLOR_OFF && !hardmode;
    }
}
