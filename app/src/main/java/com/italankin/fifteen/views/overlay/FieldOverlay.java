package com.italankin.fifteen.views.overlay;

import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.animation.DecelerateInterpolator;

import com.italankin.fifteen.Colors;
import com.italankin.fifteen.Settings;
import com.italankin.fifteen.views.BaseView;

/**
 * Вспомогательный класс для отображения оверлеев
 */
public class FieldOverlay extends BaseView {

    private Paint mPaintBg;
    private RectF mRectField;

    private ObjectAnimator mAlphaAnimator;
    protected float mAlpha = 0;

    public FieldOverlay(RectF field) {
        mRectField = field;
        mPaintBg = new Paint();
        mPaintBg.setAntiAlias(Settings.antiAlias);
        mPaintBg.setColor(Colors.getOverlayColor());

        mAlphaAnimator = ObjectAnimator.ofFloat(this, "alpha", 0, 1);
        mAlphaAnimator.setInterpolator(new DecelerateInterpolator());
    }

    @Override
    public boolean show() {
        if (mShow) {
            return true;
        }
        if (Settings.animations) {
            mAlphaAnimator.setDuration(Settings.screenAnimDuration);
            mAlphaAnimator.start();
        }
        return (mShow = true);
    }

    /* used by ObjectAnimator */
    public void setAlpha(float alpha) {
        mAlpha = alpha;
    }

    @Override
    public boolean hide() {
        mAlphaAnimator.cancel();
        return super.hide();
    }

    @Override
    public void draw(Canvas canvas, long elapsedTime) {
        if (!mShow) {
            return;
        }
        mPaintBg.setAlpha((int) (Color.alpha(Colors.getOverlayColor()) * mAlpha));

        canvas.drawRect(mRectField, mPaintBg);
    }

    @Override
    public void update() {
        mPaintBg.setColor(Colors.getOverlayColor());
        mPaintBg.setAntiAlias(Settings.antiAlias);
    }
}
