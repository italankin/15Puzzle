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

public class AdvancedPage implements SettingsPage {

    private final SettingsView mParent;
    private final Paint mPaintText;
    private final Paint mPaintValue;

    private final String mTextHardMode;
    private final String[] mTextHardModeValues;
    private final String mTextMultiColor;
    private final String[] mTextMultiColorValues;
    private final String mTextAntiAlias;
    private final String[] mTextAntiAliasValues;
    private final String mTextNewGameDelay;
    private final String[] mTextNewGameDelayValues;
    private final String mTextIngameInfo;
    private final String mTextIngameInfoValue;
    private final String mTextStats;
    private final String mTextMissingTile;
    private final String[] mTextMissingTileValues;
    private final String[] mTextStatsValues;
    private final String mTextTileAnimSpeed;
    private final String mTextTileAnimSpeedOff;
    private final String mTextTileAnimSpeedFast;
    private final String mTextTileAnimSpeedNormal;

    private RectF mRectMultiColor;
    private RectF mRectAntiAlias;
    private RectF mRectNewGameDelay;
    private RectF mRectIngameInfo;
    private RectF mRectBf;
    private RectF mRectStats;
    private RectF mRectTileAnimSpeed;
    private RectF mRectMissingTile;

    private SettingsView.Callbacks mCallbacks;

    public AdvancedPage(SettingsView parent, Paint paintText, Paint paintValue, Resources res) {
        this.mParent = parent;
        this.mPaintText = paintText;
        this.mPaintValue = paintValue;

        mTextAntiAlias = res.getString(R.string.pref_anti_alias);
        mTextAntiAliasValues = res.getStringArray(R.array.toggle);
        mTextNewGameDelay = res.getString(R.string.pref_new_game_delay);
        mTextNewGameDelayValues = res.getStringArray(R.array.toggle);
        mTextIngameInfo = res.getString(R.string.pref_ingame_info);
        mTextIngameInfoValue = res.getString(R.string.pref_ingame_info_customize);
        mTextMultiColor = res.getString(R.string.pref_fringe);
        mTextMultiColorValues = res.getStringArray(R.array.multi_color_modes);
        mTextStats = res.getString(R.string.pref_stats);
        mTextTileAnimSpeed = res.getString(R.string.pref_tile_anim_speed);
        mTextTileAnimSpeedOff = res.getString(R.string.tile_anim_speed_off);
        mTextTileAnimSpeedFast = res.getString(R.string.tile_anim_speed_fast);
        mTextTileAnimSpeedNormal = res.getString(R.string.tile_anim_speed_normal);
        mTextHardMode = res.getString(R.string.pref_mode);
        mTextHardModeValues = res.getStringArray(R.array.difficulty_modes);
        mTextStatsValues = res.getStringArray(R.array.toggle);
        mTextMissingTile = res.getString(R.string.pref_missing_tile);
        mTextMissingTileValues = res.getStringArray(R.array.missing_tile_values);
    }

    @Override
    public void init(int lineSpacing, int topMargin, int padding, int textHeight) {
        topMargin += lineSpacing;
        mRectBf = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectBf.inset(0, padding);

        topMargin += lineSpacing;
        mRectMissingTile = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectMissingTile.inset(0, padding);

        topMargin += lineSpacing;
        mRectAntiAlias = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectAntiAlias.inset(0, padding);

        topMargin += lineSpacing;
        mRectMultiColor = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectMultiColor.inset(0, padding);

        topMargin += lineSpacing;
        mRectTileAnimSpeed = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectTileAnimSpeed.inset(0, padding);

        topMargin += lineSpacing;
        mRectNewGameDelay = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectNewGameDelay.inset(0, padding);

        topMargin += lineSpacing;
        mRectIngameInfo = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectIngameInfo.inset(0, padding);

        topMargin += lineSpacing;
        mRectStats = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectStats.inset(0, padding);
    }

    @Override
    public void draw(Canvas canvas, float valueRight, float textLeft, float textYOffset) {
        canvas.drawText(mTextHardMode, textLeft, mRectBf.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextHardModeValues[Settings.hardmode ? 1 : 0],
                valueRight, mRectBf.bottom - textYOffset, mPaintValue);

        canvas.drawText(mTextMultiColor, textLeft, mRectMultiColor.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextMultiColorValues[Settings.multiColor],
                valueRight, mRectMultiColor.bottom - textYOffset, mPaintValue);

        canvas.drawText(mTextAntiAlias, textLeft, mRectAntiAlias.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextAntiAliasValues[Settings.antiAlias ? 1 : 0],
                valueRight, mRectAntiAlias.bottom - textYOffset, mPaintValue);

        canvas.drawText(mTextNewGameDelay, textLeft, mRectNewGameDelay.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextNewGameDelayValues[Settings.newGameDelay ? 1 : 0],
                valueRight, mRectNewGameDelay.bottom - textYOffset, mPaintValue);

        canvas.drawText(mTextIngameInfo, textLeft, mRectIngameInfo.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextIngameInfoValue, valueRight, mRectIngameInfo.bottom - textYOffset, mPaintValue);

        canvas.drawText(mTextStats, textLeft, mRectStats.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextStatsValues[Settings.stats ? 1 : 0],
                valueRight, mRectStats.bottom - textYOffset, mPaintValue);

        canvas.drawText(mTextMissingTile, textLeft, mRectMissingTile.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextMissingTileValues[Settings.randomMissingTile ? 1 : 0],
                valueRight, mRectMissingTile.bottom - textYOffset, mPaintValue);

        int alpha = Settings.animations ? 255 : 128;
        mPaintText.setAlpha(alpha);
        canvas.drawText(mTextTileAnimSpeed, textLeft, mRectTileAnimSpeed.bottom - textYOffset, mPaintText);
        mPaintText.setAlpha(255);
        mPaintValue.setAlpha(alpha);
        canvas.drawText(tileAnimSpeed(), valueRight, mRectTileAnimSpeed.bottom - textYOffset, mPaintValue);
        mPaintValue.setAlpha(255);
    }

    @Override
    public void onClick(int x, int y, int dx) {
        if (mRectBf.contains(x, y)) {
            Settings.hardmode = !Settings.hardmode;
            Settings.save();
            if (mCallbacks != null) {
                mCallbacks.onSettingsChanged(true);
            }
        }

        if (mRectMultiColor.contains(x, y)) {
            if (dx < 0) {
                if (--Settings.multiColor < 0) {
                    Settings.multiColor += Constants.MULTI_COLOR_MODES;
                }
            } else {
                Settings.multiColor = (++Settings.multiColor % Constants.MULTI_COLOR_MODES);
            }
            Settings.save();
            if (mCallbacks != null) {
                mCallbacks.onSettingsChanged(false);
            }
        }

        if (mRectAntiAlias.contains(x, y)) {
            Settings.antiAlias = !Settings.antiAlias;
            Settings.save();
            if (mCallbacks != null) {
                mCallbacks.onSettingsChanged(false);
            }
        }

        if (mRectNewGameDelay.contains(x, y)) {
            Settings.newGameDelay = !Settings.newGameDelay;
            Settings.save();
        }

        if (mRectIngameInfo.contains(x, y)) {
            mParent.setCurrentPage(SettingsView.PAGE_INGAME_INFO);
        }

        if (mRectStats.contains(x, y)) {
            Settings.stats = !Settings.stats;
            Settings.save();
            if (mCallbacks != null) {
                mCallbacks.onSettingsChanged(false);
            }
        }

        if (mRectMissingTile.contains(x, y)) {
            Settings.randomMissingTile = !Settings.randomMissingTile;
            Settings.save();
            if (mCallbacks != null) {
                mCallbacks.onSettingsChanged(true);
            }
        }

        if (Settings.animations && mRectTileAnimSpeed.contains(x, y)) {
            for (int i = 0, s = Constants.ANIMATION_DURATION_ARRAY.length; i < s; i++) {
                if (Settings.tileAnimDuration == Constants.ANIMATION_DURATION_ARRAY[i]) {
                    Settings.tileAnimDuration = Constants.ANIMATION_DURATION_ARRAY[(i + 1) % s];
                    Settings.save();
                    break;
                }
            }
        }
    }

    @Override
    public void addCallback(SettingsView.Callbacks callbacks) {
        mCallbacks = callbacks;
    }

    @Override
    public void update() {
    }

    private String tileAnimSpeed() {
        if (!Settings.animations || Settings.tileAnimDuration == Constants.ANIMATION_DURATION_OFF) {
            return mTextTileAnimSpeedOff;
        }
        if (Settings.tileAnimDuration == Constants.ANIMATION_DURATION_FAST) {
            return mTextTileAnimSpeedFast;
        }
        return mTextTileAnimSpeedNormal;
    }
}
