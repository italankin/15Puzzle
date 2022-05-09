package com.italankin.fifteen.views;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.italankin.fifteen.Colors;
import com.italankin.fifteen.Constants;
import com.italankin.fifteen.Dimensions;
import com.italankin.fifteen.GameState;
import com.italankin.fifteen.R;
import com.italankin.fifteen.Settings;
import com.italankin.fifteen.Tools;

import java.util.Locale;

public class InfoPanelView extends BaseView {

    private final Paint mPaintBg;
    private final Paint mPaintTextValue;
    private final Paint mPaintTextCaption;
    private final Paint mPaintTextHelp;

    private final String[] mTextGameTypes;
    private final String mTextMoves;
    private final String mTextTime;
    private final String mTextTps;

    private final RectF mRectInfo;
    private final RectF mRectGameType;
    private final RectF mRectHelp;
    private final Rect mRectGameTypeTextBounds = new Rect();

    private final int mCaptionTextOffset;
    private final int mHelpTextOffset;

    private float firstRowY;
    private float secondRowY;
    private float thirdRowY;
    private float mGameTypeTextX;
    private float mGameTypeTextY;
    private Callbacks mCallbacks;
    private String mGameType;

    public InfoPanelView(Resources res) {
        mTextGameTypes = res.getStringArray(R.array.game_types);
        mTextMoves = res.getString(R.string.info_moves);
        mTextTime = res.getString(R.string.info_time);
        mTextTps = res.getString(R.string.info_tps);

        mPaintBg = new Paint();
        mPaintBg.setAntiAlias(Settings.antiAlias);
        mPaintBg.setColor(Colors.backgroundField);

        mPaintTextValue = new Paint();
        mPaintTextValue.setAntiAlias(Settings.antiAlias);
        mPaintTextValue.setTypeface(Settings.typeface);
        mPaintTextValue.setTextAlign(Paint.Align.LEFT);
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
        mRectGameType = new RectF(mRectInfo);
        mRectGameType.right = Dimensions.surfaceWidth * 0.5f;
        float inset = mRectInfo.height() / 4.5f;
        mRectGameType.inset(inset, inset);

        Rect tmp = new Rect();
        mPaintTextCaption.getTextBounds("A", 0, 1, tmp);
        mCaptionTextOffset = tmp.centerY();
        mPaintTextHelp.getTextBounds("?", 0, 1, tmp);
        mHelpTextOffset = tmp.centerY();

        mRectHelp = new RectF(0f, 0f, Dimensions.interfaceFontSize * 1.1f, Dimensions.interfaceFontSize * 1.1f);

        updateGameType();
        updateRows();

        mShow = true;
    }

    @Override
    public void draw(Canvas canvas, long elapsedTime) {
        if (!mShow) {
            return;
        }

        canvas.drawRect(mRectInfo, mPaintBg);

        canvas.drawText(mGameType, mGameTypeTextX, mGameTypeTextY, mPaintTextValue);

        int alpha = mPaintTextValue.getAlpha();
        mPaintTextValue.setAlpha(128);
        canvas.drawCircle(mRectHelp.centerX(), mRectHelp.centerY(), mRectHelp.width() / 2, mPaintTextValue);
        mPaintTextValue.setAlpha(alpha);
        canvas.drawText("?", mRectHelp.centerX(), mRectHelp.centerY() - mHelpTextOffset, mPaintTextHelp);

        GameState state = GameState.get();
        if (shouldShowInfo(Settings.ingameInfoMoves)) {
            prepareTitlePaint();
            canvas.drawText(mTextMoves, Dimensions.surfaceWidth / 2.0f, firstRowY, mPaintTextCaption);

            prepareValuePaint();
            canvas.drawText(Integer.toString(state.getMoves()),
                    Dimensions.surfaceWidth - Dimensions.spacing * 2.0f, firstRowY, mPaintTextCaption);
        }
        if (shouldShowInfo(Settings.ingameInfoTime)) {
            prepareTitlePaint();
            canvas.drawText(mTextTime, Dimensions.surfaceWidth / 2.0f, secondRowY, mPaintTextCaption);

            prepareValuePaint();
            String time = Tools.timeToString(Settings.timeFormat, state.time);
            canvas.drawText(time, Dimensions.surfaceWidth - Dimensions.spacing * 2.0f, secondRowY, mPaintTextCaption);
        }
        if (shouldShowInfo(Settings.ingameInfoTps)) {
            prepareTitlePaint();
            canvas.drawText(mTextTps, Dimensions.surfaceWidth / 2.0f, thirdRowY, mPaintTextCaption);

            prepareValuePaint();
            long time = state.time;
            int moves = state.getMoves();
            String tps;
            if (time == 0 || moves == 0) {
                tps = "0.000";
            } else {
                tps = Tools.formatFloat((float) moves / (float) time * 1000);
            }
            canvas.drawText(tps, Dimensions.surfaceWidth - Dimensions.spacing * 2.0f, thirdRowY, mPaintTextCaption);
        }
    }

    private String getGameType() {
        return mTextGameTypes[Settings.gameType].toUpperCase(Locale.getDefault()) + (Settings.hardmode ? "*" : "");
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

        updateGameType();
        updateRows();
    }

    public boolean onClick(float x, float y) {
        if (mRectGameType.contains(x, y)) {
            if (mCallbacks != null) {
                mCallbacks.onGameTypeClick();
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

    private void updateGameType() {
        mGameType = getGameType();
        mPaintTextValue.getTextBounds(mGameType, 0, mGameType.length(), mRectGameTypeTextBounds);

        float contentWidth = mRectGameTypeTextBounds.width() + mRectHelp.width() + Dimensions.spacing * 1.5f;
        float margin = (mRectGameType.width() - contentWidth) / 2;

        mGameTypeTextX = mRectGameType.left + margin;
        mGameTypeTextY = mRectGameType.centerY() - mRectGameTypeTextBounds.centerY();

        mRectHelp.offsetTo(
                mRectGameType.right - mRectHelp.width() - margin,
                mRectInfo.centerY() - mRectHelp.height() / 2);
    }

    private boolean shouldShowInfo(int info) {
        return info == Constants.INGAME_INFO_ON || info == Constants.INGAME_INFO_AFTER_SOLVE && GameState.get().isSolved();
    }

    public interface Callbacks {

        void onGameTypeClick();
    }
}
