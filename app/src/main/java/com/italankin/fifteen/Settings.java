package com.italankin.fifteen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;

import java.text.DateFormat;

public class Settings {

    private static final String KEY_GAME_WIDTH = "puzzle_width";
    private static final String KEY_GAME_HEIGHT = "puzzle_height";
    private static final String KEY_GAME_TILE_COLOR = "tile_color";
    private static final String KEY_GAME_BG_COLOR = "bg_color";
    private static final String KEY_GAME_TYPE = "mode";
    private static final String KEY_GAME_MODE = "hardmode";
    private static final String KEY_GAME_ANTI_ALIAS = "antialias";
    private static final String KEY_ANIMATION_SPEED = "animation_speed";
    private static final String KEY_MULTI_COLOR = "multi_color";
    private static final String KEY_NEW_GAME_DELAY = "new_game_delay";
    private static final String KEY_INGAME_INFO = "ingame_info";
    private static final String KEY_INGAME_INFO_MOVES = "ingame_info_moves";
    private static final String KEY_INGAME_INFO_TIME = "ingame_info_time";
    private static final String KEY_INGAME_INFO_TPS = "ingame_info_tps";
    private static final String KEY_TIME_FORMAT = "time_format";
    private static final String KEY_STATS = "stats";
    private static final String KEY_MISSING_RANDOM_TILE = "missing_random_tile";
    private static final String KEY_CONFIRM_NEW_GAME = "confirm_new_game";

    static final String KEY_SAVED_GAME_ARRAY = "puzzle_prev";
    static final String KEY_SAVED_GAME_MOVES = "puzzle_prev_moves";
    static final String KEY_SAVED_GAME_TIME = "puzzle_prev_time";

    public static int gameWidth = Defaults.GAME_WIDTH;
    public static int gameHeight = Defaults.GAME_HEIGHT;
    public static boolean hardmode = Defaults.HARD_MODE;
    public static boolean antiAlias = Defaults.ANTI_ALIAS;
    public static boolean randomMissingTile = Defaults.RANDOM_MISSING_TILE;
    public static long animationSpeed = Defaults.ANIMATION_SPEED;
    public static int tileColor = 0;
    public static int multiColor = Defaults.MULTI_COLOR;
    public static int colorMode = Defaults.COLOR_MODE;
    public static int gameType = Defaults.GAME_TYPE;
    public static Typeface typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
    public static boolean newGameDelay = Defaults.NEW_GAME_DELAY;
    public static int ingameInfoMoves = Defaults.INGAME_INFO_MOVES;
    public static int ingameInfoTime = Defaults.INGAME_INFO_TIME;
    public static int ingameInfoTps = Defaults.INGAME_INFO_TPS;
    public static int timeFormat = Defaults.TIME_FORMAT;
    public static boolean stats = Defaults.STATS;
    public static boolean confirmNewGame = Defaults.CONFIRM_NEW_GAME;

    /**
     * Used for UI tests to limit event loop queue
     */
    public static long postInvalidateDelay = 0;

    static SharedPreferences prefs;
    public static DateFormat dateFormat;

    private static int uiMode;

    static SharedPreferences getPreferences(Context context) {
        String preferencesName = context.getPackageName() + "_preferences";
        return context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
    }

    static void load(Context context) {
        int w = prefs.getInt(KEY_GAME_WIDTH, Defaults.GAME_WIDTH);
        if (w >= Constants.MIN_GAME_WIDTH && w <= Constants.MAX_GAME_WIDTH) {
            gameWidth = w;
        }
        int h = prefs.getInt(KEY_GAME_HEIGHT, Defaults.GAME_HEIGHT);
        if (h >= Constants.MIN_GAME_HEIGHT && h <= Constants.MAX_GAME_HEIGHT) {
            gameHeight = h;
        }
        tileColor = prefs.getInt(KEY_GAME_TILE_COLOR, Defaults.TILE_COLOR);
        colorMode = prefs.getInt(KEY_GAME_BG_COLOR, Defaults.COLOR_MODE);
        gameType = prefs.getInt(KEY_GAME_TYPE, Defaults.GAME_TYPE);
        antiAlias = prefs.getBoolean(KEY_GAME_ANTI_ALIAS, Defaults.ANTI_ALIAS);
        hardmode = prefs.getBoolean(KEY_GAME_MODE, Defaults.HARD_MODE);
        dateFormat = android.text.format.DateFormat.getDateFormat(context);
        multiColor = prefs.getInt(KEY_MULTI_COLOR, Defaults.MULTI_COLOR);
        newGameDelay = prefs.getBoolean(KEY_NEW_GAME_DELAY, Defaults.NEW_GAME_DELAY);
        timeFormat = prefs.getInt(KEY_TIME_FORMAT, Defaults.TIME_FORMAT);
        stats = prefs.getBoolean(KEY_STATS, Defaults.STATS);
        randomMissingTile = prefs.getBoolean(KEY_MISSING_RANDOM_TILE, Defaults.RANDOM_MISSING_TILE);
        confirmNewGame = prefs.getBoolean(KEY_CONFIRM_NEW_GAME, Defaults.CONFIRM_NEW_GAME);

        if (prefs.contains("ingame_info")) {
            // old logic for backward compatibility
            int ingameInfo = prefs.getInt(KEY_INGAME_INFO, 0x1 | 0x2);
            ingameInfoMoves = (ingameInfo & 0x1) == 0
                    ? Constants.INGAME_INFO_AFTER_SOLVE
                    : Constants.INGAME_INFO_ON;
            ingameInfoTime = (ingameInfo & 0x2) == 0
                    ? Constants.INGAME_INFO_AFTER_SOLVE
                    : Constants.INGAME_INFO_ON;
            prefs.edit().remove("ingame_info").apply();
            Logger.d("old ingameInfo=%d, ingameInfoMoves=%d, ingameInfoTime=%d",
                    ingameInfo, ingameInfoMoves, ingameInfoTime);
        } else {
            ingameInfoMoves = prefs.getInt(KEY_INGAME_INFO_MOVES, Defaults.INGAME_INFO_MOVES);
            ingameInfoTime = prefs.getInt(KEY_INGAME_INFO_TIME, Defaults.INGAME_INFO_TIME);
            ingameInfoTps = prefs.getInt(KEY_INGAME_INFO_TPS, Defaults.INGAME_INFO_TPS);
        }

        if (prefs.contains("animation")) {
            boolean oldAnimationsEnabled = prefs.getBoolean("animation", true);
            animationSpeed = oldAnimationsEnabled
                    ? prefs.getLong("tile_animation_duration", Defaults.ANIMATION_SPEED)
                    : Defaults.ANIMATION_SPEED;
            prefs.edit()
                    .putLong(KEY_ANIMATION_SPEED, animationSpeed)
                    .remove("animation")
                    .remove("tile_animation_duration")
                    .apply();
        } else {
            animationSpeed = prefs.getLong(KEY_ANIMATION_SPEED, Defaults.ANIMATION_SPEED);
        }
    }

    static void updateUiMode(Context context) {
        uiMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
    }

    public static void save() {
        save(false);
    }

    @SuppressLint("ApplySharedPref")
    static void save(boolean sync) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_GAME_WIDTH, gameWidth);
        editor.putInt(KEY_GAME_HEIGHT, gameHeight);
        editor.putInt(KEY_GAME_TILE_COLOR, tileColor);
        editor.putInt(KEY_GAME_BG_COLOR, colorMode);
        editor.putInt(KEY_GAME_TYPE, gameType);
        editor.putBoolean(KEY_GAME_ANTI_ALIAS, antiAlias);
        editor.putLong(KEY_ANIMATION_SPEED, animationSpeed);
        editor.putBoolean(KEY_GAME_MODE, hardmode);
        editor.putInt(KEY_MULTI_COLOR, multiColor);
        editor.putBoolean(KEY_NEW_GAME_DELAY, newGameDelay);
        editor.putInt(KEY_INGAME_INFO_MOVES, ingameInfoMoves);
        editor.putInt(KEY_INGAME_INFO_TIME, ingameInfoTime);
        editor.putInt(KEY_INGAME_INFO_TPS, ingameInfoTps);
        editor.putInt(KEY_TIME_FORMAT, timeFormat);
        editor.putBoolean(KEY_STATS, stats);
        editor.putBoolean(KEY_MISSING_RANDOM_TILE, randomMissingTile);
        editor.putBoolean(KEY_CONFIRM_NEW_GAME, confirmNewGame);
        SaveGameManager.saveGame(editor);
        if (sync) {
            editor.commit();
        } else {
            editor.apply();
        }
    }

    static int getColorMode() {
        if (colorMode != Constants.COLOR_MODE_SYSTEM) {
            return colorMode;
        }
        if (uiMode == Configuration.UI_MODE_NIGHT_YES) {
            return Constants.COLOR_MODE_NIGHT;
        } else {
            return Constants.COLOR_MODE_DAY;
        }
    }

    static boolean useMultiColor() {
        return multiColor != Constants.MULTI_COLOR_OFF && !hardmode;
    }

    public static boolean animationsEnabled() {
        return animationSpeed != Constants.ANIMATION_SPEED_OFF;
    }
}
