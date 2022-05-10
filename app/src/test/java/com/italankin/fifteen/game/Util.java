package com.italankin.fifteen.game;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Util {
    private static final int[] TMP = new int[2];

    public static int[] coords(int index, int width) {
        TMP[0] = index % width;
        TMP[1] = index / width;
        return TMP;
    }

    public static List<Integer> stateFromString(String value) {
        Pattern p = Pattern.compile("\\d+");
        Matcher matcher = p.matcher(value);
        List<Integer> result = new ArrayList<>();
        while (matcher.find()) {
            result.add(Integer.parseInt(matcher.group()));
        }
        return result;
    }
}
