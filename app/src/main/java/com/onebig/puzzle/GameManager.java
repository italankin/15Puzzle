package com.onebig.puzzle;

import android.graphics.Canvas;
import android.util.Log;

public class GameManager extends Thread {
    static final long FPS = 30;                         // кадры в секунду

    private GameView mView;                             // главная область приложения

    private boolean mRunning = false;                   // флаг работы потока

    public GameManager(GameView view) {
        this.mView = view;
    }

    public void setmRunning(boolean run) {
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
                if (sleepTime == 0) {
                    sleepTime = 10;
                }
                sleep(sleepTime);
            } catch (InterruptedException e) {
                Log.e("GameManager", e.toString());
            }
        }
    }

}
