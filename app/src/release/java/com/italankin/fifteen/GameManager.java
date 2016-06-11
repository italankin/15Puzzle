package com.italankin.fifteen;

import android.graphics.Canvas;

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
        while (mRunning) {
            Canvas c = null;
            startTime = System.currentTimeMillis();
            try {
                c = mView.getHolder().lockCanvas();
                synchronized (mView.getHolder()) {
                    if (c != null) {
                        mView.draw(c, elapsed, null);
                    }
                }
            } finally {
                if (c != null) {
                    mView.getHolder().unlockCanvasAndPost(c);
                }
            }
            elapsed = System.currentTimeMillis() - startTime;
        }
    }

}
