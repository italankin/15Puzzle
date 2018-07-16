package com.italankin.fifteen;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameManager extends AbstractGameManager {

    public GameManager(GameSurface gameSurface, SurfaceHolder surfaceHolder) {
        super(gameSurface, surfaceHolder);
    }

    @Override
    public void run() {
        Canvas canvas = null;
        long startTime;
        long elapsed = 0;
        while (mRunning) {
            startTime = System.currentTimeMillis();
            try {
                canvas = mSurfaceHolder.lockCanvas();
                synchronized (mSurfaceHolder) {
                    if (canvas != null) {
                        mGameSurface.draw(canvas, elapsed, null);
                    }
                }
            } finally {
                if (canvas != null) {
                    mSurfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            elapsed = System.currentTimeMillis() - startTime;
            delay();
        }
    }

}
