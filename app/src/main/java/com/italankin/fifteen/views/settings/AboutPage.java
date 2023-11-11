package com.italankin.fifteen.views.settings;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import com.italankin.fifteen.Dimensions;
import com.italankin.fifteen.R;
import com.italankin.fifteen.views.SettingsView;

public class AboutPage implements SettingsPage {

    private final Context context;

    private final Paint mPaintText;
    private final Paint mPaintValue;

    private final String mTextSourceCode;
    private final String mTextSourceCodeValue;
    private final String mTextNewApp;
    private final String mTextNewAppValue;
    private final String mTextWebsite;
    private final String mTextWebsiteValue;

    private RectF mRectWebsite;
    private RectF mRectSourceCode;
    private RectF mRectNewApp;

    public AboutPage(Context context, Paint paintText, Paint paintValue) {
        this.context = context;

        mPaintText = paintText;
        mPaintValue = paintValue;

        Resources res = context.getResources();
        mTextSourceCode = res.getString(R.string.pref_source_code);
        mTextSourceCodeValue = res.getString(R.string.pref_source_code_value);
        mTextNewApp = res.getString(R.string.pref_new_app);
        mTextNewAppValue = res.getString(R.string.pref_new_app_value);
        mTextWebsite = res.getString(R.string.pref_website);
        mTextWebsiteValue = res.getString(R.string.pref_website_value);
    }

    @Override
    public void init(int lineSpacing, int topMargin, int padding, int textHeight) {
        topMargin += lineSpacing;
        mRectWebsite = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectWebsite.inset(0, padding);
        topMargin += lineSpacing;
        mRectSourceCode = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectSourceCode.inset(0, padding);
        topMargin += lineSpacing;
        mRectNewApp = new RectF(0, topMargin, Dimensions.surfaceWidth, topMargin + textHeight);
        mRectNewApp.inset(0, padding);
    }

    @Override
    public void draw(Canvas canvas, float valueRight, float textLeft, float textYOffset) {
        canvas.drawText(mTextWebsite, textLeft, mRectWebsite.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextWebsiteValue, valueRight, mRectWebsite.bottom - textYOffset, mPaintValue);
        canvas.drawText(mTextSourceCode, textLeft, mRectSourceCode.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextSourceCodeValue, valueRight, mRectSourceCode.bottom - textYOffset, mPaintValue);
        canvas.drawText(mTextNewApp, textLeft, mRectNewApp.bottom - textYOffset, mPaintText);
        canvas.drawText(mTextNewAppValue, valueRight, mRectNewApp.bottom - textYOffset, mPaintValue);
    }

    @Override
    public void onClick(float x, float y, float dx) {
        if (mRectWebsite.contains(x, y)) {
            openUrl(R.string.website_url);
        } else if (mRectSourceCode.contains(x, y)) {
            openUrl(R.string.source_code_url);
        } else if (mRectNewApp.contains(x, y)) {
            openUrl(R.string.fifteen_app_url);
        }
    }

    private void openUrl(int urlResource) {
        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setData(Uri.parse(context.getResources().getString(urlResource)));
        Intent chooser = Intent.createChooser(intent, null);
        context.startActivity(chooser);
    }

    @Override
    public void update() {
    }

    @Override
    public void addCallback(SettingsView.Callbacks callbacks) {
    }
}
