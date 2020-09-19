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

import java.util.Locale;

public class InfoPanelView extends BaseView {

    private final Paint mPaintBg;
    private final Paint mPaintTextValue;
    private final Paint mPaintTextCaption;
    private final Paint mPaintTextHelp;

    private final String[] mTextMode;
    private final String mTextMoves;
    private final String mTextTime;
    private final String mTextTps;

    private final RectF mRectInfo;
    private final RectF mRectMode;
    private final RectF mRectHelp;

    private int mValueTextOffset;
    private float firstRowY;
    private float secondRowY;
    private float thirdRowY;
    private final int mCaptionTextOffset;
    private final int mHelpTextOffset;
    private Callbacks mCallbacks;

    public InfoPanelView(Resources res) {
        mTextMode = res.getStringArray(R.array.game_modes);
        mTextMoves = res.getString(R.string.info_moves);
        mTextTime = res.getString(R.string.info_time);
        mTextTps = res.getString(R.string.info_tps);

        mPaintBg = new Paint();
        mPaintBg.setAntiAlias(Settings.antiAlias);
        mPaintBg.setColor(Colors.backgroundField);

        mPaintTextValue = new Paint();
        mPaintTextValue.setAntiAlias(Settings.antiAlias);
        mPaintTextValue.setTypeface(Settings.typeface);
        mPaintTextValue.setTextAlign(Paint.Align.RIGHT);
        mPaintTextValue.setTextSize(Dimensions.interfaceFontSize * 1.4f);
        mPaintTextValue.setColor(Colors.getInfoTextColor());

        mPaintTextCaption = new Paint(mPaintTextValue);
        mPaintTextCaption.setTextSize(Dimensions.interfaceFontSize * 1.2f);
        mPaintTextCaption.setTextAlign(Paint.Align.LEFT);

        mPaintTextHelp = new Paint(mPaintTextCaption);
        mPaintTextHelp.setColor(Colors.backgroundField);
        mPaintTextHelp.setTextAlign(Paint.Align.CENTER);
        mPaintTextHelp.setTextSize(Dimensions.interfaceFontSize);

        mRectInfo = new RectF(0.0f, Dimensions.infoBarMarginTop,
                Dimensions.surfaceWidth, Dimensions.infoBarMarginTop + Dimensions.infoBarHeight);
        mRectMode = new RectF(mRectInfo);
        mRectMode.right = Dimensions.surfaceWidth * 0.5f;

        mRectHelp = new RectF(0f, 0f,
                Dimensions.interfaceFontSize * 1.1f, Dimensions.interfaceFontSize * 1.1f);
        mRectHelp.offsetTo(Dimensions.surfaceWidth * 0.45f - mRectHelp.width(), mRectInfo.centerY() - mRectHelp.height() / 2);

        Rect tmp = new Rect();
        mPaintTextValue.getTextBounds(getGameMode(), 0, 1, tmp);
        mValueTextOffset = tmp.centerY();
        float inset = (float) tmp.width() / 2 - mValueTextOffset;
        mRectMode.inset(inset, inset);
        mPaintTextCaption.getTextBounds("A", 0, 1, tmp);
        mCaptionTextOffset = tmp.centerY();
        mPaintTextHelp.getTextBounds("?", 0, 1, tmp);
        mHelpTextOffset = tmp.centerY();

        updateRows();

        mShow = true;
    }

    @Override
    public void draw(Canvas canvas, long elapsedTime) {
        if (!mShow) {
            return;
        }

        canvas.drawRect(mRectInfo, mPaintBg);

        canvas.drawText(getGameMode(), mRectHelp.left - Dimensions.spacing, mRectInfo.centerY() - mValueTextOffset,
                mPaintTextValue);

        int alpha = mPaintTextValue.getAlpha();
        mPaintTextValue.setAlpha(128);
        canvas.drawCircle(mRectHelp.centerX(), mRectHelp.centerY(), mRectHelp.width() / 2, mPaintTextValue);
        mPaintTextValue.setAlpha(alpha);
        canvas.drawText("?", mRectHelp.centerX(), mRectHelp.centerY() - mHelpTextOffset, mPaintTextHelp);

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

    private String getGameMode() {
        return mTextMode[Settings.gameMode].toUpperCase(Locale.getDefault()) + (Settings.hardmode ? "*" : "");
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
        mPaintTextHelp.setAntiAlias(Settings.antiAlias);

        mPaintTextValue.setColor(Colors.getInfoTextColor());
        mPaintTextCaption.setColor(Colors.getInfoTextColor());

        updateRows();
    }

    public boolean onClick(float x, float y) {
        if (mRectMode.contains(x, y)) {
            if (mCallbacks != null) {
                mCallbacks.onModeClick();
            }
            return true;
        }
        return false;
    }

    public void addCallback(Callbacks callbacks) {
        mCallbacks = callbacks;
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

    public interface Callbacks {

        void onModeClick();
    }
}
