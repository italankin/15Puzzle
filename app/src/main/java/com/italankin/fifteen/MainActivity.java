package com.italankin.fifteen;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.italankin.fifteen.export.ExportCallback;
import com.italankin.fifteen.export.RecordsExporter;

public class MainActivity extends Activity implements ExportCallback {

    private static final int REQUEST_CODE_CREATE_DOCUMENT = 1;

    private RecordsExporter mExporter;
    private DBHelper mDbHelper;
    private GameSurface mGameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Settings.prefs = Settings.getPreferences(this);
        mDbHelper = new DBHelper(this);
        mExporter = new RecordsExporter(this, mDbHelper);
    }

    @Override
    public void onResume() {
        super.onResume();
        Settings.load(this);
        Settings.updateUiMode(this);
        if (mGameView == null) {
            mGameView = new GameSurface(this, this, mDbHelper);
        }
        setContentView(mGameView);
    }

    @Override
    public void onPause() {
        super.onPause();
        Settings.save(true);
        mGameView.onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Settings.updateUiMode(this);
        if (mGameView != null) {
            mGameView.updateViews();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CREATE_DOCUMENT && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                export(uri);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!mGameView.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void export() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, RecordsExporter.DEFAULT_FILENAME);
        startActivityForResult(intent, REQUEST_CODE_CREATE_DOCUMENT);
    }

    private void export(Uri uri) {
        mExporter.export(uri, new RecordsExporter.Callback() {
            @Override
            public void onExportSuccess() {
                Toast.makeText(MainActivity.this, R.string.export_success, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onExportError() {
                Toast.makeText(MainActivity.this, R.string.export_failure, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
