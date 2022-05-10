package com.italankin.fifteen.views.help;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.italankin.fifteen.Colors;
import com.italankin.fifteen.Dimensions;
import com.italankin.fifteen.GameState;
import com.italankin.fifteen.R;
import com.italankin.fifteen.Settings;
import com.italankin.fifteen.Tile;
import com.italankin.fifteen.TileAppearAnimator;
import com.italankin.fifteen.views.FieldView;
import com.italankin.fifteen.views.overlay.FieldOverlay;

import java.util.List;

public class HelpOverlay extends FieldOverlay {

    private final Paint mPaintText;
    private final FieldView fieldView;
    private final String mTextHelp;
    private final RectF mRectHelp;
    private final float mHelpTextOffset;

    private Callback callback;

    public HelpOverlay(Resources res, TileAppearAnimator tileAppearAnimator, RectF rect, RectF fieldRect) {
        super(rect);
        this.fieldView = new FieldView(fieldRect);
        this.mTextHelp = res.getString(R.string.help_how_to_play);

        mPaintText = new Paint();
        mPaintText.setAntiAlias(Settings.antiAlias);
        mPaintText.setColor(Colors.getOverlayTextColor());
        mPaintText.setTypeface(Settings.typeface);
        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.setTextSize(Dimensions.interfaceFontSize * 1.4f);

        Rect r = new Rect();
        mPaintText.getTextBounds(mTextHelp, 0, mTextHelp.length(), r);
        mRectHelp = new RectF(r);
        float bottom = Dimensions.fieldMarginTop + Dimensions.fieldHeight + Dimensions.spacing;
        float whitespaceHeight = Dimensions.surfaceHeight - bottom;
        float top = whitespaceHeight / 2 - mRectHelp.height() / 2 + bottom;
        mRectHelp.offsetTo(Dimensions.surfaceWidth / 2 - mRectHelp.width() / 2, top);
        mHelpTextOffset = mRectHelp.bottom;
        float inset = -Dimensions.spacing * 2;
        mRectHelp.inset(inset, inset);

        List<Integer> goal = GameState.get().game.getGoal();
        for (int i = 0, size = goal.size(); i < size; i++) {
            int number = goal.get(i);
            if (number == 0) {
                continue;
            }
            Tile t = new Tile(number, i, true);
            tileAppearAnimator.animateTile(t, TileAppearAnimator.ANIM_TYPE_NUMBER_ASC_NO_GROUP);
            fieldView.addTile(t);
        }
    }

    @Override
    public boolean show() {
        if (mShow) {
            return true;
        }
        mAlpha = 1;
        return (mShow = true);
    }

    @Override
    public void draw(Canvas canvas, long elapsedTime) {
        if (!mShow) {
            return;
        }
        super.draw(canvas, elapsedTime);
        fieldView.draw(canvas, elapsedTime);
        canvas.drawText(mTextHelp, mRectHelp.centerX(), mHelpTextOffset, mPaintText);
    }

    @Override
    public void update() {
        super.update();
        fieldView.update();
        mPaintText.setAntiAlias(Settings.antiAlias);
        mPaintText.setColor(Colors.getOverlayTextColor());
    }

    @Override
    public boolean hide() {
        fieldView.clear();
        return super.hide();
    }

    public boolean onClick(float x, float y) {
        if (mRectHelp.contains(x, y)) {
            callback.onHowToPlayClick();
            return true;
        }
        return false;
    }

    public void addCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {

        void onHowToPlayClick();
    }
}
