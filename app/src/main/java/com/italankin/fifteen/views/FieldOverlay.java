package com.italankin.fifteen.views;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.italankin.fifteen.Colors;
import com.italankin.fifteen.Dimensions;
import com.italankin.fifteen.Settings;
import com.italankin.fifteen.Tools;

/**
 * Вспомогательный класс для отображения оверлеев
 */
public class FieldOverlay extends BaseView {

    private Paint mPaintBg;                         // Paint для отрисовки фона
    private Paint mPaintText;                       // Paint для отрисовки текста

    private Rect mRectBounds;                       // рассчет границ текста

    private String mCaption;                        // отображаемый текст
    private long mAnimFrames = 0;                    // кол-во кадров анимации
    private RectF mRectField;                        // границы игрового поля

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
    }

    @Override
    public boolean show() {
        if (Settings.animations) {
            mAnimFrames = Settings.screenAnimFrames;
        }
        return (mShow = true);
    }

    public void setVisible(boolean visible) {
        mShow = visible;
    }

    @Override
    public void draw(Canvas canvas, long elapsedTime) {
        if (!mShow) {
            return;
        }

        double alpha = 1;
        if (mAnimFrames > 0) {
            mAnimFrames -= elapsedTime;
            if (mAnimFrames < 0) {
                mAnimFrames = 0;
            } else {
                alpha = Tools.easeOut(mAnimFrames, 0.0f, 1.0f, Settings.screenAnimFrames);
            }
        }
        mPaintBg.setAlpha((int) (Color.alpha(Colors.getOverlayColor()) * alpha));
        mPaintText.setAlpha((int) (255 * alpha));

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