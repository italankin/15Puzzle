package com.italankin.fifteen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import java.text.DateFormat;

public class Settings {

    public static final int MIN_GAME_WIDTH = 3;
    public static final int MIN_GAME_HEIGHT = 3;
    public static final int MAX_GAME_WIDTH = 8;
    public static final int MAX_GAME_HEIGHT = 8;
    public static final int COLOR_MODES = 2;
    public static final int GAME_MODES = 2;
    public static final int MULTI_COLOR_MODES = 5;
    public static final int INGAME_INFO_MODES = 4;
    static final int TILE_ANIM_FRAME_MULTIPLIER = 16;
    static final long NEW_GAME_DELAY = 500;

    public static final int MULTI_COLOR_OFF = 0;
    public static final int MULTI_COLOR_ROWS = 1;
    public static final int MULTI_COLOR_COLUMNS = 2;
    public static final int MULTI_COLOR_FRINGE = 3;
    public static final int MULTI_COLOR_SOLVED = 4;

    public static final int INGAME_INFO_MOVES = 0x1;
    public static final int INGAME_INFO_TIME = 0x2;
    public static final int INGAME_INFO_ALL = INGAME_INFO_MOVES | INGAME_INFO_TIME;

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

    /**
     * ширина игры
     */
    public static int gameWidth = 4;
    /**
     * высота игры
     */
    public static int gameHeight = 4;
    /**
     * сложный режим
     */
    public static boolean hardmode = false;
    /**
     * сохранение игр между сессиями
     */
    static boolean saveGame = true;
    /**
     * анимации
     */
    public static boolean animations = true;
    /**
     * сглаживание
     */
    public static boolean antiAlias = true;
    /**
     * длительность анимации плиток
     */
    static long tileAnimDuration = 300;
    /**
     * кол-во кадров для анимирования элементов интерфейса
     */
    public static long screenAnimDuration = 300;
    /**
     * цвет плиток
     */
    public static int tileColor = 0;
    /**
     * красить плитки по "слоям"
     */
    public static int multiColor = MULTI_COLOR_OFF;
    /**
     * цветовая тема приложения
     */
    public static int colorMode = 0;
    /**
     * текущий режим игры
     */
    public static int gameMode = Game.MODE_CLASSIC;
    /**
     * Typeface текста
     */
    public static Typeface typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
    public static boolean newGameDelay = true;
    public static int ingameInfo = INGAME_INFO_ALL;

    /**
     * хранилище настроек приложения
     */
    static SharedPreferences prefs;
    public static DateFormat dateFormat;

    /**
     * Чтение настроек приложения
     */
    static void load(Context context) {
        int w = prefs.getInt(KEY_GAME_WIDTH, gameWidth);
        if (w >= Settings.MIN_GAME_WIDTH && w < Settings.MAX_GAME_WIDTH) {
            gameWidth = w;
        }
        int h = prefs.getInt(KEY_GAME_HEIGHT, gameHeight);
        if (h >= Settings.MIN_GAME_HEIGHT && h < Settings.MAX_GAME_HEIGHT) {
            gameHeight = h;
        }
        saveGame = prefs.getBoolean(KEY_GAME_SAVE, saveGame);
        tileColor = prefs.getInt(KEY_GAME_TILE_COLOR, tileColor);
        colorMode = prefs.getInt(KEY_GAME_BG_COLOR, colorMode);
        gameMode = prefs.getInt(KEY_GAME_MODE, gameMode);
        antiAlias = prefs.getBoolean(KEY_GAME_ANTI_ALIAS, antiAlias);
        animations = prefs.getBoolean(KEY_GAME_ANIMATION, animations);
        hardmode = prefs.getBoolean(KEY_GAME_BF, hardmode);
        dateFormat = android.text.format.DateFormat.getDateFormat(context);
        multiColor = prefs.getInt(KEY_MULTI_COLOR, multiColor);
        newGameDelay = prefs.getBoolean(KEY_NEW_GAME_DELAY, newGameDelay);
        ingameInfo = prefs.getInt(KEY_INGAME_INFO, ingameInfo);
    }

    public static void save() {
        save(false);
    }

    /**
     * Запись настроек приложения
     */
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
        if (sync) {
            editor.commit();
        } else {
            editor.apply();
        }
    }

    static boolean useMultiColor() {
        return multiColor != MULTI_COLOR_OFF && !hardmode;
    }
}
