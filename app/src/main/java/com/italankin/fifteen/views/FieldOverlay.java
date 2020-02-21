package com.italankin.fifteen.views;

import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.animation.DecelerateInterpolator;

import com.italankin.fifteen.Colors;
import com.italankin.fifteen.Dimensions;
import com.italankin.fifteen.Settings;

/**
 * Вспомогательный класс для отображения оверлеев
 */
public class FieldOverlay extends BaseView {

    private Paint mPaintBg;                         // Paint для отрисовки фона
    private Paint mPaintText;                       // Paint для отрисовки текста

    private Rect mRectBounds;                       // рассчет границ текста

    private String mCaption;                        // отображаемый текст
    private RectF mRectField;                        // границы игрового поля

    private ObjectAnimator mAlphaAnimator;
    private float mAlpha = 1;

    /**
     * @param s текст надписи на оверлее
     */
    public FieldOverlay(RectF field, String s) {
        mRectField = field;
        mCaption = s;
        mPaintBg = new Paint();
        mPaintBg.setAntiAlias(Settings.antiAlias);
        mPaintBg.setColor(Colors.getOverlayColor());

        mPaintText = new Paint();
        mPaintText.setAntiAlias(Settings.antiAlias);
        mPaintText.setColor(Colors.getOverlayTextColor());
        mPaintText.setTypeface(Settings.typeface);
        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.setTextSize(2.3f * Dimensions.interfaceFontSize);

        mRectBounds = new Rect();
        mPaintText.getTextBounds(mCaption, 0, mCaption.length(), mRectBounds);

        mAlphaAnimator = ObjectAnimator.ofFloat(this, "alpha", 0, 1);
        mAlphaAnimator.setInterpolator(new DecelerateInterpolator(2));
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

    /**
     * Устанавливает прозрачность элемента. Используется для анимации.
     *
     * @param alpha значение прозрачности в интервале [0, 1]
     */
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
        mPaintText.setAlpha((int) (255 * mAlpha));

        canvas.drawRoundRect(mRectField, Dimensions.tileCornerRadius, Dimensions.tileCornerRadius,
                mPaintBg);
        canvas.drawText(mCaption,
                Dimensions.fieldMarginLeft + Dimensions.fieldWidth / 2.0f,
                Dimensions.fieldMarginTop + Dimensions.fieldHeight / 2.0f - mRectBounds.centerY(),
                mPaintText);

    }

    @Override
    public void update() {
        mPaintBg.setColor(Colors.getOverlayColor());
        mPaintText.setColor(Colors.getOverlayTextColor());

        mPaintBg.setAntiAlias(Settings.antiAlias);
        mPaintText.setAntiAlias(Settings.antiAlias);
    }

}
