package com.italankin.fifteen.views;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.italankin.fifteen.Colors;
import com.italankin.fifteen.Constants;
import com.italankin.fifteen.Dimensions;
import com.italankin.fifteen.R;
import com.italankin.fifteen.Settings;

public class SettingsView extends BaseView {

    private static final int PAGE_COUNT = 2;
    private static final int PAGE_BASIC = 0;
    private static final int PAGE_ADVANCED = 1;

    private Paint mPaintText;
    private Paint mPaintValue;
    private Paint mPaintControls;
    private Paint mPaintIcon;

    private String mTextWidth;
    private String mTextWidthValue;
    private String mTextHeight;
    private String mTextHeightValue;
    private String mTextHardMode;
    private String[] mTextHardModeValues;
    private String mTextAnimations;
    private String[] mTextAnimationsValues;
    private String mTextColor;
    private String mTextMultiColor;
    private String[] mTextMultiColorValues;
    private String mTextColorMode;
    private String[] mTextColorModeValues;
    private String mTextMode;
    private String[] mTextModeValue;
    private String mTextBack;
    private String[] mTextSettingsPage;
    private String mTextAntiAlias;
    private String[] mTextAntiAliasValues;
    private String mTextNewGameDelay;
    private String[] mTextNewGameDelayValues;
    private String mTextIngameInfo;
    private String[] mTextIngameInfoValues;
    private String mTextTimeFormat;
    private String[] mTextTimeFormatValues;
    private String mTextStats;
    private String[] mTextStatsValues;

    private RectF mRectWidth;
    private RectF mRectHeight;
    private RectF mRectBf;
    private RectF mRectColor;
    private RectF mRectMultiColor;
    private RectF mRectColorMode;
    private RectF mRectColorIcon;
    private RectF mRectAnimations;
    private RectF mRectMode;
    private RectF mRectAntiAlias;
    private RectF mRectNewGameDelay;
    private RectF mRectIngameInfo;
    private RectF mRectTimeFormat;
    private RectF mRectStats;
    private RectF mRectSettingsPage;
    private RectF mRectBack;

    private Callbacks mCallbacks;
    private int mPage = PAGE_BASIC;

    public SettingsView(Resources res) {
        int lineSpacing = (int) (Dimensions.surfaceHeight * 0.082f);
        int topMargin = (int) (Dimensions.surfaceHeight * 0.07f);
        int padding = -lineSpacing / 4;

        mPaintText = new Paint();
        mPaintText.setAntiAlias(Settings.antiAlias);
        mPaintText.setColor(Colors.getOverlayTextColor());
        mPaintText.setTextSize(Dimensions.menuFontSize);
        mPaintText.setTypeface(Settings.typeface);
        mPaintText.setTextAlign(Paint.Align.RIGHT);

        mPaintValue = new Paint();
        mPaintValue.setAntiAlias(Settings.antiAlias);
        mPaintValue.setColor(Colors.menuTextValue);
        mPaintValue.setTextSize(Dimensions.menuFontSize);
        mPaintValue.setTypeface(Settings.typeface);
        mPaintValue.setTextAlign(Paint.Align.LEFT);

        mPaintControls = new Paint(mPaintText);
        mPaintControls.setTextAlign(Paint.Align.CENTER);

        mPaintIcon = new Paint();
        mPaintIcon.setAntiAlias(Settings.antiAlias);

        mTextBack = res.getString(R.string.back);
        mTextSettingsPage = res.getStringArray(R.array.settings_pages);

        mTextHeight = res.getString(R.string.pref_height);
        mTextHeightValue = Integer.toString(Settings.gameHeight);
        mTextWidth = res.getString(R.string.pref_width);
        mTextWidthValue = Integer.toString(Settings.gameWidth);
        mTextMode = res.getString(R.string.pref_mode);
        mTextModeValue = res.getStringArray(R.array.game_modes);
        mTextHardMode = res.getString(R.string.pref_bf);
        mTextHardModeValues = res.getStringArray(R.array.difficulty_modes);
        mTextAnimations = res.getString(R.string.pref_animation);
        mTextAnimationsValues = res.getStringArray(R.array.toggle);
        mTextMultiColor = res.getString(R.string.pref_fringe);
        mTextMultiColorValues = res.getStringArray(R.array.multi_color_modes);
        mTextColorMode = res.getString(R.string.pref_color_mode);
        mTextColorModeValues = res.getStringArray(R.array.color_mode);
        mTextColor = res.getString(R.string.pref_color);
        mTextAntiAlias = res.getString(R.string.pref_anti_alias);
        mTextAntiAliasValues = res.getStringArray(R.array.toggle);
        mTextNewGameDelay = res.getString(R.string.pref_new_game_delay);
        mTextNewGameDelayValues = res.getStringArray(R.array.toggle);
        mTextIngameInfo = res.getString(R.string.pref_ingame_info);
        mTextIngameInfoValues = res.getStringArray(R.array.ingame_info);
        mTextTimeFormat = res.getString(R.string.pref_time_format);
        mTextTimeFormatValues = res.getStringArray(R.array.time_format);
        mTextStats = res.getString(R.string.pref_stats);
        mTextStatsValues = res.getStringArray(R.array.toggle);

        Rect r = new Rect();
        mPaintText.getTextBounds(mTextWidth, 0, mTextWidth.length(), r);
        int textHeight = r.height();

        initBasicPage(lineSpacing, topMargin, padding, textHeight);
        initAdvancedPage(lineSpacing, topMargin, padding, textHeight);

        mRectSettingsPage = new RectF(0, Dimensions.surfaceHeight - lineSpacing * 1.5f - textHeight * 2,
                Dimensions.surfaceWidth, Dimensions.surfaceHeight - lineSpacing * 1.5f - textHeight);
        mRectSettingsPage.inset(0, padding);

        mRectBack = new RectF(0, Dimensions.surfaceHeight - lineSpacing - textHeight,
                Dimensions.surfaceWidth, Dimensions.surfaceHeight - lineSpacing);
        mRectBack.inset(0, padding);
    }

    @Override
    public boolean show() {
        mPage = PAGE_BASIC;
        return super.show();
    }

    public void onClick(int x, int y, int dx) {
        if (Math.abs(dx) < 15) {
            dx = 0;
        }

        switch (mPage) {
            case PAGE_BASIC:
                onClickBasic(x, y, dx);
                break;
            case PAGE_ADVANCED:
                onClickAdvanced(x, y, dx);
                break;
        }

        if (mRectSettingsPage.contains(x, y)) {
            mPage = (mPage + 1) % PAGE_COUNT;
        }

        if (mRectBack.contains(x, y)) {
            hide();
        }
    }

    @Override
    public void draw(Canvas canvas, long elapsedTime) {
        if (!mShow) {
            return;
        }

        float valueRight = Dimensions.surfaceWidth / 2 + Dimensions.spacing;
        float textLeft = Dimensions.surfaceWidth / 2 - Dimensions.spacing;
        float textYOffset = (int) (Dimensions.surfaceHeight * 0.02f);

        canvas.drawColor(Colors.getOverlayColor());

        switch (mPage) {
            case PAGE_BASIC:
                drawBasic(canvas, valueRight, textLeft, textYOffset);
                break;
            case PAGE_ADVANCED:
                drawAdvanced(canvas, valueRight, textLeft, textYOffset);
                break;
        }

        canvas.drawText(mTextSettingsPage[mPage], mRectSettingsPage.centerX(),
                mRectSettingsPage.bottom - textYOffset, mPaintControls);

        canvas.drawText(mTextBack, mRectBack.centerX(),
                mRectBack.bottom - textYOffset, mPaintControls);
    }

    public void update() {
        mPaintText.setColor(Colors.getOverlayTextColor());
        mPaintControls.setColor(Colors.getOverlayTextColor());

        mPaintText.setAntiAlias(Settings.antiAlias);
        mPaintControls.setAntiAlias(Settings.antiAlias);
        mPaintValue.setAntiAlias(Settings.antiAlias);
        mPaintIcon.setAntiAlias(Settings.antiAlias);
    }

    public void addCallback(Callbacks callbacks) {
        mCallbacks = callbacks;
    }

    private void initBasicPage(int lineSpacing, int topMargin, int padding, int textHeight) {
        topMargin += lineSpacing;
        mRectMode = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectMode.inset(0, padding);

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

    private void initAdvancedPage(int lineSpacing, int topMargin, int padding, int textHeight) {
        topMargin += lineSpacing;
        mRectBf = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectBf.inset(0, padding);

        topMargin += lineSpacing;
        mRectAntiAlias = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectAntiAlias.inset(0, padding);

        topMargin += lineSpacing;
        mRectMultiColor = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectMultiColor.inset(0, padding);

        topMargin += lineSpacing;
        mRectNewGameDelay = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectNewGameDelay.inset(0, padding);

        topMargin += lineSpacing;
        mRectIngameInfo = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectIngameInfo.inset(0, padding);

        topMargin += lineSpacing;
        mRectTimeFormat = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectTimeFormat.inset(0, padding);

        topMargin += lineSpacing;
        mRectStats = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectStats.inset(0, padding);
    }

    private void drawBasic(Canvas canvas, float valueRight, float textLeft, float textYOffset) {
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

        canvas.drawText(mTextMode, textLeft, mRectMode.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextModeValue[Settings.gameMode],
                valueRight, mRectMode.bottom - textYOffset, mPaintValue);
    }

    private void drawAdvanced(Canvas canvas, float valueRight, float textLeft, float textYOffset) {
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
        canvas.drawText(mTextIngameInfoValues[Settings.ingameInfo],
                valueRight, mRectIngameInfo.bottom - textYOffset, mPaintValue);

        canvas.drawText(mTextTimeFormat, textLeft, mRectTimeFormat.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextTimeFormatValues[Settings.timeFormat],
                valueRight, mRectTimeFormat.bottom - textYOffset, mPaintValue);

        canvas.drawText(mTextStats, textLeft, mRectStats.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextStatsValues[Settings.stats ? 1 : 0],
                valueRight, mRectStats.bottom - textYOffset, mPaintValue);
    }

    private void onClickBasic(int x, int y, int dx) {
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

        if (mRectMode.contains(x, y)) {
            Settings.gameMode = (++Settings.gameMode % Constants.GAME_MODES);
            Settings.save();
            if (mCallbacks != null) {
                mCallbacks.onSettingsChanged(true);
            }
        }
    }

    private void onClickAdvanced(int x, int y, int dx) {
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
            Settings.ingameInfo = (++Settings.ingameInfo % Constants.INGAME_INFO_MODES);
            Settings.save();
            if (mCallbacks != null) {
                mCallbacks.onSettingsChanged(false);
            }
        }

        if (mRectTimeFormat.contains(x, y)) {
            Settings.timeFormat = (++Settings.timeFormat % Constants.TIME_FORMATS);
            Settings.save();
        }

        if (mRectStats.contains(x, y)) {
            Settings.stats = !Settings.stats;
            Settings.save();
            if (mCallbacks != null) {
                mCallbacks.onSettingsChanged(false);
            }
        }
    }

    public interface Callbacks {

        void onSettingsChanged(boolean needUpdate);
    }
}
