package com.italankin.fifteen;

import android.graphics.Canvas;
import android.util.Log;

public class GameManager extends Thread {

    /**
     * Кадры в секунду
     */
    public static final long FPS = 30;

    /**
     * Главная область приложения
     */
    private GameView mView;

    /**
     * Флаг работы потока
     */
    private boolean mRunning = false;

    public GameManager(GameView view) {
        this.mView = view;
    }

    public void setRunning(boolean run) {
        mRunning = run;
    }

    @Override
    public void run() {
        long ticksPS = 1000 / FPS;
        long startTime;
        long sleepTime;
        while (mRunning) {
            Canvas c = null;
            startTime = System.currentTimeMillis();
            try {
                c = mView.getHolder().lockCanvas();
                synchronized (mView.getHolder()) {
                    if (c != null) {
                        mView.draw(c);
                    }
                }
            } finally {
                if (c != null) {
                    mView.getHolder().unlockCanvasAndPost(c);
                }
            }
            sleepTime = ticksPS - (System.currentTimeMillis() - startTime);
            try {
                if (sleepTime < 1) {
                    sleepTime = 10;
                }
                sleep(sleepTime);
            } catch (Exception e) {
                Log.e("GameManager", e.toString());
            }
        }
    }

}
