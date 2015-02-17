package com.onebig.puzzle;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Tools {

    // направления движения плиток
    public static final int DIRECTION_DEFAULT = -1;
    public static final int DIRECTION_UP = 0;
    public static final int DIRECTION_RIGHT = 1;
    public static final int DIRECTION_DOWN = 2;
    public static final int DIRECTION_LEFT = 3;

    // функция "сглаживания" анимации
    // t - текущий кадр
    // b - начальное значение
    // c - изменение значения
    // d - общая длительность
    public static double easeOut(float t, float b, float c, float d) {
        return 1.0f - c * Math.pow(2.0f, 10.0f * (t / d - 1.0f)) + b;
    }

    // форматирование строки для отображения времени
    public static String timeToString(long duration) {
        long ms = (duration % 1000) / 100;
        long sec = (duration /= 1000) % 60;
        long min = (duration % 3600) / 60;

        return String.format("%d:%02d.%d", min, sec, ms);
    }

    // преобразование массива строк в массив чисел
    public static ArrayList<Integer> getIntegerArray(List<String> list) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        for (String s : list) {
            try {
                result.add(Integer.parseInt(s));
            } catch (NumberFormatException e) {
                Log.e("getIntegerArray", "Error: " + s + " - invalid number");
            }
        }
        return result;
    }

    // направление вектора движения
    public static int direction(float dx, float dy) {
        return (Math.abs(dx) > Math.abs(dy)) ? ((dx > 0) ? DIRECTION_RIGHT : DIRECTION_LEFT) : ((dy > 0) ? DIRECTION_DOWN : DIRECTION_UP);
    }

}
