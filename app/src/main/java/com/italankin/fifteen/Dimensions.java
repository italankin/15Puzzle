package com.italankin.fifteen;

public class Dimensions {

    public static float surfaceWidth;
    public static float surfaceHeight;
    public static float tileSize;
    public static float tileCornerRadius;
    public static float fieldWidth;
    public static float fieldHeight;
    public static float fieldMarginLeft;
    public static float fieldMarginTop;
    public static float tileFontSize;
    public static float interfaceFontSize;
    public static float menuFontSize;
    public static float spacing;
    public static float topBarHeight;
    public static float infoBarHeight;

    public static void update(int width, int height) {
        update(width, height, 1f);
    }

    public static void update(int width, int height, float scale) {
        surfaceWidth = width * scale;
        surfaceHeight = height * scale;

        spacing = Math.min(surfaceWidth, surfaceHeight) * 0.015f;
        fieldMarginTop = 0.32f * surfaceHeight;

        int sideMax = Math.max(Settings.gameWidth, Settings.gameHeight);
        float spriteSize = Math.min(surfaceWidth, surfaceHeight - fieldMarginTop)
                - spacing * (sideMax + 1);

        tileSize = spriteSize / sideMax;

        fieldWidth = (tileSize + spacing) * Settings.gameWidth - spacing;
        fieldHeight = (tileSize + spacing) * Settings.gameHeight - spacing;
        tileFontSize = Math.max(tileSize / 2.4F, 4.25f);
        interfaceFontSize = Math.max(Math.round(surfaceWidth * 0.045), 10.0f);
        menuFontSize = interfaceFontSize * 1.5f;
        fieldMarginLeft = 0.5f * surfaceWidth - 0.5f * fieldWidth;
        tileCornerRadius = 0.0f;
        topBarHeight = Dimensions.surfaceHeight * 0.07f;
        infoBarHeight = Dimensions.surfaceHeight * 0.13f;
    }

}
