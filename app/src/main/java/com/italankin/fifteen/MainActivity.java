package com.italankin.fifteen;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;

public class MainActivity extends Activity {

    private GameSurface mGameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Settings.prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Settings.load(this);
        mGameView = new GameSurface(this);
        setContentView(mGameView);
    }

    @Override
    public void onPause() {
        super.onPause();
        Settings.save(true);
        mGameView.onPause();
    }

    @Override
    public void onBackPressed() {
        if (!mGameView.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
