package com.italankin.fifteen;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;

public class MainActivity extends Activity {

    private GameView mGameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Settings.prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Settings.load();
        mGameView = new GameView(this);
        setContentView(mGameView);
    }

    @Override
    public void onPause() {
        Settings.save();
        mGameView.onPause();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (!mGameView.onBackPressed()) {
            super.onBackPressed();
        }
    }


}
