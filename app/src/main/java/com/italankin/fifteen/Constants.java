package com.italankin.fifteen;

public class Constants {

    public static final int MIN_GAME_WIDTH = 3;
    public static final int MIN_GAME_HEIGHT = 3;
    public static final int MAX_GAME_WIDTH = 10;
    public static final int MAX_GAME_HEIGHT = 10;

    public static final int COLOR_MODES = 3;
    public static final int COLOR_MODE_DAY = 0;
    public static final int COLOR_MODE_NIGHT = 1;
    public static final int COLOR_MODE_SYSTEM = 2;

    public static final int GAME_TYPES = 3;
    public static final int TYPE_CLASSIC = 0;
    public static final int TYPE_SNAKE = 1;
    public static final int TYPE_SPIRAL = 2;

    public static final int MULTI_COLOR_MODES = 6;
    public static final int MULTI_COLOR_OFF = 0;
    public static final int MULTI_COLOR_ROWS = 1;
    public static final int MULTI_COLOR_COLUMNS = 2;
    public static final int MULTI_COLOR_FRINGE = 3;
    public static final int MULTI_COLOR_SOLVED = 4;
    public static final int MULTI_COLOR_FRINGE3 = 5;

    public static final int INGAME_INFO_VALUES = 3;
    public static final int INGAME_INFO_ON = 0;
    public static final int INGAME_INFO_AFTER_SOLVE = 1;
    public static final int INGAME_INFO_OFF = 2;

    public static final int TIME_FORMATS = 4;
    public static final int TIME_FORMAT_MIN_SEC = 0;
    public static final int TIME_FORMAT_MIN_SEC_MS = 1;
    public static final int TIME_FORMAT_MIN_SEC_MS_LONG = 2;
    public static final int TIME_FORMAT_SEC_MS_LONG = 3;

    public static final int ANIMATION_SPEED_NORMAL = 300;
    public static final int ANIMATION_SPEED_FAST = 150;
    public static final int ANIMATION_SPEED_OFF = 0;
    public static final int[] ANIMATION_SPEED_ARRAY = {
            ANIMATION_SPEED_NORMAL,
            ANIMATION_SPEED_FAST,
            ANIMATION_SPEED_OFF
    };

    public static final int TILE_ANIM_FRAME_MULTIPLIER = 16;

    public static final long NEW_GAME_DELAY = 500;
}
