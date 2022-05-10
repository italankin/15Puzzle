package com.italankin.fifteen;

import android.graphics.Color;

import java.util.Locale;

public class Tools {

    public static int interpolateColor(int startColor, int endColor, float fraction) {
        float[] start = new float[3], end = new float[3];
        Color.colorToHSV(startColor, start);
        Color.colorToHSV(endColor, end);
        for (int i = 0; i < 3; i++) {
            end[i] = (start[i] + ((end[i] - start[i]) * fraction));
        }
        return Color.HSVToColor(end);
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

    public static String formatFloat(float f) {
        return String.format(Locale.ROOT, "%.3f", f);
    }

    /**
     * @see <a href="https://en.wikipedia.org/wiki/Taxicab_geometry">Taxicab geometry</a>
     */
    public static int manhattan(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }
}
