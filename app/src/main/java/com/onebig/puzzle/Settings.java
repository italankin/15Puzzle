package com.onebig.puzzle;

import android.content.SharedPreferences;
import android.graphics.Typeface;

public class Settings {

    public static final int MAX_GAME_WIDTH = 8;
    public static final int MAX_GAME_HEIGHT = 8;
    public static final int COLOR_MODES = 2;
    public static final int GAME_MODES = 2;

    public static final String KEY_GAME_WIDTH = "puzzle_width";
    public static final String KEY_GAME_HEIGHT = "puzzle_height";
    public static final String KEY_GAME_ARRAY = "puzzle_prev";
    public static final String KEY_GAME_MOVES = "puzzle_prev_moves";
    public static final String KEY_GAME_TIME = "puzzle_prev_time";
    public static final String KEY_GAME_SAVE = "savegame";
    public static final String KEY_GAME_TILE_COLOR = "tile_color";
    public static final String KEY_GAME_BG_COLOR = "bg_color";
    public static final String KEY_GAME_MODE = "mode";
    public static final String KEY_GAME_BF = "blind";
    public static final String KEY_GAME_ANTI_ALIAS = "antialias";
    public static final String KEY_GAME_ANIMATION = "animation";

    public static int gameWidth = 4;                    // ширина игры (в ячейках)
    public static int gameHeight = 4;                   // высота игры
    public static boolean blindfolded = false;
    public static boolean saveGame = true;              // сохранение игр между сессиями
    public static boolean animationEnabled = true;      // анимации (вкл/выкл)
    public static boolean antiAlias = true;             // сглаживание (вкл/выкл)
    public static int tileAnimFrames = 13;              // количество кадров для анимирования плиток
    public static int screenAnimFrames = 10;            // кол-во кадров для анимирования элементов интерфейса
    public static int tileColor = 0;                    // индекс ячейки в массиве с цветом плиток
    public static int colorMode = 0;                    // цвет фона
    public static int gameMode = Game.MODE_CLASSIC;     // режим игры
    public static Typeface typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD); // шрифт

    public static SharedPreferences prefs;              // хранилище настроек приложения

    // чтение настроек игры
    public static void load() {
        gameWidth = prefs.getInt(KEY_GAME_WIDTH, gameWidth);
        gameHeight = prefs.getInt(KEY_GAME_HEIGHT, gameHeight);
        saveGame = prefs.getBoolean(KEY_GAME_SAVE, saveGame);
        tileColor = prefs.getInt(KEY_GAME_TILE_COLOR, tileColor);
        colorMode = prefs.getInt(KEY_GAME_BG_COLOR, colorMode);
        gameMode = prefs.getInt(KEY_GAME_MODE, gameMode);
        antiAlias = prefs.getBoolean(KEY_GAME_ANTI_ALIAS, antiAlias);
        animationEnabled = prefs.getBoolean(KEY_GAME_ANIMATION, animationEnabled);
        blindfolded = prefs.getBoolean(KEY_GAME_BF, blindfolded);
    }

    // запись настроек игры
    public static void save() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_GAME_WIDTH, gameWidth);
        editor.putInt(KEY_GAME_HEIGHT, gameHeight);
        if (!Game.isSolved() && saveGame) {
            String string_array = Game.getGrid().toString();
            editor.putString(KEY_GAME_ARRAY, string_array.substring(1, string_array.length() - 1));
            editor.putInt(KEY_GAME_MOVES, Game.move(0));
            editor.putLong(KEY_GAME_TIME, Game.time(0));
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
        editor.putBoolean(KEY_GAME_ANIMATION, animationEnabled);
        editor.putBoolean(KEY_GAME_BF, blindfolded);
        editor.commit();
    }

}
