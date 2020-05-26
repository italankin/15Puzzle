package com.italankin.fifteen;

import java.util.ArrayList;
import java.util.List;

public class Generator {

    public static List<Integer> fill(int width, int height, int mode) {
        switch (mode) {
            case Constants.MODE_CLASSIC:
                return fillClassic(width, height);
            case Constants.MODE_SNAKE:
                return fillSnake(width, height);
            default:
                throw new IllegalStateException("Unknown mode=" + mode);
        }
    }

    private static List<Integer> fillClassic(int width, int height) {
        int size = width * height;
        List<Integer> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add((i + 1) % size);
        }
        return result;
    }

    private static List<Integer> fillSnake(int width, int height) {
        int size = width * height;
        List<Integer> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            int row = i / width;
            int n;
            if (row % 2 == 0) {
                n = i + 1;
            } else {
                n = (width * (1 + i / width) - i % width);
            }
            result.add((n) % size);
        }
        return result;
    }
}
