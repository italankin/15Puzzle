package com.italankin.fifteen.views;

import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.italankin.fifteen.Colors;
import com.italankin.fifteen.Constants;
import com.italankin.fifteen.DBHelper;
import com.italankin.fifteen.Dimensions;
import com.italankin.fifteen.R;
import com.italankin.fifteen.Settings;
import com.italankin.fifteen.Tools;

import java.util.ArrayList;
import java.util.Date;

public class LeaderboardView extends BaseView {

    private final Paint mPaintText;
    private final Paint mPaintValue;
    private final Paint mPaintTable;
    private final Paint mPaintControls;

    private final String mTextWidth;
    private final String mTextHeight;
    private final String mTextMode;
    private final String[] mTextModeValue;
    private final String mTextType;
    private final String[] mTextTypeValue;
    private final String mTextSort;
    private final String[] mTextSortValue;
    private final String mTextExport;
    private final String mTextImport;
    private final String mTextBack;
    private final String mTextNoData;

    private final RectF mRectWidth;
    private final RectF mRectHeight;
    private final RectF mRectType;
    private final RectF mRectMode;
    private final RectF mRectSort;
    private final RectF mRectExport;
    private final RectF mRectImport;
    private final RectF mRectBack;

    private final ArrayList<TableItem> mTableItems = new ArrayList<>();
    private final DBHelper mDbHelper;

    private final float[] mTableGuides = {
            Dimensions.surfaceWidth * 0.11f,
            Dimensions.surfaceWidth * 0.28f,
            Dimensions.surfaceWidth * 0.62f,
            Dimensions.surfaceWidth * 0.95f
    };
    private final int[] mSettingsGuides = {
            (int) (Dimensions.surfaceWidth * 0.07f),
            (int) (Dimensions.surfaceWidth * 0.28f),
            (int) (Dimensions.surfaceWidth * 0.58f),
            (int) (Dimensions.surfaceWidth * 0.84f)
    };

    private final float mTableMarginTop;

    private int mSortMode = 0;
    private int mGameWidth = Settings.gameWidth;
    private int mGameHeight = Settings.gameHeight;
    private int mGameType = Settings.gameType;
    private int mMode = Settings.hardmode ? 1 : 0;

    private Callbacks mCallbacks;

    public LeaderboardView(DBHelper helper, Resources res) {
        mDbHelper = helper;

        float textSize = Dimensions.menuFontSize * 0.85f;

        mPaintText = new Paint();
        mPaintText.setAntiAlias(Settings.antiAlias);
        mPaintText.setColor(Colors.getOverlayTextColor());
        mPaintText.setTextSize(textSize);
        mPaintText.setTypeface(Settings.typeface);
        mPaintText.setTextAlign(Paint.Align.LEFT);

        mPaintValue = new Paint();
        mPaintValue.setAntiAlias(Settings.antiAlias);
        mPaintValue.setColor(Colors.menuTextValue);
        mPaintValue.setTextSize(textSize);
        mPaintValue.setTypeface(Settings.typeface);
        mPaintValue.setTextAlign(Paint.Align.LEFT);

        mPaintTable = new Paint();
        mPaintTable.setAntiAlias(Settings.antiAlias);
        mPaintTable.setTextSize(textSize);
        mPaintTable.setTypeface(Settings.typeface);
        mPaintTable.setTextAlign(Paint.Align.RIGHT);

        mPaintControls = new Paint(mPaintText);
        mPaintControls.setTextAlign(Paint.Align.CENTER);
        mPaintControls.setTextSize(Dimensions.menuFontSize);
        mPaintControls.setTypeface(Settings.typeface);

        mTextWidth = res.getString(R.string.pref_width);
        mTextHeight = res.getString(R.string.pref_height);
        mTextMode = res.getString(R.string.pref_mode);
        mTextModeValue = res.getStringArray(R.array.difficulty_modes);
        mTextType = res.getString(R.string.pref_type);
        mTextTypeValue = res.getStringArray(R.array.game_types);
        mTextSort = res.getString(R.string.pref_sort);
        mTextSortValue = res.getStringArray(R.array.sort_types);
        mTextBack = res.getString(R.string.back);
        mTextExport = res.getString(R.string.export);
        mTextImport = res.getString(R.string.import_records);
        mTextNoData = res.getString(R.string.info_no_data);

        Rect r = new Rect();
        mPaintText.getTextBounds(mTextWidth, 0, 1, r);

        int lineHeight = r.height();
        int marginTop = (int) (Dimensions.surfaceHeight * 0.08f);
        int mLineGap = (int) (Dimensions.surfaceHeight * 0.06f);
        mTableMarginTop = marginTop + lineHeight + 3.4f * mLineGap;

        mRectType = new RectF(0, marginTop, mSettingsGuides[2], marginTop + lineHeight);
        mRectType.inset(0, -lineHeight / 3);
        mRectWidth = new RectF(mSettingsGuides[2], marginTop,
                (int) Dimensions.surfaceWidth, marginTop + lineHeight);
        mRectWidth.inset(0, -lineHeight / 3);
        mRectMode = new RectF(0, marginTop + mLineGap,
                mSettingsGuides[2], marginTop + mLineGap + lineHeight);
        mRectMode.inset(0, -lineHeight / 3);
        mRectHeight = new RectF(mSettingsGuides[2], marginTop + mLineGap,
                (int) Dimensions.surfaceWidth, marginTop + mLineGap + lineHeight);
        mRectHeight.inset(0, -lineHeight / 3);
        mRectSort = new RectF(0, marginTop + 2 * mLineGap,
                (int) Dimensions.surfaceWidth, marginTop + 2 * mLineGap + lineHeight);
        mRectSort.inset(0, -lineHeight / 3);

        int lineSpacing = (int) (Dimensions.surfaceHeight * 0.082f);
        int padding = -lineSpacing / 4;
        mPaintControls.getTextBounds(mTextBack, 0, mTextBack.length(), r);
        mRectBack = new RectF(0, Dimensions.surfaceHeight - lineSpacing - r.height(),
                Dimensions.surfaceWidth, Dimensions.surfaceHeight - lineSpacing);
        mRectBack.inset(0, padding);
        mRectExport = new RectF(0, mRectBack.top - lineHeight - r.height(),
                Dimensions.surfaceWidth / 2, mRectBack.top - (int) (lineSpacing * 0.375f));
        mRectExport.inset(0, padding);
        mRectImport = new RectF(Dimensions.surfaceWidth / 2, mRectBack.top - lineHeight - r.height(),
                Dimensions.surfaceWidth, mRectBack.top - (int) (lineSpacing * 0.375f));
        mRectImport.inset(0, padding);
    }

    public void onClick(float x, float y, float dx) {
        if (Math.abs(dx) < 15) {
            dx = 0;
        }

        if (mRectType.contains(x, y)) {
            mGameType = ++mGameType % Constants.GAME_TYPES;
            updateData();
        }

        if (mRectMode.contains(x, y)) {
            mMode = ++mMode % 2;
            updateData();
        }

        if (mRectSort.contains(x, y)) {
            mSortMode = ++mSortMode % 2;
            updateData();
        }

        if (mRectWidth.contains(x, y)) {
            mGameWidth += ((dx == 0) ? 1 : Math.signum(dx));
            if (mGameWidth < Constants.MIN_GAME_WIDTH) {
                mGameWidth = Constants.MAX_GAME_WIDTH;
            }
            if (mGameWidth > Constants.MAX_GAME_WIDTH) {
                mGameWidth = Constants.MIN_GAME_WIDTH;
            }
            updateData();
        }

        if (mRectHeight.contains(x, y)) {
            mGameHeight += ((dx == 0) ? 1 : Math.signum(dx));
            if (mGameHeight < Constants.MIN_GAME_HEIGHT) {
                mGameHeight = Constants.MAX_GAME_HEIGHT;
            }
            if (mGameHeight > Constants.MAX_GAME_HEIGHT) {
                mGameHeight = Constants.MIN_GAME_HEIGHT;
            }
            updateData();
        }

        if (mRectBack.contains(x, y)) {
            hide();
        }
        if (mRectExport.contains(x, y) && mCallbacks != null) {
            mCallbacks.onExportClicked();
        }
        if (mRectImport.contains(x, y) && mCallbacks != null) {
            mCallbacks.onImportClicked();
            hide();
        }
    }

    private void updateData() {
        mTableItems.clear();

        Cursor cursor = mDbHelper.query(mGameType, mGameWidth, mGameHeight, mMode, mSortMode);
        if (cursor.moveToFirst()) {
            int indexMoves = cursor.getColumnIndex(DBHelper.KEY_MOVES);
            int indexTime = cursor.getColumnIndex(DBHelper.KEY_TIME);
            int indexTimestamp = cursor.getColumnIndex(DBHelper.KEY_TIMESTAMP);

            do {
                String id = Integer.toString(cursor.getPosition() + 1);
                String moves = Integer.toString(cursor.getInt(indexMoves));
                int timeValue = cursor.getInt(indexTime);
                String time = Tools.timeToString(Settings.timeFormat, timeValue);
                Date d = new Date(cursor.getLong(indexTimestamp));
                String timestamp = Settings.dateFormat.format(d);
                mTableItems.add(new TableItem(id, moves, time, timestamp));
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (mCallbacks != null) {
            mCallbacks.onChanged();
        }
    }

    @Override
    public boolean show() {
        mGameType = Settings.gameType;
        mMode = Settings.hardmode ? 1 : 0;
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
        canvas.drawText(mTextType, mSettingsGuides[0], mRectType.bottom - s, mPaintText);
        canvas.drawText(mTextTypeValue[mGameType], mSettingsGuides[1],
                mRectType.bottom - s, mPaintValue);
        canvas.drawText(mTextMode, mSettingsGuides[0], mRectMode.bottom - s, mPaintText);
        canvas.drawText(mTextModeValue[mMode], mSettingsGuides[1],
                mRectMode.bottom - s, mPaintValue);
        canvas.drawText(mTextSort, mSettingsGuides[0], mRectSort.bottom - s, mPaintText);
        canvas.drawText(mTextSortValue[mSortMode], mSettingsGuides[1],
                mRectSort.bottom - s, mPaintValue);

        canvas.drawText(mTextWidth, mSettingsGuides[2], mRectWidth.bottom - s, mPaintText);
        canvas.drawText("" + mGameWidth, mSettingsGuides[3],
                mRectWidth.bottom - s, mPaintValue);
        canvas.drawText(mTextHeight, mSettingsGuides[2], mRectHeight.bottom - s, mPaintText);
        canvas.drawText("" + mGameHeight, mSettingsGuides[3],
                mRectHeight.bottom - s, mPaintValue);

        float textYOffset = Dimensions.surfaceHeight * 0.02f;
        canvas.drawText(mTextBack, mRectBack.centerX(), mRectBack.bottom - textYOffset, mPaintControls);
        canvas.drawText(mTextExport, mRectExport.centerX(), mRectExport.bottom - textYOffset, mPaintControls);
        canvas.drawText(mTextImport, mRectImport.centerX(), mRectImport.bottom - textYOffset, mPaintControls);

        if (mTableItems.size() == 0) {
            mPaintText.setTextAlign(Paint.Align.CENTER);
            mPaintText.setAlpha(128);
            canvas.drawText(mTextNoData,
                    Dimensions.surfaceWidth * .5f, mTableMarginTop, mPaintText);
            mPaintText.setAlpha(255);
            return;
        }

        float gap = Dimensions.surfaceHeight * 0.05f;
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
        mPaintText.setAntiAlias(Settings.antiAlias);
        mPaintValue.setAntiAlias(Settings.antiAlias);
        mPaintTable.setAntiAlias(Settings.antiAlias);
        mPaintControls.setAntiAlias(Settings.antiAlias);

        mPaintText.setColor(Colors.getOverlayTextColor());
        mPaintControls.setColor(Colors.getOverlayTextColor());
    }

    private static class TableItem {

        final String id;
        final String moves;
        final String time;
        final String timestamp;

        TableItem(String id, String moves, String time, String timestamp) {
            this.id = id;
            this.moves = moves;
            this.time = time;
            this.timestamp = timestamp;
        }
    }

    public void addCallback(Callbacks callbacks) {
        mCallbacks = callbacks;
    }

    public interface Callbacks {

        void onChanged();

        void onExportClicked();

        void onImportClicked();
    }
}
