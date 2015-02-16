package com.onebig.puzzle;

import android.graphics.Canvas;

public class GameManager extends Thread {
    static final long FPS = 30;                         // кадры в секунду

    private GameView view;                              // главная область приложения

    private boolean running = false;                    // флаг работы потока

    public GameManager(GameView view) {
        this.view = view;
    }

    public void setRunning(boolean run) {
        running = run;
    }

    @Override
    public void run() {
        long ticksPS = 1000 / FPS;
        long startTime;
        long sleepTime;
        while (running) {
            Canvas c = null;
            startTime = System.currentTimeMillis();
            try {
                c = view.getHolder().lockCanvas();
                synchronized (view.getHolder()) {
                    if (c != null) {
                        view.draw(c);
                    }
                }
            } finally {
                if (c != null) {
                    view.getHolder().unlockCanvasAndPost(c);
                }
            }
            sleepTime = ticksPS - (System.currentTimeMillis() - startTime);
            try {
                if (sleepTime == 0) {
                    sleepTime = 10;
                }
                sleep(sleepTime);
            } catch (Exception e) {
            }
        }
    }

}
