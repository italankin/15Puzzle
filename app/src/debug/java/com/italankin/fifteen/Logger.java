package com.italankin.fifteen;

import android.util.Log;

public class Logger {

    private static final String TAG = "15puzzle";

    public static void e(Throwable e, String message, Object... args) {
        Log.e(TAG, String.format(message, args), e);
    }

    public static void d(String message, Object... args) {
        Log.d(TAG, String.format(message, args));
    }
}
