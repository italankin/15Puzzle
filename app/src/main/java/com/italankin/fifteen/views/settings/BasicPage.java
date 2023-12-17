package com.italankin.fifteen.views.settings;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import com.italankin.fifteen.*;
import com.italankin.fifteen.views.SettingsView;

public class BasicPage implements SettingsPage {

    private final Context mContext;
    private final Paint mPaintText;
    private final Paint mPaintValue;
    private final Paint mPaintIcon;

    private final String mTextWidth;
    private String mTextWidthValue;
    private final String mTextHeight;
    private String mTextHeightValue;
    private final String mTextTileAnimation;
    private final String mTextTileAnimationOff;
    private final String mTextTileAnimationFast;
    private final String mTextTileAnimationNormal;
    private final String mTextColor;
    private final String mTextColorMode;
    private final String[] mTextColorModeValues;
    private final String mTextType;
    private final String[] mTextTypeValue;
    private final String mTextNewApp;
    private final String mTextNewAppValue;

    private RectF mRectWidth;
    private RectF mRectAnimations;
    private RectF mRectGameType;
    private RectF mRectHeight;
    private RectF mRectColor;
    private RectF mRectColorMode;
    private RectF mRectColorIcon;
    private RectF mRectNewApp;

    private SettingsView.Callbacks mCallbacks;

    public BasicPage(Context context, Paint paintText, Paint paintValue) {
        this.mContext = context;
        this.mPaintText = paintText;
        this.mPaintValue = paintValue;

        mPaintIcon = new Paint();
        mPaintIcon.setAntiAlias(Settings.antiAlias);

        Resources res = context.getResources();
        mTextHeight = res.getString(R.string.pref_height);
        mTextHeightValue = Integer.toString(Settings.gameHeight);
        mTextWidth = res.getString(R.string.pref_width);
        mTextWidthValue = Integer.toString(Settings.gameWidth);
        mTextType = res.getString(R.string.pref_type);
        mTextTypeValue = res.getStringArray(R.array.game_types);
        mTextTileAnimation = res.getString(R.string.pref_animation);
        mTextTileAnimationOff = res.getString(R.string.tile_anim_speed_off);
        mTextTileAnimationFast = res.getString(R.string.tile_anim_speed_fast);
        mTextTileAnimationNormal = res.getString(R.string.tile_anim_speed_normal);
        mTextColorMode = res.getString(R.string.pref_color_mode);
        mTextColorModeValues = res.getStringArray(R.array.color_mode);
        mTextColor = res.getString(R.string.pref_color);
        mTextNewApp = res.getString(R.string.pref_new_app);
        mTextNewAppValue = res.getString(R.string.pref_new_app_value);
    }

    public void addCallback(SettingsView.Callbacks callbacks) {
        this.mCallbacks = callbacks;
    }

    @Override
    public void init(int lineSpacing, int topMargin, int padding, int textHeight) {
        topMargin += lineSpacing;
        mRectGameType = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectGameType.inset(0, padding);

        topMargin += lineSpacing;
        mRectWidth = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectWidth.inset(0, padding);

        topMargin += lineSpacing;
        mRectHeight = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectHeight.inset(0, padding);

        topMargin += lineSpacing;
        mRectAnimations = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectAnimations.inset(0, padding);

        topMargin += lineSpacing;
        mRectColorMode = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectColorMode.inset(0, padding);

        topMargin += lineSpacing;
        mRectColor = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectColor.inset(0, padding);
        mRectColorIcon = new RectF(Dimensions.surfaceWidth / 2 + 2.0f * Dimensions.spacing,
                mRectColor.top - padding,
                Dimensions.surfaceWidth / 2 + 2.0f * Dimensions.spacing + textHeight,
                mRectColor.bottom + padding);
        mRectColorIcon.inset(-mRectColorIcon.width() / 4, -mRectColorIcon.width() / 4);

        topMargin += lineSpacing;
        mRectNewApp = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectNewApp.inset(0, padding);
    }

    @Override
    public void draw(Canvas canvas, float valueRight, float textLeft, float textYOffset) {
        mTextWidthValue = Integer.toString(Settings.gameWidth);
        mTextHeightValue = Integer.toString(Settings.gameHeight);

        canvas.drawText(mTextWidth, textLeft, mRectWidth.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextWidthValue, valueRight, mRectWidth.bottom - textYOffset, mPaintValue);

        canvas.drawText(mTextHeight, textLeft, mRectHeight.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextHeightValue, valueRight, mRectHeight.bottom - textYOffset, mPaintValue);

        canvas.drawText(mTextTileAnimation, textLeft, mRectAnimations.bottom - textYOffset, mPaintText);
        canvas.drawText(tileAnimSpeed(), valueRight, mRectAnimations.bottom - textYOffset, mPaintValue);

        canvas.drawText(mTextColor, textLeft, mRectColor.bottom - textYOffset, mPaintText);
        mPaintIcon.setColor(Colors.getTileColor());
        canvas.drawRect(mRectColorIcon, mPaintIcon);

        canvas.drawText(mTextColorMode, textLeft, mRectColorMode.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextColorModeValues[Settings.colorMode],
                valueRight, mRectColorMode.bottom - textYOffset, mPaintValue);

        canvas.drawText(mTextType, textLeft, mRectGameType.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextTypeValue[Settings.gameType],
                valueRight, mRectGameType.bottom - textYOffset, mPaintValue);

        canvas.drawText(mTextNewApp, textLeft, mRectNewApp.bottom - textYOffset, mPaintText);
        int valueColor = mPaintValue.getColor();
        mPaintValue.setColor(Colors.NEW_APP);
        canvas.drawText(mTextNewAppValue, valueRight, mRectNewApp.bottom - textYOffset, mPaintValue);
        mPaintValue.setColor(valueColor);
    }

    @Override
    public void onClick(float x, float y, float dx) {
        if (mRectWidth.contains(x, y)) {
            Settings.gameWidth += ((dx == 0) ? 1 : Math.signum(dx));
            if (Settings.gameWidth < Constants.MIN_GAME_WIDTH) {
                Settings.gameWidth = Constants.MAX_GAME_WIDTH;
            }
            if (Settings.gameWidth > Constants.MAX_GAME_WIDTH) {
                Settings.gameWidth = Constants.MIN_GAME_WIDTH;
            }
            Settings.save();
            if (mCallbacks != null) {
                mCallbacks.onSettingsChanged(true);
            }
        }

        if (mRectHeight.contains(x, y)) {
            Settings.gameHeight += ((dx == 0) ? 1 : Math.signum(dx));
            if (Settings.gameHeight < Constants.MIN_GAME_HEIGHT) {
                Settings.gameHeight = Constants.MAX_GAME_HEIGHT;
            }
            if (Settings.gameHeight > Constants.MAX_GAME_HEIGHT) {
                Settings.gameHeight = Constants.MIN_GAME_HEIGHT;
            }
            Settings.save();
            if (mCallbacks != null) {
                mCallbacks.onSettingsChanged(true);
            }
        }

        if (mRectAnimations.contains(x, y)) {
            for (int i = 0, s = Constants.ANIMATION_SPEED_ARRAY.length; i < s; i++) {
                if (Settings.animationSpeed == Constants.ANIMATION_SPEED_ARRAY[i]) {
                    Settings.animationSpeed = Constants.ANIMATION_SPEED_ARRAY[(i + 1) % s];
                    Settings.save();
                    break;
                }
            }
        }

        if (mRectColor.contains(x, y)) {
            int totalColors = Colors.getTileColors().length;
            if (dx < 0) {
                if (--Settings.tileColor < 0) {
                    Settings.tileColor += totalColors;
                }
            } else {
                Settings.tileColor = (++Settings.tileColor % totalColors);
            }
            Settings.save();
            if (mCallbacks != null) {
                mCallbacks.onSettingsChanged(false);
            }
        }

        if (mRectColorMode.contains(x, y)) {
            Settings.colorMode = (++Settings.colorMode % Constants.COLOR_MODES);
            Settings.save();
            if (mCallbacks != null) {
                mCallbacks.onSettingsChanged(false);
            }
        }

        if (mRectGameType.contains(x, y)) {
            Settings.gameType = (++Settings.gameType % Constants.GAME_TYPES);
            Settings.save();
            if (mCallbacks != null) {
                mCallbacks.onSettingsChanged(true);
            }
        }

        if (mRectNewApp.contains(x, y)) {
            Tools.openUrl(mContext, R.string.fifteen_app_url);
        }
    }

    @Override
    public void update() {
        mPaintIcon.setAntiAlias(Settings.antiAlias);
    }

    private String tileAnimSpeed() {
        if (!Settings.animationsEnabled()) {
            return mTextTileAnimationOff;
        }
        if (Settings.animationSpeed == Constants.ANIMATION_SPEED_FAST) {
            return mTextTileAnimationFast;
        }
        return mTextTileAnimationNormal;
    }
}
