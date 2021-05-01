package com.italankin.fifteen.views.settings;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.italankin.fifteen.Colors;
import com.italankin.fifteen.Constants;
import com.italankin.fifteen.Dimensions;
import com.italankin.fifteen.R;
import com.italankin.fifteen.Settings;
import com.italankin.fifteen.views.SettingsView;

public class BasicPage implements SettingsPage {

    private final Paint mPaintText;
    private final Paint mPaintValue;
    private final Paint mPaintIcon;

    private String mTextWidth;
    private String mTextWidthValue;
    private String mTextHeight;
    private String mTextHeightValue;
    private String mTextAnimations;
    private String[] mTextAnimationsValues;
    private String mTextColor;
    private String mTextColorMode;
    private String[] mTextColorModeValues;
    private String mTextType;
    private String[] mTextTypeValue;

    private RectF mRectWidth;
    private RectF mRectAnimations;
    private RectF mRectGameType;
    private RectF mRectHeight;
    private RectF mRectColor;
    private RectF mRectColorMode;
    private RectF mRectColorIcon;

    private SettingsView.Callbacks mCallbacks;

    public BasicPage(Paint paintText, Paint paintValue, Resources res) {
        this.mPaintText = paintText;
        this.mPaintValue = paintValue;

        mPaintIcon = new Paint();
        mPaintIcon.setAntiAlias(Settings.antiAlias);

        mTextHeight = res.getString(R.string.pref_height);
        mTextHeightValue = Integer.toString(Settings.gameHeight);
        mTextWidth = res.getString(R.string.pref_width);
        mTextWidthValue = Integer.toString(Settings.gameWidth);
        mTextType = res.getString(R.string.pref_type);
        mTextTypeValue = res.getStringArray(R.array.game_types);
        mTextAnimations = res.getString(R.string.pref_animation);
        mTextAnimationsValues = res.getStringArray(R.array.toggle);
        mTextColorMode = res.getString(R.string.pref_color_mode);
        mTextColorModeValues = res.getStringArray(R.array.color_mode);
        mTextColor = res.getString(R.string.pref_color);
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
    }

    @Override
    public void draw(Canvas canvas, float valueRight, float textLeft, float textYOffset) {
        mTextWidthValue = Integer.toString(Settings.gameWidth);
        mTextHeightValue = Integer.toString(Settings.gameHeight);

        canvas.drawText(mTextWidth, textLeft, mRectWidth.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextWidthValue, valueRight, mRectWidth.bottom - textYOffset, mPaintValue);

        canvas.drawText(mTextHeight, textLeft, mRectHeight.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextHeightValue, valueRight, mRectHeight.bottom - textYOffset, mPaintValue);

        canvas.drawText(mTextAnimations, textLeft, mRectAnimations.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextAnimationsValues[Settings.animations ? 1 : 0],
                valueRight, mRectAnimations.bottom - textYOffset, mPaintValue);

        canvas.drawText(mTextColor, textLeft, mRectColor.bottom - textYOffset, mPaintText);
        mPaintIcon.setColor(Colors.getTileColor());
        canvas.drawRect(mRectColorIcon, mPaintIcon);

        canvas.drawText(mTextColorMode, textLeft, mRectColorMode.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextColorModeValues[Settings.colorMode],
                valueRight, mRectColorMode.bottom - textYOffset, mPaintValue);

        canvas.drawText(mTextType, textLeft, mRectGameType.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextTypeValue[Settings.gameType],
                valueRight, mRectGameType.bottom - textYOffset, mPaintValue);
    }

    @Override
    public void onClick(int x, int y, int dx) {
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
            Settings.animations = !Settings.animations;
            Settings.save();
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
    }

    @Override
    public void update() {
        mPaintIcon.setAntiAlias(Settings.antiAlias);
    }
}
