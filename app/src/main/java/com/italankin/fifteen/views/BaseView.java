package com.italankin.fifteen.views;

import android.graphics.Canvas;

/**
 * Базовый класс элемента интерфейса
 */
public abstract class BaseView {

    protected boolean mShow = false;

    /**
     * Показать элемент на экране.
     *
     * @return {@code true}, если элемент будет показан.
     */
    public boolean show() {
        return (mShow = true);
    }

    /**
     * Скрыть элемент.
     *
     * @return {@code true}, если элемент будет скрыт, {@code false}, если он уже находился в скрытом
     * состоянии
     */
    public boolean hide() {
        if (mShow) {
            mShow = false;
            return true;
        }
        return false;
    }

    /**
     * @return состояние видимости элемента
     */
    public boolean isShown() {
        return mShow;
    }

    /**
     * Отрисовка элемента.
     *
     * @param canvas      холст, на котором нужно нарисовать себя
     * @param elapsedTime время, прошедшее с момента отрисовки последнего кадра
     */
    public abstract void draw(Canvas canvas, long elapsedTime);

    /**
     * Обновление состояния элемента (цветов, размеров и т.д.) в соответствии с текущими настройками.
     */
    public abstract void update();

}
