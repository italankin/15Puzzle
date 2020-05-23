package com.italankin.fifteen;

public class Constants {

    public static final int MIN_GAME_WIDTH = 3;
    public static final int MIN_GAME_HEIGHT = 3;
    public static final int MAX_GAME_WIDTH = 8;
    public static final int MAX_GAME_HEIGHT = 8;

    public static final int COLOR_MODES = 2;
    public static final int COLOR_MODE_DAY = 0;
    public static final int COLOR_MODE_NIGHT = 1;

    public static final int GAME_MODES = 2;
    public static final int MODE_CLASSIC = 0;
    public static final int MODE_SNAKE = 1;

    public static final int MULTI_COLOR_MODES = 5;
    public static final int MULTI_COLOR_OFF = 0;
    public static final int MULTI_COLOR_ROWS = 1;
    public static final int MULTI_COLOR_COLUMNS = 2;
    public static final int MULTI_COLOR_FRINGE = 3;
    public static final int MULTI_COLOR_SOLVED = 4;

    public static final int INGAME_INFO_MODES = 4;
    public static final int INGAME_INFO_MOVES = 0x1;
    public static final int INGAME_INFO_TIME = 0x2;
    public static final int INGAME_INFO_ALL = INGAME_INFO_MOVES | INGAME_INFO_TIME;

    public static final int TIME_FORMATS = 4;
    public static final int TIME_FORMAT_MIN_SEC = 0;
    public static final int TIME_FORMAT_MIN_SEC_MS = 1;
    public static final int TIME_FORMAT_MIN_SEC_MS_LONG = 2;
    public static final int TIME_FORMAT_SEC_MS_LONG = 3;

    public static final int TILE_ANIM_FRAME_MULTIPLIER = 16;

    public static final long NEW_GAME_DELAY = 500;
}
