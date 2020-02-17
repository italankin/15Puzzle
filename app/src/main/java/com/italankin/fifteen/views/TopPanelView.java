package com.italankin.fifteen.views;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.italankin.fifteen.Colors;
import com.italankin.fifteen.Dimensions;
import com.italankin.fifteen.Settings;
import com.italankin.fifteen.Tools;

import java.util.ArrayList;

public class TopPanelView extends BaseView {

    private ArrayList<Button> mButtons;
    private Callbacks mCallbacks;

    private int mButtonTextOffset;
    private Paint mPaintButton;
    private Paint mPaintOverlay;
    private Paint mPaintTextButton;

    public TopPanelView() {
        mButtons = new ArrayList<>();

        mPaintTextButton = new Paint();
        mPaintTextButton.setAntiAlias(Settings.antiAlias);
        mPaintTextButton.setColor(Colors.getTileTextColor());
        mPaintTextButton.setTypeface(Settings.typeface);
        mPaintTextButton.setTextAlign(Paint.Align.CENTER);
        mPaintTextButton.setTextSize(Dimensions.interfaceFontSize);

        mPaintButton = new Paint();
        mPaintButton.setAntiAlias(Settings.antiAlias);
        mPaintButton.setColor(Colors.getTileColor());

        mPaintOverlay = new Paint();
        mPaintOverlay.setColor(Colors.getBackgroundColor());

        Rect r = new Rect();
        mPaintTextButton.getTextBounds("A", 0, 1, r);
        mButtonTextOffset = r.centerY();

        mShow = true;
    }

    public void addButton(int id, String caption) {
        mButtons.add(new Button(id, caption));
        updateWidths();
    }

    private void updateWidths() {
        int size = mButtons.size();
        float width = Dimensions.surfaceWidth / size;
        Button b;
        for (int i = 0; i < size; i++) {
            b = mButtons.get(i);
            b.rect.set(width * i, 0.0f, width * (i + 1), Dimensions.topBarHeight);
        }
    }

    /**
     * Обработка событий нажатия
     *
     * @param x координата x нажатия
     * @param y координата y нажатия
     */
    public boolean onClick(float x, float y) {
        for (Button b : mButtons) {
            if (b.contains(x, y)) {
                b.setOverlay(Settings.screenAnimDuration);
                if (mCallbacks != null) {
                    mCallbacks.onTopPanelButtonClick(b.id);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas, long elapsedTime) {
        if (!mShow) {
            return;
        }

        canvas.drawRect(0.0f, 0.0f, Dimensions.surfaceWidth, Dimensions.topBarHeight, mPaintButton);

        for (Button button : mButtons) {
            if (button.frame > 0) {
                button.frame -= elapsedTime;
                float a = (float) Tools.easeOut(button.frame, 0.0f, 1.0f, Settings.screenAnimDuration);
                mPaintOverlay.setAlpha((int) (255 * (1.0f - a)));
                canvas.drawRect(button.rect, mPaintOverlay);
            }
            canvas.drawText(button.caption, button.rect.centerX(),
                    button.rect.centerY() - mButtonTextOffset, mPaintTextButton);
        }
    }

    public void update() {
        mPaintTextButton.setColor(Colors.getTileTextColor());
        mPaintTextButton.setAntiAlias(Settings.antiAlias);
        mPaintOverlay.setColor(Colors.getBackgroundColor());
        mPaintButton.setAntiAlias(Settings.antiAlias);
        mPaintButton.setColor(Colors.getTileColor());
    }

    public void addCallback(Callbacks callbacks) {
        mCallbacks = callbacks;
    }

    /**
     * Класс кнопки верхней панели
     */
    private class Button {
        final RectF rect;
        final String caption;
        final int id;
        long frame = 0;

        Button(int id, String s) {
            rect = new RectF();
            caption = s;
            this.id = id;
        }

        boolean contains(float x, float y) {
            return rect.contains(x, y);
        }

        void setOverlay(long frames) {
            frame = frames;
        }
    }

    /**
     * Интерфейс для отслеживания нажатий
     */
    public interface Callbacks {
        void onTopPanelButtonClick(int id);
    }
}
