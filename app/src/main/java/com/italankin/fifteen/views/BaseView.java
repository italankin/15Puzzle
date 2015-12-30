package com.italankin.fifteen.views;

import android.graphics.Canvas;

/**
 * Базовый класс элемента интерфейса
 */
public abstract class BaseView {

    protected boolean mShow = false;

    public boolean show() {
        return (mShow = true);
    }

    public boolean hide() {
        return mShow && !(mShow = false);
    }

    public boolean isShown() {
        return mShow;
    }

    public abstract void draw(Canvas canvas, long elapsedTime);

    public abstract void update();

}