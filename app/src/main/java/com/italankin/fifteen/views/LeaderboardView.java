package com.italankin.fifteen.views;


import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.italankin.fifteen.Colors;
import com.italankin.fifteen.DBHelper;
import com.italankin.fifteen.Dimensions;
import com.italankin.fifteen.R;
import com.italankin.fifteen.Settings;
import com.italankin.fifteen.Tools;

import java.util.ArrayList;
import java.util.Date;

public class LeaderboardView extends BaseView {

    private Paint mPaintText;
    private Paint mPaintValue;
    private Paint mPaintTable;

    /**
     * Ширина поля
     */
    private String mTextWidth;
    /**
     * Высота поля
     */
    private String mTextHeight;
    /**
     * Hard Mode
     */
    private String mTextBf;
    private String[] mTextBfValue;
    /**
     * Режим игры
     */
    private String mTextMode;
    private String[] mTextModeValue;
    /**
     * Сортировка
     */
    private String mTextSort;
    private String[] mTextSortValue;
    /**
     * Кнопка "Назад"
     */
    private String mTextBack;
    private String mTextNoData;

    private Rect mRectWidth;
    private Rect mRectHeight;
    private Rect mRectMode;
    private Rect mRectBf;
    private Rect mRectSort;
    private Rect mRectBack;

    private ArrayList<TableItem> mTableItems = new ArrayList<>();
    private DBHelper mDbHelper;

    private float[] mTableGuides = {
            Dimensions.surfaceWidth * 0.12f,
            Dimensions.surfaceWidth * 0.27f,
            Dimensions.surfaceWidth * 0.53f,
            Dimensions.surfaceWidth * 0.95f
    };
    private int[] mSettingsGuides = {
            (int) (Dimensions.surfaceWidth * 0.07f),
            (int) (Dimensions.surfaceWidth * 0.31f),
            (int) (Dimensions.surfaceWidth * 0.58f),
            (int) (Dimensions.surfaceWidth * 0.86f)
    };

    private float mTableMarginTop;

    private int mSortMode = 0;
    private int mGameWidth = Settings.gameWidth;
    private int mGameHeight = Settings.gameHeight;
    private int mGameMode = Settings.gameMode;
    private int mHardMode = Settings.hardmode ? 1 : 0;

    private Callbacks mCallbacks;

    public LeaderboardView(DBHelper helper, Resources res) {
        mDbHelper = helper;

        mPaintText = new Paint();
        mPaintText.setAntiAlias(Settings.antiAlias);
        mPaintText.setColor(Colors.getOverlayTextColor());
        mPaintText.setTextSize(Dimensions.menuFontSize * 0.9f);
        mPaintText.setTypeface(Settings.typeface);
        mPaintText.setTextAlign(Paint.Align.LEFT);

        mPaintValue = new Paint();
        mPaintValue.setAntiAlias(Settings.antiAlias);
        mPaintValue.setColor(Colors.menuTextValue);
        mPaintValue.setTextSize(Dimensions.menuFontSize * 0.9f);
        mPaintValue.setTypeface(Settings.typeface);
        mPaintValue.setTextAlign(Paint.Align.LEFT);

        mPaintTable = new Paint();
        mPaintTable.setAntiAlias(Settings.antiAlias);
        mPaintTable.setTextSize(Dimensions.menuFontSize * 0.9f);
        mPaintTable.setTypeface(Settings.typeface);
        mPaintTable.setTextAlign(Paint.Align.RIGHT);

        mTextWidth = res.getString(R.string.pref_width);
        mTextHeight = res.getString(R.string.pref_height);
        mTextBf = res.getString(R.string.pref_bf);
        mTextBfValue = res.getStringArray(R.array.difficulty_modes);
        mTextMode = res.getString(R.string.pref_mode);
        mTextModeValue = res.getStringArray(R.array.game_modes);
        mTextSort = res.getString(R.string.pref_sort);
        mTextSortValue = res.getStringArray(R.array.sort_types);
        mTextBack = res.getString(R.string.back);
        mTextNoData = res.getString(R.string.info_no_data);

        Rect r = new Rect();
        mPaintText.getTextBounds(mTextWidth, 0, 1, r);

        int lineHeight = r.height();
        int marginTop = (int) (Dimensions.surfaceHeight * 0.12f);
        int mLineGap = (int) (Dimensions.surfaceHeight * 0.075f);
        mTableMarginTop = marginTop + lineHeight + 3.4f * mLineGap;

        mRectMode = new Rect(0, marginTop, mSettingsGuides[2], marginTop + lineHeight);
        mRectMode.inset(0, -lineHeight / 3);
        mRectWidth = new Rect(mSettingsGuides[2], marginTop,
                (int) Dimensions.surfaceWidth, marginTop + lineHeight);
        mRectWidth.inset(0, -lineHeight / 3);
        mRectBf = new Rect(0, marginTop + mLineGap,
                mSettingsGuides[2], marginTop + mLineGap + lineHeight);
        mRectBf.inset(0, -lineHeight / 3);
        mRectHeight = new Rect(mSettingsGuides[2], marginTop + mLineGap,
                (int) Dimensions.surfaceWidth, marginTop + mLineGap + lineHeight);
        mRectHeight.inset(0, -lineHeight / 3);
        mRectSort = new Rect(0, marginTop + 2 * mLineGap,
                (int) Dimensions.surfaceWidth, marginTop + 2 * mLineGap + lineHeight);
        mRectSort.inset(0, -lineHeight / 3);
        mRectBack = new Rect(0, (int) Dimensions.surfaceHeight - 3 * lineHeight,
                (int) Dimensions.surfaceWidth, (int) Dimensions.surfaceHeight);
    }

    public void onClick(int x, int y, int dx) {

        if (Math.abs(dx) < 15) {
            dx = 0;
        }

        if (mRectMode.contains(x, y)) {
            mGameMode = ++mGameMode % Settings.GAME_MODES;
            updateData();
        }

        if (mRectBf.contains(x, y)) {
            mHardMode = ++mHardMode % 2;
            updateData();
        }

        if (mRectSort.contains(x, y)) {
            mSortMode = ++mSortMode % 2;
            updateData();
        }

        if (mRectWidth.contains(x, y)) {
            mGameWidth += ((dx == 0) ? 1 : Math.signum(dx));
            if (mGameWidth < Settings.MIN_GAME_WIDTH) {
                mGameWidth = Settings.MAX_GAME_WIDTH;
            }
            if (mGameWidth > Settings.MAX_GAME_WIDTH) {
                mGameWidth = Settings.MIN_GAME_WIDTH;
            }
            updateData();
        }

        // -- высота поля --
        if (mRectHeight.contains(x, y)) {
            mGameHeight += ((dx == 0) ? 1 : Math.signum(dx));
            if (mGameHeight < Settings.MIN_GAME_HEIGHT) {
                mGameHeight = Settings.MAX_GAME_HEIGHT;
            }
            if (mGameHeight > Settings.MAX_GAME_HEIGHT) {
                mGameHeight = Settings.MIN_GAME_HEIGHT;
            }
            updateData();
        }

        if (mRectBack.contains(x, y)) {
            hide();
        }

    }

    /**
     * Запрос данных из бд, обновление {@link #mTableItems}
     */
    public void updateData() {
        mTableItems.clear();

        Cursor cursor = mDbHelper.query(mGameMode, mGameWidth, mGameHeight, mHardMode, mSortMode);
        if (cursor.moveToFirst()) {
            int indexMoves = cursor.getColumnIndex(DBHelper.KEY_MOVES);
            int indexTime = cursor.getColumnIndex(DBHelper.KEY_TIME);
            int indexTimestamp = cursor.getColumnIndex(DBHelper.KEY_TIMESTAMP);

            do {
                TableItem item = new TableItem();

                item.id = Integer.toString(cursor.getPosition() + 1);
                item.moves = Integer.toString(cursor.getInt(indexMoves));
                item.time = Tools.timeToString(cursor.getInt(indexTime));

                Date d = new Date(cursor.getLong(indexTimestamp));
                item.timestamp = Settings.dateFormat.format(d);

                mTableItems.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (mCallbacks != null) {
            mCallbacks.onChanged();
        }
    }

    @Override
    public boolean show() {
        mGameMode = Settings.gameMode;
        mHardMode = Settings.hardmode ? 1 : 0;
        mGameWidth = Settings.gameWidth;
        mGameHeight = Settings.gameHeight;
        mSortMode = 0;

        updateData();

        return super.show();
    }

    @Override
    public void draw(Canvas canvas, long elapsedTime) {
        if (!mShow) {
            return;
        }

        canvas.drawColor(Colors.getOverlayColor());

        mPaintText.setTextAlign(Paint.Align.LEFT);

        float s = Dimensions.menuFontSize * 0.29f;

        canvas.drawText(mTextMode, mSettingsGuides[0], mRectMode.bottom - s, mPaintText);
        canvas.drawText(mTextModeValue[mGameMode], mSettingsGuides[1],
                mRectMode.bottom - s, mPaintValue);
        canvas.drawText(mTextBf, mSettingsGuides[0], mRectBf.bottom - s, mPaintText);
        canvas.drawText(mTextBfValue[mHardMode], mSettingsGuides[1],
                mRectBf.bottom - s, mPaintValue);
        canvas.drawText(mTextSort, mSettingsGuides[0], mRectSort.bottom - s, mPaintText);
        canvas.drawText(mTextSortValue[mSortMode], mSettingsGuides[1],
                mRectSort.bottom - s, mPaintValue);

        canvas.drawText(mTextWidth, mSettingsGuides[2], mRectWidth.bottom - s, mPaintText);
        canvas.drawText("" + mGameWidth, mSettingsGuides[3],
                mRectWidth.bottom - s, mPaintValue);
        canvas.drawText(mTextHeight, mSettingsGuides[2], mRectHeight.bottom - s, mPaintText);
        canvas.drawText("" + mGameHeight, mSettingsGuides[3],
                mRectHeight.bottom - s, mPaintValue);

        mPaintText.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(mTextBack, mRectBack.centerX(), mRectBack.centerY(), mPaintText);

        if (mTableItems.size() == 0) {
            mPaintText.setAlpha(128);
            canvas.drawText(mTextNoData,
                    Dimensions.surfaceWidth * .5f, mTableMarginTop, mPaintText);
            mPaintText.setAlpha(255);
            return;
        }

        // отступ новой строки
        float gap = Dimensions.surfaceHeight * 0.05f;

        // отрисовка таблицы
        int overlayTextColor = Colors.getOverlayTextColor();
        for (int i = 0; i < mTableItems.size(); i++) {
            TableItem item = mTableItems.get(i);

            mPaintTable.setColor(overlayTextColor);
            canvas.drawText(item.id, mTableGuides[0],
                    mTableMarginTop + gap * i, mPaintTable);

            mPaintTable.setColor(Colors.menuTextValue);
            canvas.drawText(item.moves, mTableGuides[1],
                    mTableMarginTop + gap * i, mPaintTable);
            canvas.drawText(item.time, mTableGuides[2],
                    mTableMarginTop + gap * i, mPaintTable);
            canvas.drawText(item.timestamp, mTableGuides[3],
                    mTableMarginTop + gap * i, mPaintTable);
        }
    }

    @Override
    public void update() {
        mPaintText.setColor(Colors.getOverlayTextColor());
    }

    /**
     * Хранит данные о записи в таблице рекордов
     */
    private class TableItem {
        public String id;
        public String moves;
        public String time;
        public String timestamp;
    }

    public void addCallback(Callbacks callbacks) {
        mCallbacks = callbacks;
    }

    public interface Callbacks {
        void onChanged();
    }
}
