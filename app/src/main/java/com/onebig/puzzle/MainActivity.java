package com.onebig.puzzle;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;

public class MainActivity extends Activity {

    public GameView gameView;                           // главный экран

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Settings.prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public void onPause() {
        Settings.save();
        gameView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        Settings.load();
        gameView = new GameView(this);
        setContentView(gameView);
    }

    @Override
    public void onBackPressed() {
        if (gameView.onBackPressed()) {
            super.onBackPressed();
        }
    }


}
