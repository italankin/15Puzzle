package com.italankin.fifteen;

import android.graphics.Canvas;

import java.util.Locale;

public class GameManager extends Thread {

    /**
     * Главная область приложения
     */
    private final GameSurface mView;

    /**
     * Флаг работы потока
     */
    private boolean mRunning = false;

    public GameManager(GameSurface view) {
        this.mView = view;
    }

    public void setRunning(boolean running) {
        mRunning = running;
    }

    @Override
    public void run() {
        long startTime;
        long elapsed = 0;
        float min = 999;
        float frameBuffer = 0, timeBuffer = 0;
        float allFrames = 0, allTime = 0;
        String info = "fps / min / avg";
        while (mRunning) {
            Canvas c = null;
            startTime = System.currentTimeMillis();
            try {
                c = mView.getHolder().lockCanvas();
                synchronized (mView.getHolder()) {
                    if (c != null) {
                        mView.draw(c, elapsed, info);
                    }
                }
            } finally {
                if (c != null) {
                    mView.getHolder().unlockCanvasAndPost(c);
                }
            }
            elapsed = System.currentTimeMillis() - startTime;
            timeBuffer += elapsed;
            frameBuffer += 1.0f;
            if (timeBuffer > 1000) {
                float fps = frameBuffer / (timeBuffer / 1000);
                if (fps < min) {
                    min = fps;
                }
                allTime += timeBuffer;
                allFrames += frameBuffer;
                info = String.format(Locale.ROOT, "%.1f / %.1f / %.1f", fps, min,
                        allFrames / (allTime / 1000));
                frameBuffer = 0;
                timeBuffer = 0;
            }
        }
    }

}
