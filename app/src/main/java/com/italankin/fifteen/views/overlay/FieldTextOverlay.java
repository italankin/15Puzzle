package com.italankin.fifteen.views.overlay;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.italankin.fifteen.Colors;
import com.italankin.fifteen.Dimensions;
import com.italankin.fifteen.Settings;

/**
 * Вспомогательный класс для отображения оверлеев
 */
public class FieldTextOverlay extends FieldOverlay {

    private final Paint mPaintText;
    private final Rect mRectBounds;
    private final String mCaption;

    public FieldTextOverlay(RectF field, String caption) {
        super(field);
        mCaption = caption;

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
    public void draw(Canvas canvas, long elapsedTime) {
        if (!mShow) {
            return;
        }
        super.draw(canvas, elapsedTime);

        mPaintText.setAlpha((int) (255 * mAlpha));

        canvas.drawText(mCaption,
                Dimensions.fieldMarginLeft + Dimensions.fieldWidth / 2.0f,
                Dimensions.fieldMarginTop + Dimensions.fieldHeight / 2.0f - mRectBounds.centerY(),
                mPaintText);
    }

    @Override
    public void update() {
        super.update();
        mPaintText.setColor(Colors.getOverlayTextColor());
        mPaintText.setAntiAlias(Settings.antiAlias);
    }
}
