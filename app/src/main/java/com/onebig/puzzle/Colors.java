package com.onebig.puzzle;

public class Colors {

    public static int background[] = {0xffcacaca, 0xff181818};      // задний фон главного экрана
    public static int backgroundField = 0xff373737;                 // цвет фона поля
    public static int overlay[] = {0xdaa0a0a0, 0xda373737};         // цвет фона оверлея
    public static int overlayText[] = {0xff373737, 0xff787878};     // цвет текста оверлея
    public static int spriteText[] = {0xfff8f8f8, 0xff181818};      // цвет текста спрайтов
    public static int textInfo[] = {0xffcacaca, 0xff787878};        // цвет текста инфо
    public static int menuTextValue = 0xfffefefe;                   // цвет текста значений в меню
    public static int tiles[] = {                                   // цвета спрайтов
            0xffe39836, 0xff5e7bbf,
            0xffe74c3c, 0xff2ecc71,
            0xff6cc0d4, 0xff9b59b6,
            0xff979797
    };

    public static int getBackgroundColor() {
        return background[Settings.colorMode];
    }

    public static int getTileColor() {
        return tiles[Settings.tileColor];
    }

    public static int getTileTextColor() {
        return spriteText[Settings.colorMode];
    }

    public static int getInfoTextColor() {
        return textInfo[Settings.colorMode];
    }

    public static int getOverlayColor() {
        return overlay[Settings.colorMode];
    }

    public static int getOverlayTextColor() {
        return overlayText[Settings.colorMode];
    }

}
