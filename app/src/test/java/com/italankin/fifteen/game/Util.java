package com.italankin.fifteen.game;

import java.util.ArrayList;
import java.util.List;

public final class Util {
    private static final int[] TMP = new int[2];

    public static int[] coords(int index, int width) {
        TMP[0] = index % width;
        TMP[1] = index / width;
        return TMP;
    }

    public static List<Integer> stateFromString(String value) {
        String[] split = value.split("[\\s,;]+");
        List<Integer> result = new ArrayList<>(split.length);
        for (String s : split) {
            result.add(Integer.parseInt(s));
        }
        return result;
    }
}
