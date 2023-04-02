package com.italankin.fifteen.views.overlay;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.italankin.fifteen.Colors;
import com.italankin.fifteen.Dimensions;
import com.italankin.fifteen.R;
import com.italankin.fifteen.Settings;

public class NewGameConfirmationOverlay extends FieldOverlay {

    private final Paint paint;
    private final String textTitle;
    private final String textButtonConfirm;
    private final String textButtonResume;

    private final RectF rectButtonConfirm;
    private final RectF rectButtonResume;
    private final int textHeight;

    private final float textSizeTitle;
    private final float textSizeButton;

    private Callback callback;

    public NewGameConfirmationOverlay(RectF screen, Resources res) {
        super(screen);

        paint = new Paint();
        paint.setAntiAlias(Settings.antiAlias);
        paint.setColor(Colors.getOverlayTextColor());
        paint.setTypeface(Settings.typeface);
        paint.setTextAlign(Paint.Align.CENTER);

        textSizeTitle = 1.5f * Dimensions.menuFontSize;
        textSizeButton = Dimensions.menuFontSize;

        textTitle = res.getString(R.string.confirm_new_game_title);
        textButtonConfirm = res.getString(R.string.confirm_new_game_confirm);
        textButtonResume = res.getString(R.string.confirm_new_game_resume);

        rectButtonConfirm = new RectF();
        rectButtonResume = new RectF();
        float buttonTop = Dimensions.fieldMarginTop + Dimensions.fieldHeight * .66f;

        Rect r = new Rect();
        paint.setTextSize(textSizeButton);
        paint.getTextBounds(textButtonConfirm, 0, 1, r);
        textHeight = r.height();

        rectButtonConfirm.set(0, buttonTop, screen.right / 2f, buttonTop + textHeight);
        rectButtonResume.set(screen.right / 2f, buttonTop, screen.right, buttonTop + textHeight);

        float inset = -Dimensions.spacing * 2f;
        rectButtonConfirm.inset(inset, inset);
        rectButtonResume.inset(inset, inset);
    }

    public void addCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void draw(Canvas canvas, long elapsedTime) {
        if (!mShow) {
            return;
        }
        super.draw(canvas, elapsedTime);
        paint.setTextSize(textSizeTitle);
        canvas.drawText(textTitle,
                Dimensions.fieldMarginLeft + Dimensions.fieldWidth / 2f,
                Dimensions.fieldMarginTop + Dimensions.fieldHeight / 3f,
                paint);
        paint.setTextSize(textSizeButton);
        canvas.drawText(textButtonConfirm,
                rectButtonConfirm.centerX(),
                rectButtonConfirm.centerY() + textHeight / 2f,
                paint);
        canvas.drawText(textButtonResume,
                rectButtonResume.centerX(),
                rectButtonResume.centerY() + textHeight / 2f,
                paint);
    }

    public void onClick(float x, float y) {
        if (rectButtonConfirm.contains(x, y)) {
            callback.onNewGameConfirm();
        } else if (rectButtonResume.contains(x, y)) {
            callback.onResumeGame();
        }
    }

    @Override
    public void update() {
        super.update();
        paint.setColor(Colors.getOverlayTextColor());
        paint.setAntiAlias(Settings.antiAlias);
    }

    public interface Callback {

        void onNewGameConfirm();

        void onResumeGame();
    }
}
