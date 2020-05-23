package com.italankin.fifteen.views;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.italankin.fifteen.Colors;
import com.italankin.fifteen.Dimensions;
import com.italankin.fifteen.Game;
import com.italankin.fifteen.R;
import com.italankin.fifteen.Settings;

import java.util.ArrayList;
import java.util.List;

public class HardModeView extends BaseView {

    private static final int ID_CHECK = 0;
    private static final int ID_PEEK = 1;

    private final Paint mPaintTextValue;
    private final Resources mResources;
    private final List<Button> mButtons = new ArrayList<>(2);
    private final RectF mRect;
    private final int mTextOffsetY;
    private Callbacks mCallbacks;

    public HardModeView(Resources res) {
        mResources = res;
        mPaintTextValue = new Paint();
        mPaintTextValue.setAntiAlias(Settings.antiAlias);
        mPaintTextValue.setTypeface(Settings.typeface);
        mPaintTextValue.setTextAlign(Paint.Align.CENTER);
        mPaintTextValue.setTextSize(Dimensions.interfaceFontSize * 1.4f);
        mPaintTextValue.setColor(Colors.getHardModeButtonsColor());

        float width = Dimensions.surfaceWidth * .75f;
        float margin = (Dimensions.surfaceWidth - width) / 2;
        mRect = new RectF(
                margin,
                Dimensions.hardModeViewMarginBottom - Dimensions.hardModeViewHeight,
                margin + width,
                Dimensions.hardModeViewMarginBottom
        );

        Rect r = new Rect();
        mPaintTextValue.getTextBounds("A", 0, 1, r);
        mTextOffsetY = r.centerY();

        updateButtons();
    }

    @Override
    public void draw(Canvas canvas, long elapsedTime) {
        for (Button button : mButtons) {
            int color;
            if ((button.frame -= elapsedTime) > 0) {
                color = Colors.ERROR;
            } else {
                color = Colors.getHardModeButtonsColor();
            }
            mPaintTextValue.setColor(color);
            canvas.drawText(button.title, button.rect.centerX(), button.rect.centerY() - mTextOffsetY, mPaintTextValue);
        }
    }

    @Override
    public void update() {
        mPaintTextValue.setColor(Colors.getOverlayTextColor());
        mPaintTextValue.setAntiAlias(Settings.antiAlias);
        updateButtons();
    }

    public void setCallbacks(Callbacks callbacks) {
        mCallbacks = callbacks;
    }

    public boolean isPeekAt(int x, int y) {
        Button peek = findButtonById(ID_PEEK);
        return peek.contains(x, y);
    }

    public boolean onClick(int x, int y) {
        if (mCallbacks != null && !Game.isNotStarted()) {
            Button check = findButtonById(ID_CHECK);
            if (check.contains(x, y)) {
                if (!mCallbacks.onCheckButtonClick()) {
                    check.frame = Settings.screenAnimDuration * 4;
                }
                return true;
            }
        }
        return false;
    }

    private Button findButtonById(int id) {
        for (Button button : mButtons) {
            if (button.id == id) {
                return button;
            }
        }
        throw new IllegalArgumentException("Unknown button with id=" + id);
    }

    private void updateButtons() {
        mButtons.clear();
        mButtons.add(new Button(ID_CHECK, mResources.getString(R.string.action_hm_check)));
        mButtons.add(new Button(ID_PEEK, mResources.getString(R.string.action_hm_peek)));
        updateButtonDimensions();
    }

    private void updateButtonDimensions() {
        int size = mButtons.size();
        float width = mRect.width() / size;
        Button b;
        for (int i = 0; i < size; i++) {
            b = mButtons.get(i);
            b.rect.set(mRect.left + width * i, mRect.top, mRect.left + width * (i + 1), mRect.bottom);
        }
    }

    private static class Button {

        final RectF rect;
        final int id;
        final String title;
        long frame = 0;

        Button(int id, String title) {
            this.rect = new RectF();
            this.title = title;
            this.id = id;
        }

        boolean contains(float x, float y) {
            return rect.contains(x, y);
        }
    }

    public interface Callbacks {

        boolean onCheckButtonClick();
    }
}
