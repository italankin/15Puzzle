package com.italankin.fifteen.anim;

import android.animation.TimeInterpolator;
import android.view.animation.LinearInterpolator;

import com.italankin.fifteen.Tile;

/**
 * Базовый класс для анимирования тайлов.
 */
public abstract class TileAnimator {

    private final Tile target;
    private final float[] values = new float[2];
    private float delta;

    private long delay = 0;
    private boolean cancelled = true;
    private long elapsed = 0;
    private long duration = 0;
    private TimeInterpolator interpolator = new LinearInterpolator();

    public TileAnimator(Tile target) {
        this.target = target;
    }

    public void setValues(float start, float end) {
        this.values[0] = start;
        this.values[1] = end;
        this.delta = end - start;
    }

    public void setStartDelay(long delay) {
        this.delay = delay;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setInterpolator(TimeInterpolator interpolator) {
        this.interpolator = interpolator;
    }

    public boolean isRunning() {
        return !cancelled;
    }

    public void start() {
        cancelled = false;
        elapsed = 0;
        update(target, values[0]);
    }

    public void cancel() {
        cancelled = true;
    }

    /**
     * Рендер следующего кадра
     *
     * @param timeDelta время, прошедшее после отрисовки предыдущего кадра
     */
    public void nextFrame(long timeDelta) {
        if (cancelled) {
            return;
        }
        elapsed += timeDelta;
        if (delay > 0) {
            if (elapsed > delay) {
                elapsed -= delay;
                delay = 0;
            } else {
                return;
            }
        }
        if (elapsed >= duration) {
            update(target, values[1]);
            cancel();
        } else {
            float progress = (float) elapsed / (float) duration;
            float value = values[0] + this.delta * interpolator.getInterpolation(progress);
            update(target, value);
        }
    }

    /**
     * Обновление состояния
     *
     * @param target тайл
     * @param value  новое значение для анимируемого свойства
     */
    protected abstract void update(Tile target, float value);

}
