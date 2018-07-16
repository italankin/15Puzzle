package com.italankin.fifteen;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

import java.util.Locale;

public class GameManager extends AbstractGameManager {

    public GameManager(GameSurface gameSurface, SurfaceHolder surfaceHolder) {
        super(gameSurface, surfaceHolder);
    }

    @Override
    public void run() {
        Canvas canvas = null;
        long startTime;
        long elapsed = 0;
        float min = 999;
        float frameBuffer = 0, timeBuffer = 0;
        float allFrames = 0, allTime = 0;
        String info = "fps / min / avg";
        while (mRunning) {
            startTime = System.currentTimeMillis();
            try {
                canvas = mSurfaceHolder.lockCanvas();
                synchronized (mSurfaceHolder) {
                    if (canvas != null) {
                        mGameSurface.draw(canvas, elapsed, info);
                    }
                }
            } finally {
                if (canvas != null) {
                    mSurfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            elapsed = System.currentTimeMillis() - startTime;
            delay();
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
