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
     * @param t current frame
     * @param a start value
     * @param b value change
     * @param d total frames
     * @return [0.0 ... 1.0]
     */
    public static double easeOut(float t, float a, float b, float d) {
        return 1.0f - b * Math.pow(2.0f, 10.0f * (t / d - 1.0f)) + a;
    }

    public static String timeToString(int style, long duration) {
        long d = duration;
        long ms = (d % 1000);
        long sec = (d /= 1000) % 60;
        long min = (d % 3600) / 60;

        switch (style) {
            case Constants.TIME_FORMAT_MIN_SEC_MS:
                return String.format(Locale.ROOT, "%d:%02d.%d", min, sec, ms / 100);
            case Constants.TIME_FORMAT_MIN_SEC_MS_LONG:
                return String.format(Locale.ROOT, "%d:%02d.%03d", min, sec, ms);
            case Constants.TIME_FORMAT_SEC_MS_LONG:
                return String.format(Locale.ROOT, "%d.%03d", duration / 1000, ms);
            case Constants.TIME_FORMAT_MIN_SEC:
            default:
                return String.format(Locale.ROOT, "%d:%02d", min, sec);
        }
    }

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
