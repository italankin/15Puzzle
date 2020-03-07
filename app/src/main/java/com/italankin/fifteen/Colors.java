package com.italankin.fifteen;

public class Colors {

    private static final int MUTED_BLUE = 0xff5e707e;
    private static final int MUTED_BLUE_U = 0xff454c51;

    private static final int RED = 0xff983030;
    private static final int RED_U = 0xff5b3434;

    private static final int ORANGE = 0xffd2974c;
    private static final int ORANGE_U = 0xff705a3f;

    private static final int GREEN = 0xff39885a;
    private static final int GREEN_U = 0xff385544;

    private static final int PURPLE = 0xff614d69;
    private static final int PURPLE_U = 0xff47404a;

    private static final int BLUE = 0xff5893b5;
    private static final int BLUE_U = 0xff435965;

    private static final int GREY = 0xff838383;
    private static final int GREY_U = 0xff535353;

    private static final int PINK = 0xff935783;
    private static final int PINK_U = 0xff594353;

    private static final int LIGHT_GREEN = 0xff8db368;
    private static final int LIGHT_GREEN_U = 0xff576549;

    private static final int BLACK = 0xff111111;
    private static final int BLACK_U = 0xff292929;

    private static final int WHITE = 0xffeeeeee;
    private static final int WHITE_U = 0xff7a7a7a;

    /**
     * задний фон главного экрана
     */
    public static final int[] background = {0xffcacaca, 0xff181818};
    /**
     * цвет фона поля
     */
    public static final int backgroundField = 0xff373737;
    /**
     * цвет фона оверлеев
     */
    public static final int[] overlay = {0xdaa0a0a0, 0xda373737};
    /**
     * цвет текста оверлея
     */
    public static final int[] overlayText = {0xff373737, 0xff787878};
    /**
     * цвет текста спрайтов
     */
    public static final int[] spriteText = {0xfff8f8f8, 0xff181818};
    /**
     * цвет текста инфо
     */
    public static final int[] textInfo = {0xffcacaca, 0xff787878};
    /**
     * цвет текста значений в меню настроек
     */
    public static final int menuTextValue = 0xfffefefe;
    /**
     * цвета спрайтов (день)
     */
    public static final int[] tilesDay = {
            MUTED_BLUE,
            RED,
            ORANGE,
            GREEN,
            PURPLE,
            BLUE,
            PINK,
            LIGHT_GREEN,
            GREY,
            BLACK
    };
    public static final int[] tilesDayUnsolved = {
            MUTED_BLUE_U,
            RED_U,
            ORANGE_U,
            GREEN_U,
            PURPLE_U,
            BLUE_U,
            PINK_U,
            LIGHT_GREEN_U,
            GREY_U,
            BLACK_U
    };
    /**
     * цвета спрайтов (ночь)
     */
    public static final int[] tilesNight = {
            MUTED_BLUE,
            RED,
            ORANGE,
            GREEN,
            PURPLE,
            BLUE,
            PINK,
            LIGHT_GREEN,
            GREY,
            WHITE
    };
    public static final int[] tilesNightUnsolved = {
            MUTED_BLUE_U,
            RED_U,
            ORANGE_U,
            GREEN_U,
            PURPLE_U,
            BLUE_U,
            PINK_U,
            LIGHT_GREEN_U,
            GREY_U,
            WHITE_U
    };
    /**
     * цвета спрайтов
     */
    public static final int[] multiColorTiles = {
            RED,
            BLUE,
            GREEN,
            ORANGE,
            PURPLE,
            MUTED_BLUE,
            GREY
    };

    /**
     * @return цвет фона для текущей цветовой схемы
     */
    public static int getBackgroundColor() {
        return background[Settings.colorMode];
    }

    /**
     * @return текущий цвет плиток
     */
    public static int getTileColor() {
        if (Settings.colorMode == 0) {
            return tilesDay[Settings.tileColor];
        } else {
            return tilesNight[Settings.tileColor];
        }
    }

    public static int[] getTileColors() {
        if (Settings.colorMode == 0) {
            return tilesDay;
        } else {
            return tilesNight;
        }
    }

    public static int getUnsolvedTileColor() {
        if (Settings.colorMode == 0) {
            return tilesDayUnsolved[Settings.tileColor];
        } else {
            return tilesNightUnsolved[Settings.tileColor];
        }
    }

    /**
     * @return цвет текста на плитках
     */
    public static int getTileTextColor() {
        return spriteText[Settings.colorMode];
    }

    /**
     * @return цвет текста информации
     */
    public static int getInfoTextColor() {
        return textInfo[Settings.colorMode];
    }

    /**
     * @return цвет оверлея для текущей цветовой схемы
     */
    public static int getOverlayColor() {
        return overlay[Settings.colorMode];
    }

    /**
     * @return цвет текста оверлея для текущей цветовой схемы
     */
    public static int getOverlayTextColor() {
        return overlayText[Settings.colorMode];
    }
}
