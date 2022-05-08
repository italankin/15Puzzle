package com.italankin.fifteen.game;

public final class Util {
    private static final int[] TMP = new int[2];

    public static int[] coords(int index, int width) {
        TMP[0] = index % width;
        TMP[1] = index / width;
        return TMP;
    }
}
