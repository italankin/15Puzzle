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
import com.italankin.fifteen.views.settings.AdvancedPage;
import com.italankin.fifteen.views.settings.BasicPage;
import com.italankin.fifteen.views.settings.SettingsPage;

import java.util.HashMap;
import java.util.Map;

public class SettingsView extends BaseView {

    private static final int PAGE_BASIC = 0;
    private static final int PAGE_ADVANCED = 1;

    private final Paint mPaintText;
    private final Paint mPaintValue;
    private final Paint mPaintControls;

    private final Map<Integer, SettingsPage> pages = new HashMap<>();

    private String mTextBack;
    private String mTextSettingsPageBasic;
    private String mTextSettingsPageAdvanced;

    private RectF mRectSettingsPage;
    private RectF mRectBack;

    private int mCurrentPage = PAGE_BASIC;

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

        mTextBack = res.getString(R.string.back);
        mTextSettingsPageBasic = res.getString(R.string.settings_page_basic);
        mTextSettingsPageAdvanced = res.getString(R.string.settings_page_advanced);

        Rect r = new Rect();
        mPaintText.getTextBounds("A", 0, 1, r);
        int textHeight = r.height();

        initPages(res, lineSpacing, topMargin, padding, textHeight);

        mRectSettingsPage = new RectF(0, Dimensions.surfaceHeight - lineSpacing * 1.5f - textHeight * 2,
                Dimensions.surfaceWidth, Dimensions.surfaceHeight - lineSpacing * 1.5f - textHeight);
        mRectSettingsPage.inset(0, padding);

        mRectBack = new RectF(0, Dimensions.surfaceHeight - lineSpacing - textHeight,
                Dimensions.surfaceWidth, Dimensions.surfaceHeight - lineSpacing);
        mRectBack.inset(0, padding);
    }

    @Override
    public boolean show() {
        mCurrentPage = PAGE_BASIC;
        return super.show();
    }

    public void onClick(int x, int y, int dx) {
        if (Math.abs(dx) < 15) {
            dx = 0;
        }

        pages.get(mCurrentPage).onClick(x, y, dx);

        if (mRectSettingsPage.contains(x, y)) {
            switch (mCurrentPage) {
                case PAGE_BASIC:
                    mCurrentPage = PAGE_ADVANCED;
                    break;
                case PAGE_ADVANCED:
                    mCurrentPage = PAGE_BASIC;
                    break;
                default:
                    // do nothing
                    break;
            }
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

        pages.get(mCurrentPage).draw(canvas, valueRight, textLeft, textYOffset);

        String nextPageName = null;
        switch (mCurrentPage) {
            case PAGE_BASIC:
                nextPageName = mTextSettingsPageAdvanced;
                break;
            case PAGE_ADVANCED:
                nextPageName = mTextSettingsPageBasic;
                break;
        }
        if (nextPageName != null) {
            canvas.drawText(nextPageName, mRectSettingsPage.centerX(),
                    mRectSettingsPage.bottom - textYOffset, mPaintControls);
        }

        canvas.drawText(mTextBack, mRectBack.centerX(),
                mRectBack.bottom - textYOffset, mPaintControls);
    }

    public void update() {
        mPaintText.setColor(Colors.getOverlayTextColor());
        mPaintControls.setColor(Colors.getOverlayTextColor());

        mPaintText.setAntiAlias(Settings.antiAlias);
        mPaintControls.setAntiAlias(Settings.antiAlias);
        mPaintValue.setAntiAlias(Settings.antiAlias);

        for (SettingsPage page : pages.values()) {
            page.update();
        }
    }

    public void addCallback(Callbacks callbacks) {
        for (SettingsPage page : pages.values()) {
            page.addCallback(callbacks);
        }
    }

    private void initPages(Resources res, int lineSpacing, int topMargin, int padding, int textHeight) {
        pages.put(PAGE_BASIC, new BasicPage(mPaintText, mPaintValue, res));
        pages.put(PAGE_ADVANCED, new AdvancedPage(mPaintText, mPaintValue, res));

        for (SettingsPage page : pages.values()) {
            page.init(lineSpacing, topMargin, padding, textHeight);
        }
    }

    public interface Callbacks {

        void onSettingsChanged(boolean needUpdate);
    }
}
