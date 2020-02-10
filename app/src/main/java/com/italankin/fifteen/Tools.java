package com.italankin.fifteen;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Tools {

    public static final int DIRECTION_DEFAULT = -1;
    public static final int DIRECTION_UP = 0;
    public static final int DIRECTION_RIGHT = 1;
    public static final int DIRECTION_DOWN = 2;
    public static final int DIRECTION_LEFT = 3;

    /**
     * Функция "сглаживания" (анимации и т.д.)
     *
     * @param t текущий кадр (или мс)
     * @param a начальное значение
     * @param b изменение значения
     * @param d общая длительность (кадры или мс)
     * @return число в промежутке [0.0 ... 1.0]
     */
    public static double easeOut(float t, float a, float b, float d) {
        return 1.0f - b * Math.pow(2.0f, 10.0f * (t / d - 1.0f)) + a;
    }

    /**
     * Форматирование строки для отображения времени
     *
     * @param duration время в мс
     * @return форматированная строка времени
     */
    public static String timeToString(long duration) {
        long ms = (duration % 1000) / 100;
        long sec = (duration /= 1000) % 60;
        long min = (duration % 3600) / 60;

        return String.format(Locale.US, "%d:%02d.%d", min, sec, ms);
    }

    /**
     * Преобразование массива строк в массив чисел
     */
    public static List<Integer> getIntegerArray(String... strings) {
        ArrayList<Integer> result = new ArrayList<>();
        for (String s : strings) {
            try {
                result.add(Integer.parseInt(s));
            } catch (NumberFormatException e) {
                Log.e("getIntegerArray", "Error: " + s + " - invalid number");
                return Collections.emptyList();
            }
        }
        return result;
    }

    /**
     * Направление вектора движения
     *
     * @param dx изменение x
     * @param dy изменение y
     * @return константу направления
     */
    public static int direction(float dx, float dy) {
        if (dx == 0 && dy == 0) {
            return DIRECTION_DEFAULT;
        }
        if (Math.abs(dx) > Math.abs(dy)) {
            return dx > 0 ? DIRECTION_RIGHT : DIRECTION_LEFT;
        } else {
            return dy > 0 ? DIRECTION_DOWN : DIRECTION_UP;
        }
    }


    /**
     * Вычисление расстояния между клетками на игровом поле
     *
     * @param x1 x первой ячейки
     * @param y1 y первой ячейки
     * @param x2 x второй ячейки
     * @param y2 y второй ячейки
     * @return расстояние между ячейками
     * @see <a href="https://en.wikipedia.org/wiki/Taxicab_geometry">Taxicab geometry</a>
     */
    public static int manhattan(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    public static void log(String message) {
        if (BuildConfig.DEBUG) {
            Log.d("15puzzle", message);
        }
    }
}
