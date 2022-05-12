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
import com.italankin.fifteen.Tools;
import com.italankin.fifteen.statistics.Statistics;
import com.italankin.fifteen.statistics.StatisticsManager;

public class StatisticsView extends BaseView {

    private final Resources mResources;

    private final Paint mPaintText;
    private final Paint mPaintTitle;
    private final Paint mPaintValue;
    private final Paint mPaintControls;
    private final Paint mPaintTotal;

    private RectF mRectTitle;

    private final String mTextSingle;
    private RectF mRectSingle;

    private final String mTextAo5;
    private RectF mRectAo5;

    private final String mTextAo12;
    private RectF mRectAo12;

    private final String mTextAo50;
    private RectF mRectAo50;

    private final String mTextAo100;
    private RectF mRectAo100;

    private final String mTextSessionAvg;
    private RectF mRectSessionAvg;

    private final String mTextBestTime;
    private RectF mRectBestTime;

    private final String mTextBestMoves;
    private RectF mRectBestMoves;

    private final String mTextWorstTime;
    private RectF mRectWorstTime;

    private final String mTextWorstMoves;
    private RectF mRectWorstMoves;

    private final String mTextExport;
    private final RectF mRectExport;

    private final String mTextReset;
    private final RectF mRectReset;

    private final String mTextBack;
    private final RectF mRectBack;

    private RectF mRectTotal;

    private final String mTextNa;
    private final String mTextTime;
    private final String mTextMoves;
    private final String mTextTps;

    private final StatisticsManager statisticsManager;
    private Statistics statistics = Statistics.EMPTY;

    private final float[] mTableGuides = {
            Dimensions.surfaceWidth * 0.29f,
            Dimensions.surfaceWidth * 0.32f,
            Dimensions.surfaceWidth * 0.59f,
            Dimensions.surfaceWidth * 0.82f
    };

    private Callbacks callbacks;

    public StatisticsView(StatisticsManager statisticsManager, Resources res) {
        this.statisticsManager = statisticsManager;
        mResources = res;

        int lineSpacing = (int) (Dimensions.surfaceHeight * 0.06f);
        int topMargin = (int) (Dimensions.surfaceHeight * 0.08f);
        int padding = -lineSpacing / 4;
        float textSize = Dimensions.menuFontSize * .65f;

        mPaintText = new Paint();
        mPaintText.setAntiAlias(Settings.antiAlias);
        mPaintText.setColor(Colors.getOverlayTextColor());
        mPaintText.setTextSize(textSize);
        mPaintText.setTypeface(Settings.typeface);
        mPaintText.setTextAlign(Paint.Align.RIGHT);

        mPaintValue = new Paint();
        mPaintValue.setAntiAlias(Settings.antiAlias);
        mPaintValue.setColor(Colors.menuTextValue);
        mPaintValue.setTextSize(textSize);
        mPaintValue.setTypeface(Settings.typeface);
        mPaintValue.setTextAlign(Paint.Align.LEFT);

        mPaintTitle = new Paint(mPaintText);
        mPaintTitle.setTextAlign(Paint.Align.LEFT);

        mPaintTotal = new Paint(mPaintValue);
        mPaintTotal.setTextAlign(Paint.Align.CENTER);

        mPaintControls = new Paint(mPaintText);
        mPaintControls.setTextSize(Dimensions.menuFontSize);
        mPaintControls.setTextAlign(Paint.Align.CENTER);

        mTextBack = res.getString(R.string.back);
        mTextExport = res.getString(R.string.export);
        mTextReset = res.getString(R.string.reset);
        mTextSingle = res.getString(R.string.stats_single);
        mTextAo5 = res.getString(R.string.stats_ao5);
        mTextAo12 = res.getString(R.string.stats_ao12);
        mTextAo50 = res.getString(R.string.stats_ao50);
        mTextAo100 = res.getString(R.string.stats_ao100);
        mTextSessionAvg = res.getString(R.string.stats_session_avg);
        mTextBestTime = res.getString(R.string.stats_best_time);
        mTextBestMoves = res.getString(R.string.stats_best_moves);
        mTextWorstTime = res.getString(R.string.stats_worst_time);
        mTextWorstMoves = res.getString(R.string.stats_worst_moves);
        mTextNa = res.getString(R.string.stats_na);
        mTextTime = res.getString(R.string.stats_time);
        mTextMoves = res.getString(R.string.stats_moves);
        mTextTps = res.getString(R.string.stats_tps);

        Rect r = new Rect();
        mPaintText.getTextBounds(mTextBack, 0, mTextBack.length(), r);
        int textHeight = r.height();

        initStats(lineSpacing, topMargin, padding, textHeight);

        mRectBack = new RectF(0, Dimensions.surfaceHeight - lineSpacing - textHeight,
                Dimensions.surfaceWidth, Dimensions.surfaceHeight - lineSpacing);
        mRectBack.inset(0, padding);

        mRectExport = new RectF(0, mRectBack.top - textHeight - lineSpacing / 2f,
                Dimensions.surfaceWidth / 2, mRectBack.top - lineSpacing / 2f);
        mRectExport.inset(0, padding);

        mRectReset = new RectF(mRectExport);
        mRectReset.left = Dimensions.surfaceWidth / 2;
        mRectReset.right = Dimensions.surfaceWidth;
    }

    public void addCallbacks(Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    public void onClick(int x, int y) {
        if (mRectBack.contains(x, y)) {
            hide();
        }
        if (mRectExport.contains(x, y) && callbacks != null) {
            callbacks.onExportClicked();
        }
        if (mRectReset.contains(x, y)) {
            statisticsManager.clear();
            statistics = Statistics.EMPTY;
        }
    }

    @Override
    public boolean show() {
        statistics = statisticsManager.get(Settings.gameWidth, Settings.gameHeight, Settings.gameType, Settings.hardmode);
        return super.show();
    }

    @Override
    public void draw(Canvas canvas, long elapsedTime) {
        if (!mShow) {
            return;
        }

        float textYOffset = (int) (Dimensions.surfaceHeight * 0.02f);

        canvas.drawColor(Colors.getOverlayColor());
        drawStats(canvas, textYOffset);
        canvas.drawText(mTextBack, mRectBack.centerX(),
                mRectBack.bottom - textYOffset, mPaintControls);
        canvas.drawText(mTextExport, mRectExport.centerX(),
                mRectExport.bottom - textYOffset, mPaintControls);
        canvas.drawText(mTextReset, mRectReset.centerX(),
                mRectReset.bottom - textYOffset, mPaintControls);
    }

    @Override
    public void update() {
        mPaintText.setColor(Colors.getOverlayTextColor());
        mPaintControls.setColor(Colors.getOverlayTextColor());
        mPaintTitle.setColor(Colors.getOverlayTextColor());

        mPaintText.setAntiAlias(Settings.antiAlias);
        mPaintControls.setAntiAlias(Settings.antiAlias);
        mPaintValue.setAntiAlias(Settings.antiAlias);
        mPaintTitle.setAntiAlias(Settings.antiAlias);
        mPaintTotal.setAntiAlias(Settings.antiAlias);
    }

    private void initStats(int lineSpacing, int topMargin, int padding, int textHeight) {
        topMargin += lineSpacing;
        mRectTitle = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectTitle.inset(0, padding);

        topMargin += lineSpacing;
        mRectSingle = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectSingle.inset(0, padding);

        topMargin += lineSpacing;
        mRectAo5 = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectAo5.inset(0, padding);

        topMargin += lineSpacing;
        mRectAo12 = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectAo12.inset(0, padding);

        topMargin += lineSpacing;
        mRectAo50 = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectAo50.inset(0, padding);

        topMargin += lineSpacing;
        mRectAo100 = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectAo100.inset(0, padding);

        topMargin += lineSpacing;
        mRectBestTime = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectBestTime.inset(0, padding);

        topMargin += lineSpacing;
        mRectBestMoves = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectBestMoves.inset(0, padding);

        topMargin += lineSpacing;
        mRectWorstTime = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectWorstTime.inset(0, padding);

        topMargin += lineSpacing;
        mRectWorstMoves = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectWorstMoves.inset(0, padding);

        topMargin += lineSpacing;
        mRectSessionAvg = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectSessionAvg.inset(0, padding);

        topMargin += lineSpacing;
        mRectTotal = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectTotal.inset(0, padding);
    }

    private void drawStats(Canvas canvas, float textYOffset) {
        canvas.drawText(mTextTime, mTableGuides[1], mRectTitle.bottom - textYOffset, mPaintTitle);
        canvas.drawText(mTextMoves, mTableGuides[2], mRectTitle.bottom - textYOffset, mPaintTitle);
        canvas.drawText(mTextTps, mTableGuides[3], mRectTitle.bottom - textYOffset, mPaintTitle);

        canvas.drawText(mTextSingle, mTableGuides[0], mRectSingle.bottom - textYOffset, mPaintText);
        canvas.drawText(formatTime(statistics.single), mTableGuides[1], mRectSingle.bottom - textYOffset, mPaintValue);
        canvas.drawText(formatMoves(statistics.single), mTableGuides[2], mRectSingle.bottom - textYOffset, mPaintValue);
        canvas.drawText(formatTps(statistics.single), mTableGuides[3], mRectSingle.bottom - textYOffset, mPaintValue);

        canvas.drawText(mTextAo5, mTableGuides[0], mRectAo5.bottom - textYOffset, mPaintText);
        canvas.drawText(formatTime(statistics.ao5), mTableGuides[1], mRectAo5.bottom - textYOffset, mPaintValue);
        canvas.drawText(formatMoves(statistics.ao5), mTableGuides[2], mRectAo5.bottom - textYOffset, mPaintValue);
        canvas.drawText(formatTps(statistics.ao5), mTableGuides[3], mRectAo5.bottom - textYOffset, mPaintValue);

        canvas.drawText(mTextAo12, mTableGuides[0], mRectAo12.bottom - textYOffset, mPaintText);
        canvas.drawText(formatTime(statistics.ao12), mTableGuides[1], mRectAo12.bottom - textYOffset, mPaintValue);
        canvas.drawText(formatMoves(statistics.ao12), mTableGuides[2], mRectAo12.bottom - textYOffset, mPaintValue);
        canvas.drawText(formatTps(statistics.ao12), mTableGuides[3], mRectAo12.bottom - textYOffset, mPaintValue);

        canvas.drawText(mTextAo50, mTableGuides[0], mRectAo50.bottom - textYOffset, mPaintText);
        canvas.drawText(formatTime(statistics.ao50), mTableGuides[1], mRectAo50.bottom - textYOffset, mPaintValue);
        canvas.drawText(formatMoves(statistics.ao50), mTableGuides[2], mRectAo50.bottom - textYOffset, mPaintValue);
        canvas.drawText(formatTps(statistics.ao50), mTableGuides[3], mRectAo50.bottom - textYOffset, mPaintValue);

        canvas.drawText(mTextAo100, mTableGuides[0], mRectAo100.bottom - textYOffset, mPaintText);
        canvas.drawText(formatTime(statistics.ao100), mTableGuides[1], mRectAo100.bottom - textYOffset, mPaintValue);
        canvas.drawText(formatMoves(statistics.ao100), mTableGuides[2], mRectAo100.bottom - textYOffset, mPaintValue);
        canvas.drawText(formatTps(statistics.ao100), mTableGuides[3], mRectAo100.bottom - textYOffset, mPaintValue);

        canvas.drawText(mTextSessionAvg, mTableGuides[0], mRectSessionAvg.bottom - textYOffset, mPaintText);
        canvas.drawText(formatTime(statistics.session), mTableGuides[1], mRectSessionAvg.bottom - textYOffset, mPaintValue);
        canvas.drawText(formatMoves(statistics.session), mTableGuides[2], mRectSessionAvg.bottom - textYOffset, mPaintValue);
        canvas.drawText(formatTps(statistics.session), mTableGuides[3], mRectSessionAvg.bottom - textYOffset, mPaintValue);

        canvas.drawText(mTextBestTime, mTableGuides[0], mRectBestTime.bottom - textYOffset, mPaintText);
        canvas.drawText(formatTime(statistics.bestTime), mTableGuides[1], mRectBestTime.bottom - textYOffset, mPaintValue);
        canvas.drawText(formatMoves(statistics.bestTime), mTableGuides[2], mRectBestTime.bottom - textYOffset, mPaintValue);
        canvas.drawText(formatTps(statistics.bestTime), mTableGuides[3], mRectBestTime.bottom - textYOffset, mPaintValue);

        canvas.drawText(mTextBestMoves, mTableGuides[0], mRectBestMoves.bottom - textYOffset, mPaintText);
        canvas.drawText(formatTime(statistics.bestMoves), mTableGuides[1], mRectBestMoves.bottom - textYOffset, mPaintValue);
        canvas.drawText(formatMoves(statistics.bestMoves), mTableGuides[2], mRectBestMoves.bottom - textYOffset, mPaintValue);
        canvas.drawText(formatTps(statistics.bestMoves), mTableGuides[3], mRectBestMoves.bottom - textYOffset, mPaintValue);

        canvas.drawText(mTextWorstTime, mTableGuides[0], mRectWorstTime.bottom - textYOffset, mPaintText);
        canvas.drawText(formatTime(statistics.worstTime), mTableGuides[1], mRectWorstTime.bottom - textYOffset, mPaintValue);
        canvas.drawText(formatMoves(statistics.worstTime), mTableGuides[2], mRectWorstTime.bottom - textYOffset, mPaintValue);
        canvas.drawText(formatTps(statistics.worstTime), mTableGuides[3], mRectWorstTime.bottom - textYOffset, mPaintValue);

        canvas.drawText(mTextWorstMoves, mTableGuides[0], mRectWorstMoves.bottom - textYOffset, mPaintText);
        canvas.drawText(formatTime(statistics.worstMoves), mTableGuides[1], mRectWorstMoves.bottom - textYOffset, mPaintValue);
        canvas.drawText(formatMoves(statistics.worstMoves), mTableGuides[2], mRectWorstMoves.bottom - textYOffset, mPaintValue);
        canvas.drawText(formatTps(statistics.worstMoves), mTableGuides[3], mRectWorstMoves.bottom - textYOffset, mPaintValue);

        String total = mResources.getQuantityString(R.plurals.stats_total, statistics.totalCount, statistics.totalCount);
        canvas.drawText(total, mRectTotal.centerX(), mRectTotal.bottom - textYOffset, mPaintTotal);
    }

    private String formatTime(Statistics.Avg avg) {
        return avg == null ? mTextNa : Tools.timeToString(Settings.timeFormat, avg.time);
    }

    private String formatMoves(Statistics.Avg avg) {
        return avg == null ? mTextNa : Tools.formatFloat(avg.moves);
    }

    private String formatTps(Statistics.Avg avg) {
        return avg == null ? mTextNa : Tools.formatFloat(avg.tps);
    }

    public interface Callbacks {
        void onExportClicked();
    }
}
