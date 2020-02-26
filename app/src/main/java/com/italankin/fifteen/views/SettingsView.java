package com.italankin.fifteen.views;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.italankin.fifteen.Colors;
import com.italankin.fifteen.Dimensions;
import com.italankin.fifteen.R;
import com.italankin.fifteen.Settings;

/**
 * Класс объединяет элементы интерфейса настроек и управляет их отрисовкой и поведением
 */
public class SettingsView extends BaseView {

    private static final int PAGE_COUNT = 2;
    private static final int PAGE_BASIC = 0;
    private static final int PAGE_ADVANCED = 1;

    /**
     * заголовок элемента настроек
     */
    private Paint mPaintText;
    /**
     * значение элемента настроек
     */
    private Paint mPaintValue;
    /**
     * кнопки управления (назад)
     */
    private Paint mPaintControls;
    /**
     * для графического представления (например, цвет плиток)
     */
    private Paint mPaintIcon;

    /**
     * ширина поля
     */
    private String mTextWidth;
    private String mTextWidthValue;
    /**
     * высота поля
     */
    private String mTextHeight;
    private String mTextHeightValue;
    /**
     * hardmode
     */
    private String mTextBf;
    private String[] mTextBfValue;
    /**
     * анимации
     */
    private String mTextAnimations;
    private String[] mTextAnimationsValue;
    /**
     * цвет плиток
     */
    private String mTextColor;
    /**
     * режим fringer
     */
    private String mTextMultiColor;
    private String[] mTextMultiColorValue;
    /**
     * цвет фона
     */
    private String mTextColorMode;
    /**
     * цветовая тема
     */
    private String[] mTextColorModeValue;
    /**
     * режим игры
     */
    private String mTextMode;
    private String[] mTextModeValue;
    /**
     * кнопка "назад"
     */
    private String mTextBack;
    private String[] mTextSettingsPage;

    private String mTextAntiAlias;
    private String[] mTextAntiAliasValue;

    private String mTextNewGameDelay;
    private String[] mTextNewGameDelayValue;

    private String mTextIngameInfo;
    private String[] mTextIngameInfoValues;

    /**
     * граница элемента настройки ширины
     */
    private RectF mRectWidth;
    /**
     * граница элемента высоты
     */
    private RectF mRectHeight;
    /**
     * граница элемента "слепого" режима
     */
    private RectF mRectBf;
    /**
     * граница элемента цвета
     */
    private RectF mRectColor;
    /**
     * режим fringe
     */
    private RectF mRectMultiColor;
    /**
     * граница элемента цвета фона
     */
    private RectF mRectColorMode;
    /**
     * граница элемента визуальное представление цвета
     */
    private RectF mRectColorIcon;
    /**
     * граница элемента анимации
     */
    private RectF mRectAnimations;
    /**
     * граница элемента режим игры
     */
    private RectF mRectMode;
    private RectF mRectAntiAlias;
    private RectF mRectNewGameDelay;
    private RectF mRectIngameInfo;
    private RectF mRectSettingsPage;
    /**
     * граница элемента "назад"
     */
    private RectF mRectBack;

    private Callbacks mCallbacks;
    private int mPage = PAGE_BASIC;

    public SettingsView(Resources res) {
        int lineSpacing = (int) (Dimensions.surfaceHeight * 0.082f); // промежуток между строками
        int topMargin = (int) (Dimensions.surfaceHeight * 0.10f); // отступ от верхнего края экрана
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
        mTextBf = res.getString(R.string.pref_bf);
        mTextBfValue = res.getStringArray(R.array.difficulty_modes);
        mTextAnimations = res.getString(R.string.pref_animation);
        mTextAnimationsValue = res.getStringArray(R.array.toggle);
        mTextMultiColor = res.getString(R.string.pref_fringe);
        mTextMultiColorValue = res.getStringArray(R.array.multi_color_modes);
        mTextColorMode = res.getString(R.string.pref_color_mode);
        mTextColorModeValue = res.getStringArray(R.array.color_mode);
        mTextColor = res.getString(R.string.pref_color);
        mTextAntiAlias = res.getString(R.string.pref_anti_alias);
        mTextAntiAliasValue = res.getStringArray(R.array.toggle);
        mTextNewGameDelay = res.getString(R.string.pref_new_game_delay);
        mTextNewGameDelayValue = res.getStringArray(R.array.toggle);
        mTextIngameInfo = res.getString(R.string.pref_ingame_info);
        mTextIngameInfoValues = res.getStringArray(R.array.ingame_info);

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

    /**
     * Обработка событий нажатия
     *
     * @param x  координата x нажатия
     * @param y  координата y нажатия
     * @param dx направление жеста
     */
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

        // отступ от центра
        float valueRight = Dimensions.surfaceWidth / 2 + Dimensions.spacing;
        // для выравнивания элементов
        float textLeft = Dimensions.surfaceWidth / 2 - Dimensions.spacing;
        // смещение по вертикали
        float textYOffset = (int) (Dimensions.surfaceHeight * 0.02f);

        // фон
        canvas.drawColor(Colors.getOverlayColor());

        // страница настроек
        switch (mPage) {
            case PAGE_BASIC:
                drawBasic(canvas, valueRight, textLeft, textYOffset);
                break;
            case PAGE_ADVANCED:
                drawAdvanced(canvas, valueRight, textLeft, textYOffset);
                break;
        }

        // кнопка страницы настроек
        canvas.drawText(mTextSettingsPage[mPage], mRectSettingsPage.centerX(),
                mRectSettingsPage.bottom - textYOffset, mPaintControls);

        // кнопка "назад"
        canvas.drawText(mTextBack, mRectBack.centerX(),
                mRectBack.bottom - textYOffset, mPaintControls);
    }

    public void update() {
        mPaintText.setColor(Colors.getOverlayTextColor());
        mPaintControls.setColor(Colors.getOverlayTextColor());
        mPaintValue.setColor(Colors.menuTextValue);

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
        mRectBf = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectBf.inset(0, padding);

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
    }

    private void drawBasic(Canvas canvas, float valueRight, float textLeft, float textYOffset) {
        // чтение настроек игры
        mTextWidthValue = Integer.toString(Settings.gameWidth);
        mTextHeightValue = Integer.toString(Settings.gameHeight);

        // ширина поля
        canvas.drawText(mTextWidth, textLeft, mRectWidth.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextWidthValue, valueRight, mRectWidth.bottom - textYOffset, mPaintValue);

        // высота поля
        canvas.drawText(mTextHeight, textLeft, mRectHeight.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextHeightValue, valueRight, mRectHeight.bottom - textYOffset, mPaintValue);

        // анимации
        canvas.drawText(mTextAnimations, textLeft, mRectAnimations.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextAnimationsValue[Settings.animations ? 1 : 0],
                valueRight, mRectAnimations.bottom - textYOffset, mPaintValue);

        // цвет
        canvas.drawText(mTextColor, textLeft, mRectColor.bottom - textYOffset, mPaintText);
        mPaintIcon.setColor(Colors.getTileColor());
        canvas.drawRect(mRectColorIcon, mPaintIcon);

        // цвет фона
        canvas.drawText(mTextColorMode, textLeft, mRectColorMode.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextColorModeValue[Settings.colorMode],
                valueRight, mRectColorMode.bottom - textYOffset, mPaintValue);

        // режим
        canvas.drawText(mTextMode, textLeft, mRectMode.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextModeValue[Settings.gameMode],
                valueRight, mRectMode.bottom - textYOffset, mPaintValue);

        // bf
        canvas.drawText(mTextBf, textLeft, mRectBf.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextBfValue[Settings.hardmode ? 1 : 0],
                valueRight, mRectBf.bottom - textYOffset, mPaintValue);
    }

    private void drawAdvanced(Canvas canvas, float valueRight, float textLeft, float textYOffset) {
        canvas.drawText(mTextMultiColor, textLeft, mRectMultiColor.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextMultiColorValue[Settings.multiColor],
                valueRight, mRectMultiColor.bottom - textYOffset, mPaintValue);

        canvas.drawText(mTextAntiAlias, textLeft, mRectAntiAlias.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextAntiAliasValue[Settings.antiAlias ? 1 : 0],
                valueRight, mRectAntiAlias.bottom - textYOffset, mPaintValue);

        canvas.drawText(mTextNewGameDelay, textLeft, mRectNewGameDelay.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextNewGameDelayValue[Settings.newGameDelay ? 1 : 0],
                valueRight, mRectNewGameDelay.bottom - textYOffset, mPaintValue);

        canvas.drawText(mTextIngameInfo, textLeft, mRectIngameInfo.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextIngameInfoValues[Settings.ingameInfo],
                valueRight, mRectIngameInfo.bottom - textYOffset, mPaintValue);
    }

    private void onClickBasic(int x, int y, int dx) {
        // -- ширина поля --
        if (mRectWidth.contains(x, y)) {
            Settings.gameWidth += ((dx == 0) ? 1 : Math.signum(dx));
            if (Settings.gameWidth < Settings.MIN_GAME_WIDTH) {
                Settings.gameWidth = Settings.MAX_GAME_WIDTH;
            }
            if (Settings.gameWidth > Settings.MAX_GAME_WIDTH) {
                Settings.gameWidth = Settings.MIN_GAME_WIDTH;
            }
            Settings.save();
            if (mCallbacks != null) {
                mCallbacks.onChanged(true);
            }
        }

        // -- высота поля --
        if (mRectHeight.contains(x, y)) {
            Settings.gameHeight += ((dx == 0) ? 1 : Math.signum(dx));
            if (Settings.gameHeight < Settings.MIN_GAME_HEIGHT) {
                Settings.gameHeight = Settings.MAX_GAME_HEIGHT;
            }
            if (Settings.gameHeight > Settings.MAX_GAME_HEIGHT) {
                Settings.gameHeight = Settings.MIN_GAME_HEIGHT;
            }
            Settings.save();
            if (mCallbacks != null) {
                mCallbacks.onChanged(true);
            }
        }

        // -- переключение анимаций --
        if (mRectAnimations.contains(x, y)) {
            Settings.animations = !Settings.animations;
            Settings.save();
        }

        // -- цвет спрайтов --
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
                mCallbacks.onChanged(false);
            }
        }

        // -- цвет фона --
        if (mRectColorMode.contains(x, y)) {
            Settings.colorMode = (++Settings.colorMode % Settings.COLOR_MODES);
            Settings.save();
            if (mCallbacks != null) {
                mCallbacks.onChanged(false);
            }
        }

        // -- режим игры --
        if (mRectMode.contains(x, y)) {
            Settings.gameMode = (++Settings.gameMode % Settings.GAME_MODES);
            Settings.save();
            if (mCallbacks != null) {
                mCallbacks.onChanged(true);
            }
        }

        // -- режим игры --
        if (mRectBf.contains(x, y)) {
            Settings.hardmode = !Settings.hardmode;
            Settings.save();
            if (mCallbacks != null) {
                mCallbacks.onChanged(true);
            }
        }
    }

    private void onClickAdvanced(int x, int y, int dx) {
        if (mRectMultiColor.contains(x, y)) {
            if (dx < 0) {
                if (--Settings.multiColor < 0) {
                    Settings.multiColor += Settings.MULTI_COLOR_MODES;
                }
            } else {
                Settings.multiColor = (++Settings.multiColor % Settings.MULTI_COLOR_MODES);
            }
            Settings.save();
            if (mCallbacks != null) {
                mCallbacks.onChanged(false);
            }
        }

        if (mRectAntiAlias.contains(x, y)) {
            Settings.antiAlias = !Settings.antiAlias;
            Settings.save();
            if (mCallbacks != null) {
                mCallbacks.onChanged(false);
            }
        }

        if (mRectNewGameDelay.contains(x, y)) {
            Settings.newGameDelay = !Settings.newGameDelay;
            Settings.save();
        }

        if (mRectIngameInfo.contains(x, y)) {
            Settings.ingameInfo = (++Settings.ingameInfo % Settings.INGAME_INFO_MODES);
            Settings.save();
            if (mCallbacks != null) {
                mCallbacks.onChanged(false);
            }
        }
    }

    public interface Callbacks {

        void onChanged(boolean needUpdate);
    }
}
