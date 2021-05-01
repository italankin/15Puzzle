package com.italankin.fifteen.views.settings;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.italankin.fifteen.Constants;
import com.italankin.fifteen.Dimensions;
import com.italankin.fifteen.R;
import com.italankin.fifteen.Settings;
import com.italankin.fifteen.views.SettingsView;

public class IngameInfoPage implements SettingsPage {

    private final Paint mPaintText;
    private final Paint mPaintValue;

    private final String[] mTextValues;

    private final String mTextMoves;
    private final String mTextTime;
    private final String mTextTps;

    private final String mTextTimeFormat;
    private final String[] mTextTimeFormatValues;

    private RectF mRectMoves;
    private RectF mRectTime;
    private RectF mRectTps;
    private RectF mRectTimeFormat;

    private SettingsView.Callbacks mCallbacks;

    public IngameInfoPage(Paint paintText, Paint paintValue, Resources res) {
        this.mPaintText = paintText;
        this.mPaintValue = paintValue;

        mTextValues = res.getStringArray(R.array.ingame_info);
        mTextMoves = res.getString(R.string.pref_ingame_info_moves);
        mTextTime = res.getString(R.string.pref_ingame_info_time);
        mTextTps = res.getString(R.string.pref_ingame_info_tps);
        mTextTimeFormat = res.getString(R.string.pref_time_format);
        mTextTimeFormatValues = res.getStringArray(R.array.time_format);
    }

    @Override
    public void init(int lineSpacing, int topMargin, int padding, int textHeight) {
        topMargin += lineSpacing;
        mRectTimeFormat = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectTimeFormat.inset(0, padding);

        topMargin += lineSpacing;
        mRectMoves = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectMoves.inset(0, padding);

        topMargin += lineSpacing;
        mRectTime = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectTime.inset(0, padding);

        topMargin += lineSpacing;
        mRectTps = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectTps.inset(0, padding);
    }

    @Override
    public void draw(Canvas canvas, float valueRight, float textLeft, float textYOffset) {
        canvas.drawText(mTextTimeFormat, textLeft, mRectTimeFormat.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextTimeFormatValues[Settings.timeFormat],
                valueRight, mRectTimeFormat.bottom - textYOffset, mPaintValue);

        canvas.drawText(mTextMoves, textLeft, mRectMoves.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextValues[Settings.ingameInfoMoves], valueRight, mRectMoves.bottom - textYOffset, mPaintValue);

        canvas.drawText(mTextTime, textLeft, mRectTime.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextValues[Settings.ingameInfoTime], valueRight, mRectTime.bottom - textYOffset, mPaintValue);

        canvas.drawText(mTextTps, textLeft, mRectTps.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextValues[Settings.ingameInfoTps], valueRight, mRectTps.bottom - textYOffset, mPaintValue);
    }

    @Override
    public void onClick(int x, int y, int dx) {
        if (mRectTimeFormat.contains(x, y)) {
            Settings.timeFormat = (++Settings.timeFormat % Constants.TIME_FORMATS);
            Settings.save();
        }
        if (mRectMoves.contains(x, y)) {
            Settings.ingameInfoMoves = (++Settings.ingameInfoMoves % Constants.INGAME_INFO_VALUES);
            Settings.save();
            if (mCallbacks != null) {
                mCallbacks.onSettingsChanged(false);
            }
        }
        if (mRectTime.contains(x, y)) {
            Settings.ingameInfoTime = (++Settings.ingameInfoTime % Constants.INGAME_INFO_VALUES);
            Settings.save();
            if (mCallbacks != null) {
                mCallbacks.onSettingsChanged(false);
            }
        }
        if (mRectTps.contains(x, y)) {
            Settings.ingameInfoTps = (++Settings.ingameInfoTps % Constants.INGAME_INFO_VALUES);
            Settings.save();
            if (mCallbacks != null) {
                mCallbacks.onSettingsChanged(false);
            }
        }
    }

    @Override
    public void update() {
    }

    @Override
    public void addCallback(SettingsView.Callbacks callbacks) {
        mCallbacks = callbacks;
    }
}
