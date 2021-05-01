package com.italankin.fifteen;

import java.util.ArrayList;
import java.util.List;

public class Generator {

    public static List<Integer> generate(int width, int height, int type) {
        switch (type) {
            case Constants.TYPE_CLASSIC:
                return classic(width, height);
            case Constants.TYPE_SNAKE:
                return snake(width, height);
            case Constants.TYPE_SPIRAL:
                return spiral(width, height);
            default:
                throw new IllegalStateException("Unknown type=" + type);
        }
    }

    private static List<Integer> classic(int width, int height) {
        int size = width * height;
        List<Integer> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add((i + 1) % size);
        }
        return result;
    }

    private static List<Integer> snake(int width, int height) {
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
            result.add(n % size);
        }
        return result;
    }

    private static List<Integer> spiral(int width, int height) {
        int h = height, w = width, size = width * height;
        int number = 1;
        int r = 0, c = 0; // start row and column
        int[][] array = new int[h][w];
        while (r < h && c < w) {
            for (int i = c; i < w; i++) {
                array[r][i] = number++ % size;
            }
            r++;
            for (int i = r; i < h; i++) {
                array[i][w - 1] = number++ % size;
            }
            w--;
            if (r < h) {
                for (int i = w - 1; i >= c; i--) {
                    array[h - 1][i] = number++ % size;
                }
                h--;
            }
            if (c < w) {
                for (int i = h - 1; i >= r; i--) {
                    array[i][c] = number++ % size;
                }
                c++;
            }
        }
        List<Integer> result = new ArrayList<>(size);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                result.add(array[i][j]);
            }
        }
        return result;
    }
}
