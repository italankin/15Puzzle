package com.italankin.fifteen.views;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.italankin.fifteen.Colors;
import com.italankin.fifteen.Constants;
import com.italankin.fifteen.Dimensions;
import com.italankin.fifteen.Game;
import com.italankin.fifteen.R;
import com.italankin.fifteen.Settings;
import com.italankin.fifteen.Tools;

public class InfoPanelView extends BaseView {

    private Paint mPaintBg;
    private Paint mPaintTextValue;
    private Paint mPaintTextCaption;

    private String[] mTextMode;
    private String mTextMoves;
    private String mTextTime;

    private RectF mRectInfo;

    private int mValueTextOffset;
    private final float firstRowY;
    private final float secondRowY;

    public InfoPanelView(Resources res) {
        mPaintBg = new Paint();
        mPaintBg.setAntiAlias(Settings.antiAlias);
        mPaintBg.setColor(Colors.backgroundField);

        mPaintTextValue = new Paint();
        mPaintTextValue.setAntiAlias(Settings.antiAlias);
        mPaintTextValue.setTypeface(Settings.typeface);
        mPaintTextValue.setTextAlign(Paint.Align.CENTER);
        mPaintTextValue.setTextSize(Dimensions.interfaceFontSize * 1.4f);
        mPaintTextValue.setColor(Colors.getInfoTextColor());

        mPaintTextCaption = new Paint(mPaintTextValue);
        mPaintTextCaption.setTextSize(Dimensions.interfaceFontSize * 1.2f);
        mPaintTextCaption.setTextAlign(Paint.Align.LEFT);

        mRectInfo = new RectF(0.0f, Dimensions.infoBarMarginTop,
                Dimensions.surfaceWidth, Dimensions.infoBarMarginTop + Dimensions.infoBarHeight);

        Rect r = new Rect();
        mPaintTextValue.getTextBounds("A", 0, 1, r);
        mValueTextOffset = r.centerY();
        mPaintTextCaption.getTextBounds("A", 0, 1, r);
        int mCaptionTextOffset = r.centerY();

        firstRowY = mRectInfo.top + mRectInfo.height() * 0.3f - mCaptionTextOffset;
        secondRowY = mRectInfo.top + mRectInfo.height() * 0.7f - mCaptionTextOffset;

        mTextMode = res.getStringArray(R.array.game_modes);
        mTextMoves = res.getString(R.string.info_moves);
        mTextTime = res.getString(R.string.info_time);

        mShow = true;
    }

    @Override
    public void draw(Canvas canvas, long elapsedTime) {
        if (!mShow) {
            return;
        }

        canvas.drawRoundRect(mRectInfo, Dimensions.tileCornerRadius, Dimensions.tileCornerRadius,
                mPaintBg);

        canvas.drawText(
                mTextMode[Settings.gameMode].toUpperCase() + (Settings.hardmode ? "*" : ""),
                Dimensions.surfaceWidth * 0.25f, mRectInfo.centerY() - mValueTextOffset,
                mPaintTextValue);

        if (shouldShowInfo(Constants.INGAME_INFO_MOVES)) {
            prepareTitlePaint();
            canvas.drawText(mTextMoves, Dimensions.surfaceWidth / 2.0f, firstRowY, mPaintTextCaption);

            prepareValuePaint();
            canvas.drawText(Integer.toString(Game.getMoves()),
                    Dimensions.surfaceWidth - Dimensions.spacing * 2.0f, firstRowY, mPaintTextCaption);
        }
        if (shouldShowInfo(Constants.INGAME_INFO_TIME)) {
            prepareTitlePaint();
            canvas.drawText(mTextTime, Dimensions.surfaceWidth / 2.0f, secondRowY, mPaintTextCaption);

            prepareValuePaint();
            String time = Tools.timeToString(Settings.timeFormat, Game.getTime());
            canvas.drawText(time, Dimensions.surfaceWidth - Dimensions.spacing * 2.0f, secondRowY, mPaintTextCaption);
        }
    }

    private void prepareTitlePaint() {
        mPaintTextCaption.setColor(Colors.getTileTextColor());
        mPaintTextCaption.setTextAlign(Paint.Align.LEFT);
    }

    private void prepareValuePaint() {
        mPaintTextCaption.setColor(Colors.getInfoTextColor());
        mPaintTextCaption.setTextAlign(Paint.Align.RIGHT);
    }

    private static boolean shouldShowInfo(int infoMoves) {
        return Game.isNotStarted() || Game.isSolved() || (Settings.ingameInfo & infoMoves) == infoMoves;
    }

    @Override
    public void update() {
        mPaintTextValue.setColor(Colors.getInfoTextColor());
        mPaintTextValue.setAntiAlias(Settings.antiAlias);
        mPaintBg.setColor(Colors.backgroundField);
        mPaintTextCaption.setAntiAlias(Settings.antiAlias);
        mPaintBg.setAntiAlias(Settings.antiAlias);
    }
}
