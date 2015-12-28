package com.italankin.fifteen;

public class Colors {

    /**
     * задний фон главного экрана
     */
    public static int background[] = {0xffcacaca, 0xff181818};
    /**
     * цвет фона поля
     */
    public static int backgroundField = 0xff373737;
    /**
     * цвет фона оверлеев
     */
    public static int overlay[] = {0xdaa0a0a0, 0xda373737};
    /**
     * цвет текста оверлея
     */
    public static int overlayText[] = {0xff373737, 0xff787878};
    /**
     * цвет текста спрайтов
     */
    public static int spriteText[] = {0xfff8f8f8, 0xff181818};
    /**
     * цвет текста инфо
     */
    public static int textInfo[] = {0xffcacaca, 0xff787878};
    /**
     * цвет текста значений в меню настроек
     */
    public static int menuTextValue = 0xfffefefe;
    /**
     * цвета спрайтов
     */
    public static int tiles[] = {
            0xff5e707e, // синий+серый
            0xff983030, // красный
            0xffd2974c, // оранжевый
            0xff39885a, // зеленый
            0xff614d69, // фиолетовый
            0xff5893b5, // синий
            0xff838383  // серый
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
        return tiles[Settings.tileColor];
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
