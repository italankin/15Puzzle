package com.italankin.fifteen.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import com.italankin.fifteen.*;
import com.italankin.fifteen.views.settings.*;

import java.util.HashMap;
import java.util.Map;

public class SettingsView extends BaseView {

    public static final int PAGE_BASIC = 0;
    public static final int PAGE_ADVANCED = 1;
    public static final int PAGE_INGAME_INFO = 2;
    public static final int PAGE_ABOUT = 3;

    private final Context context;

    private final Paint mPaintText;
    private final Paint mPaintValue;
    private final Paint mPaintControls;

    private final Map<Integer, SettingsPage> pages = new HashMap<>();

    private final String mTextBack;
    private final String mTextAbout;
    private final String mTextSettingsPageBasic;
    private final String mTextSettingsPageAdvanced;

    private final RectF mRectSettingsPage;
    private final RectF mRectAbout;
    private final RectF mRectBack;

    private int mCurrentPage = PAGE_BASIC;

    public SettingsView(Context context) {
        this.context = context;

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

        Resources res = context.getResources();
        mTextBack = res.getString(R.string.back);
        mTextAbout = res.getString(R.string.settings_page_about);
        mTextSettingsPageBasic = res.getString(R.string.settings_page_basic);
        mTextSettingsPageAdvanced = res.getString(R.string.settings_page_advanced);

        Rect r = new Rect();
        mPaintText.getTextBounds("A", 0, 1, r);
        int textHeight = r.height();

        initPages(res);

        for (SettingsPage page : pages.values()) {
            page.init(lineSpacing, topMargin, padding, textHeight);
        }

        mRectBack = new RectF(0, Dimensions.surfaceHeight - lineSpacing - textHeight,
                Dimensions.surfaceWidth, Dimensions.surfaceHeight - lineSpacing);
        mRectBack.inset(0, padding);

        float buttonSpacing = lineSpacing * 0.15f;

        mRectSettingsPage = new RectF(0, mRectBack.top - textHeight - buttonSpacing,
                Dimensions.surfaceWidth, mRectBack.top - buttonSpacing);
        mRectSettingsPage.inset(0, padding);

        mRectAbout = new RectF(0, mRectSettingsPage.top - textHeight - buttonSpacing,
                Dimensions.surfaceWidth, mRectSettingsPage.top - buttonSpacing);
        mRectAbout.inset(0, padding);
    }

    @Override
    public boolean show() {
        mCurrentPage = PAGE_BASIC;
        return super.show();
    }

    public void onClick(float x, float y, float dx) {
        if (Math.abs(dx) < 15) {
            dx = 0;
        }

        SettingsPage page = pages.get(mCurrentPage);
        if (page != null) {
            page.onClick(x, y, dx);
        } else {
            Logger.d("Page not found: " + mCurrentPage);
        }

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

        if (mCurrentPage == PAGE_BASIC && mRectAbout.contains(x, y)) {
            mCurrentPage = PAGE_ABOUT;
            return;
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

        SettingsPage page = pages.get(mCurrentPage);
        if (page != null) {
            page.draw(canvas, valueRight, textLeft, textYOffset);
        } else {
            Logger.d("Page not found: " + mCurrentPage);
        }

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

        if (mCurrentPage == PAGE_BASIC) {
            canvas.drawText(mTextAbout, mRectAbout.centerX(),
                    mRectAbout.bottom - textYOffset, mPaintControls);
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

    public void setCurrentPage(int page) {
        mCurrentPage = page;
    }

    @Override
    public boolean hide() {
        switch (mCurrentPage) {
            case PAGE_INGAME_INFO:
                mCurrentPage = PAGE_ADVANCED;
                return true;
            case PAGE_ABOUT:
                mCurrentPage = PAGE_BASIC;
                return true;
            default:
                return super.hide();
        }
    }

    private void initPages(Resources res) {
        pages.put(PAGE_BASIC, new BasicPage(context, mPaintText, mPaintValue));
        pages.put(PAGE_ADVANCED, new AdvancedPage(this, mPaintText, mPaintValue, res));
        pages.put(PAGE_INGAME_INFO, new IngameInfoPage(mPaintText, mPaintValue, res));
        pages.put(PAGE_ABOUT, new AboutPage(context, mPaintText, mPaintValue));
    }

    public interface Callbacks {

        void onSettingsChanged(boolean needUpdate);
    }
}
