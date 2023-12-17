package com.italankin.fifteen;

public class Colors {

    private static final int MUTED_BLUE = 0xff5e707e;
    private static final int MUTED_BLUE_U = 0xff454c51;
    private static final int MUTED_BLUE_F3 = 0xff95adbf;

    private static final int RED = 0xff983030;
    private static final int RED_U = 0xff5b3434;
    private static final int RED_F3 = 0xffe33939;

    private static final int ORANGE = 0xffd2974c;
    private static final int ORANGE_U = 0xff705a3f;
    private static final int ORANGE_F3 = 0xfff29927;

    private static final int GREEN = 0xff39885a;
    private static final int GREEN_U = 0xff385544;
    private static final int GREEN_F3 = 0xff29ba66;

    private static final int PURPLE = 0xff614d69;
    private static final int PURPLE_U = 0xff47404a;
    private static final int PURPLE_F3 = 0xff8e58a3;

    private static final int BLUE = 0xff5893b5;
    private static final int BLUE_U = 0xff435965;
    private static final int BLUE_F3 = 0xff23a2eb;

    private static final int GREY = 0xff838383;
    private static final int GREY_U = 0xff535353;
    private static final int GREY_F3 = 0xffb3b3b3;

    private static final int PINK = 0xff935783;
    private static final int PINK_U = 0xff594353;
    private static final int PINK_F3 = 0xffd158b1;

    private static final int LIGHT_GREEN = 0xff8db368;
    private static final int LIGHT_GREEN_U = 0xff576549;
    private static final int LIGHT_GREEN_F3 = 0xff85c445;

    private static final int BLACK = 0xff111111;
    private static final int BLACK_U = 0xff292929;

    private static final int WHITE = 0xffeeeeee;
    private static final int WHITE_U = 0xff7a7a7a;

    private static final int CYAN = 0xff3fa694;
    private static final int CYAN_U = 0xff4e6e68;
    private static final int CYAN_F3 = 0xff39d4ba;

    public static final int backgroundField = 0xff373737;
    public static final int menuTextValue = 0xfffefefe;
    private static final int[] background = {0xffcacaca, 0xff181818};
    private static final int[] overlay = {0xdaa0a0a0, 0xda373737};
    private static final int[] overlayText = {0xff373737, 0xff787878};
    private static final int[] spriteText = {0xfff8f8f8, 0xff181818};
    private static final int[] textInfo = {0xffcacaca, 0xff787878};
    private static final int[] tilesDay = {
            MUTED_BLUE,
            RED,
            ORANGE,
            CYAN,
            GREEN,
            PURPLE,
            BLUE,
            PINK,
            LIGHT_GREEN,
            GREY,
            BLACK,
    };
    private static final int[] tilesDayUnsolved = {
            MUTED_BLUE_U,
            RED_U,
            ORANGE_U,
            CYAN_U,
            GREEN_U,
            PURPLE_U,
            BLUE_U,
            PINK_U,
            LIGHT_GREEN_U,
            GREY_U,
            BLACK_U,
    };
    private static final int[] tilesNight = {
            MUTED_BLUE,
            RED,
            ORANGE,
            CYAN,
            GREEN,
            PURPLE,
            BLUE,
            PINK,
            LIGHT_GREEN,
            GREY,
            WHITE,
    };
    private static final int[] tilesNightUnsolved = {
            MUTED_BLUE_U,
            RED_U,
            ORANGE_U,
            CYAN_U,
            GREEN_U,
            PURPLE_U,
            BLUE_U,
            PINK_U,
            LIGHT_GREEN_U,
            GREY_U,
            WHITE_U,
    };
    public static final int[] multiColorTiles = {
            RED,
            BLUE,
            GREEN,
            ORANGE,
            PURPLE,
            MUTED_BLUE,
            GREY,
            LIGHT_GREEN,
            PINK,
            CYAN
    };
    public static final int[] multiColorTilesFringe3 = {
            RED_F3,
            BLUE_F3,
            GREEN_F3,
            ORANGE_F3,
            PURPLE_F3,
            MUTED_BLUE_F3,
            GREY_F3,
            LIGHT_GREEN_F3,
            PINK_F3,
            CYAN_F3
    };
    public static final int ERROR = 0xffd24242;
    public static final int NEW_APP = 0xff00639b;
    public static final int NEW_APP_TEXT = 0xffffffff;

    public static int getBackgroundColor() {
        return background[Settings.getColorMode()];
    }

    public static int getTileColor() {
        if (Settings.getColorMode() == Constants.COLOR_MODE_DAY) {
            return tilesDay[Settings.tileColor];
        } else {
            return tilesNight[Settings.tileColor];
        }
    }

    public static int[] getTileColors() {
        if (Settings.getColorMode() == Constants.COLOR_MODE_DAY) {
            return tilesDay;
        } else {
            return tilesNight;
        }
    }

    public static int getUnsolvedTileColor() {
        if (Settings.getColorMode() == Constants.COLOR_MODE_DAY) {
            return tilesDayUnsolved[Settings.tileColor];
        } else {
            return tilesNightUnsolved[Settings.tileColor];
        }
    }

    public static int getTileTextColor() {
        return spriteText[Settings.getColorMode()];
    }

    public static int getInfoTextColor() {
        return textInfo[Settings.getColorMode()];
    }

    public static int getOverlayColor() {
        return overlay[Settings.getColorMode()];
    }

    public static int getOverlayTextColor() {
        return overlayText[Settings.getColorMode()];
    }

    public static int getHardModeButtonsColor() {
        return Settings.getColorMode() == Constants.COLOR_MODE_DAY ? 0xff373737 : 0xff787878;
    }
}
