package com.italankin.fifteen;

import android.view.SurfaceHolder;

import java.util.concurrent.TimeUnit;

public abstract class AbstractGameManager extends Thread {

    protected final GameSurface mGameSurface;
    protected final SurfaceHolder mSurfaceHolder;
    protected volatile boolean mRunning = false;

    public AbstractGameManager(GameSurface gameSurface, SurfaceHolder surfaceHolder) {
        this.mGameSurface = gameSurface;
        this.mSurfaceHolder = surfaceHolder;
    }

    public void setRunning(boolean running) {
        mRunning = running;
    }

    @Override
    public abstract void run();

    void delay() {
        try {
            TimeUnit.MILLISECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
