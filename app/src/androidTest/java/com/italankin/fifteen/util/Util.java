package com.italankin.fifteen.util;

import com.italankin.fifteen.Dimensions;
import com.italankin.fifteen.game.Game;

public class Util {

    private static final float[] TMP = new float[2];

    public static float[] tileCenterCoordinates(Game game, int number) {
        int index = game.getState().indexOf(number);
        int x = index % game.getWidth();
        int y = index / game.getWidth();
        float tileX = Dimensions.fieldMarginLeft +
                x * (Dimensions.tileSize + Dimensions.spacing) +
                Dimensions.tileSize / 2;
        float tileY = Dimensions.fieldMarginTop +
                y * (Dimensions.tileSize + Dimensions.spacing) +
                Dimensions.tileSize / 2;
        TMP[0] = tileX;
        TMP[1] = tileY;
        return TMP;
    }
}
