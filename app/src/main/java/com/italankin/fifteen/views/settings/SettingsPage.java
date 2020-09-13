package com.italankin.fifteen.views.settings;

import android.graphics.Canvas;

import com.italankin.fifteen.views.SettingsView;

public interface SettingsPage {

    void init(int lineSpacing, int topMargin, int padding, int textHeight);

    void draw(Canvas canvas, float valueRight, float textLeft, float textYOffset);

    void onClick(int x, int y, int dx);

    void update();

    void addCallback(SettingsView.Callbacks callbacks);
}
