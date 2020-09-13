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
    private String mTextTps;

    private RectF mRectInfo;

    private int mValueTextOffset;
    private float firstRowY;
    private float secondRowY;
    private float thirdRowY;
    private final int mCaptionTextOffset;

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
        mCaptionTextOffset = r.centerY();

        updateRows();

        mTextMode = res.getStringArray(R.array.game_modes);
        mTextMoves = res.getString(R.string.info_moves);
        mTextTime = res.getString(R.string.info_time);
        mTextTps = res.getString(R.string.info_tps);

        mShow = true;
    }

    @Override
    public void draw(Canvas canvas, long elapsedTime) {
        if (!mShow) {
            return;
        }

        canvas.drawRect(mRectInfo, mPaintBg);

        canvas.drawText(
                mTextMode[Settings.gameMode].toUpperCase() + (Settings.hardmode ? "*" : ""),
                Dimensions.surfaceWidth * 0.25f, mRectInfo.centerY() - mValueTextOffset,
                mPaintTextValue);

        if (shouldShowInfo(Settings.ingameInfoMoves)) {
            prepareTitlePaint();
            canvas.drawText(mTextMoves, Dimensions.surfaceWidth / 2.0f, firstRowY, mPaintTextCaption);

            prepareValuePaint();
            canvas.drawText(Integer.toString(Game.getMoves()),
                    Dimensions.surfaceWidth - Dimensions.spacing * 2.0f, firstRowY, mPaintTextCaption);
        }
        if (shouldShowInfo(Settings.ingameInfoTime)) {
            prepareTitlePaint();
            canvas.drawText(mTextTime, Dimensions.surfaceWidth / 2.0f, secondRowY, mPaintTextCaption);

            prepareValuePaint();
            String time = Tools.timeToString(Settings.timeFormat, Game.getTime());
            canvas.drawText(time, Dimensions.surfaceWidth - Dimensions.spacing * 2.0f, secondRowY, mPaintTextCaption);
        }
        if (shouldShowInfo(Settings.ingameInfoTps)) {
            prepareTitlePaint();
            canvas.drawText(mTextTps, Dimensions.surfaceWidth / 2.0f, thirdRowY, mPaintTextCaption);

            prepareValuePaint();
            long time = Game.getTime();
            int moves = Game.getMoves();
            String tps;
            if (time == 0 || moves == 0) {
                tps = "0.000";
            } else {
                tps = Tools.formatFloat((float) moves / (float) time * 1000);
            }
            canvas.drawText(tps, Dimensions.surfaceWidth - Dimensions.spacing * 2.0f, thirdRowY, mPaintTextCaption);
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

    @Override
    public void update() {
        mPaintTextValue.setAntiAlias(Settings.antiAlias);
        mPaintTextCaption.setAntiAlias(Settings.antiAlias);
        mPaintBg.setAntiAlias(Settings.antiAlias);

        mPaintTextValue.setColor(Colors.getInfoTextColor());
        mPaintTextCaption.setColor(Colors.getInfoTextColor());

        updateRows();
    }

    private void updateRows() {
        if (Settings.ingameInfoTps == Constants.INGAME_INFO_OFF) {
            firstRowY = mRectInfo.top + mRectInfo.height() * 0.3f - mCaptionTextOffset;
            secondRowY = mRectInfo.top + mRectInfo.height() * 0.7f - mCaptionTextOffset;
        } else {
            firstRowY = mRectInfo.top + mRectInfo.height() * 0.2f - mCaptionTextOffset;
            secondRowY = mRectInfo.top + mRectInfo.height() * 0.5f - mCaptionTextOffset;
            thirdRowY = mRectInfo.top + mRectInfo.height() * 0.8f - mCaptionTextOffset;
        }
    }

    private boolean shouldShowInfo(int info) {
        return info == Constants.INGAME_INFO_ON || info == Constants.INGAME_INFO_AFTER_SOLVE && Game.isSolved();
    }
}
