package com.italankin.fifteen.views;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.animation.DecelerateInterpolator;
import com.italankin.fifteen.*;

public class NewAppBannerView extends BaseView {

    public static final long DELAY_TIME = 500;
    public static final long APPEAR_TIME = 400;
    public static final long DISAPPEAR_TIME = 400;
    public static final long ACTIVE_TIME = 8000;

    private final Context context;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF bannerRect = new RectF();
    private final RectF clipRect = new RectF();
    private final float buttonTextYOffset;
    private final String bannerText;
    private final TimeInterpolator timeInterpolator = new DecelerateInterpolator(2f);
    private long time;

    public NewAppBannerView(Context context) {
        this.context = context;
        paint.setTextSize(Dimensions.interfaceFontSize);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Settings.typeface);
        Rect r = new Rect();
        paint.getTextBounds("A", 0, 1, r);
        buttonTextYOffset = r.centerY();
        bannerText = context.getString(R.string.new_app_banner_text);
        bannerRect.set(0, Dimensions.topBarMargin,
                Dimensions.surfaceWidth, Dimensions.topBarMargin + Dimensions.topBarHeight);
        clipRect.set(bannerRect);
    }

    @Override
    public boolean show() {
        time = 0;
        return super.show();
    }

    @Override
    public void draw(Canvas canvas, long elapsedTime) {
        time += elapsedTime;
        if (time <= DELAY_TIME) {
            return;
        }
        long realTime = time - DELAY_TIME;
        int saveCount = canvas.save();
        if (realTime < APPEAR_TIME) {
            float p = Math.min(1f, (realTime / (float) APPEAR_TIME));
            float w = bannerRect.width() / 2f * timeInterpolator.getInterpolation(p);
            clipRect.left = bannerRect.centerX() - w;
            clipRect.right = bannerRect.centerX() + w;
            canvas.clipRect(clipRect);
        } else if (realTime >= APPEAR_TIME + ACTIVE_TIME + DISAPPEAR_TIME) {
            hide();
            return;
        } else if (realTime >= APPEAR_TIME + ACTIVE_TIME) {
            float p = Math.max(0f, 1f - ((realTime - APPEAR_TIME - ACTIVE_TIME) / (float) APPEAR_TIME));
            float w = bannerRect.width() / 2f * timeInterpolator.getInterpolation(p);
            clipRect.left = bannerRect.centerX() - w;
            clipRect.right = bannerRect.centerX() + w;
            canvas.clipRect(clipRect);
        }
        paint.setColor(Colors.NEW_APP);
        canvas.drawRect(bannerRect, paint);
        paint.setColor(Colors.NEW_APP_TEXT);
        canvas.drawText(bannerText, bannerRect.centerX(), bannerRect.centerY() - buttonTextYOffset, paint);
        canvas.restoreToCount(saveCount);
    }

    public void onClick(float x, float y) {
        if (bannerRect.contains(x, y)) {
            Tools.openUrl(context, R.string.fifteen_app_url);
            hide();
        }
    }

    @Override
    public void update() {
    }
}
