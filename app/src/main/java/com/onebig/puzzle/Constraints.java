package com.onebig.puzzle;

public class Constraints {

    public static float surfaceWidth;                   // ширина области рисования
    public static float surfaceHeight;                  // высота области рисования
    public static float tileWidth;                      // ширина ячейки
    public static float tileHeight;                     // высота ячейки
    public static float tileCornerRadius;               // радиус закругления углов спрайтов
    public static float fieldWidth;                     // ширина поля (на области рисования)
    public static float fieldHeight;                    // высота поля (на области рисования)
    public static float fieldMarginLeft;                // отступ поля от левого края
    public static float fieldMarginTop;                 // отступ поля от верхнего края
    public static float tileFontSize;                   // размер шрифта на спрайтах
    public static float interfaceFontSize;              // размер шрифта элементов интерфейса
    public static float menuFontSize;                   // размер шрифта меню
    public static float spacing;                        // отступ между ячейками на поле

    // вычисление размеров и границ элементов
    public static void compute(int width, int height, float scale) {
        surfaceWidth = width * scale;
        surfaceHeight = height * scale;

        spacing = Math.min(surfaceWidth, surfaceHeight) * 0.015f;
        fieldMarginTop = 0.32f * surfaceHeight;

        int sideMax = Math.max(Settings.gameWidth, Settings.gameHeight);
        float spriteSize = Math.min(surfaceWidth, surfaceHeight - fieldMarginTop)
                - spacing * (sideMax + 1);

        tileWidth = spriteSize / sideMax;
        tileHeight = spriteSize / sideMax;

        fieldWidth = (tileWidth + spacing) * Settings.gameWidth - spacing;
        fieldHeight = (tileHeight + spacing) * Settings.gameHeight - spacing;
        tileFontSize = Math.max(tileHeight / 2.4F, 5.0f);
        interfaceFontSize = Math.max(Math.round(surfaceHeight * 0.029), 11.0f);
        menuFontSize = interfaceFontSize * 1.5f;
        fieldMarginLeft = 0.5f * surfaceWidth - 0.5f * fieldWidth;
        tileCornerRadius = 0.0f;
    }

}
