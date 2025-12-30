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
    public static float topBarMargin;
    public static float topBarHeight;
    public static float infoBarHeight;
    public static float infoBarMarginTop;
    public static float hardModeViewHeight;
    public static float hardModeViewMarginBottom;

    public static void update(int width, int height, int insetTop) {
        update(width, height, insetTop, 1f, 1f);
    }

    public static void update(int width, int height, int insetTop, float scaleX, float scaleY) {
        surfaceWidth = width * scaleX;
        surfaceHeight = height * scaleY;

        topBarMargin = insetTop;
        topBarHeight = Dimensions.surfaceHeight * 0.07f;
        float hwf = surfaceHeight / surfaceWidth;
        float viewSpacing = Dimensions.surfaceHeight * (hwf > 1.63 ? 0.05f : 0.03f);

        infoBarMarginTop = topBarMargin + topBarHeight + viewSpacing;
        infoBarHeight = Dimensions.surfaceHeight * 0.13f;

        spacing = Math.min(surfaceWidth, surfaceHeight) * 0.015f;
        fieldMarginTop = infoBarMarginTop + infoBarHeight + viewSpacing + spacing;

        int sideMax = Math.max(Settings.gameWidth, Settings.gameHeight);
        float spriteSize = Math.min(surfaceWidth, surfaceHeight - fieldMarginTop)
                - spacing * (sideMax + 1);

        tileSize = spriteSize / sideMax;

        fieldWidth = (tileSize + spacing) * Settings.gameWidth - spacing;
        fieldHeight = (tileSize + spacing) * Settings.gameHeight - spacing;
        fieldMarginLeft = 0.5f * surfaceWidth - 0.5f * fieldWidth;
        tileFontSize = Math.max(tileSize / 2.4F, 4.25f);
        interfaceFontSize = Math.max(Math.round(surfaceWidth * 0.045), 10.0f);
        menuFontSize = interfaceFontSize * 1.5f;
        tileCornerRadius = 0.0f;

        hardModeViewHeight = surfaceHeight * 0.07f;
        hardModeViewMarginBottom = fieldMarginTop + fieldHeight + spacing + hardModeViewHeight + viewSpacing / 4;
    }
}
