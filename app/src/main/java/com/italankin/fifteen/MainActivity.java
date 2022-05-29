package com.italankin.fifteen;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.italankin.fifteen.export.ExportCallback;
import com.italankin.fifteen.export.Exporter;
import com.italankin.fifteen.export.RecordsExporter;
import com.italankin.fifteen.export.SessionExporter;

public class MainActivity extends Activity implements ExportCallback {

    private static final int REQUEST_CODE_EXPORT_RECORDS = 1;
    private static final int REQUEST_CODE_EXPORT_SESSION = 2;
    private static final int REQUEST_CODE_IMPORT_RECORDS = 3;

    private Exporter mRecordsExporter;
    private Exporter mSessionExporter;
    private DBHelper mDbHelper;
    private GameSurface mGameView;

    private final Exporter.Callback exportCallback = new Exporter.Callback() {
        @Override
        public void onSuccess(int count) {
            Toast.makeText(MainActivity.this, getString(R.string.export_success, count), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError() {
            Toast.makeText(MainActivity.this, R.string.export_import_failure, Toast.LENGTH_SHORT).show();
        }
    };

    private final Exporter.Callback importCallback = new Exporter.Callback() {
        @Override
        public void onSuccess(int count) {
            Toast.makeText(MainActivity.this, getString(R.string.import_success, count), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError() {
            Toast.makeText(MainActivity.this, R.string.export_import_failure, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Settings.prefs = Settings.getPreferences(this);
        mDbHelper = new DBHelper(this);
        mRecordsExporter = new RecordsExporter(this, mDbHelper);
        mSessionExporter = new SessionExporter(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Settings.load(this);
        Settings.updateUiMode(this);
        if (mGameView == null) {
            mGameView = new GameSurface(this, this, mDbHelper);
            mGameView.setId(R.id.game_view);
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
        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                switch (requestCode) {
                    case REQUEST_CODE_EXPORT_RECORDS:
                        mRecordsExporter.export(uri, exportCallback);
                        break;
                    case REQUEST_CODE_EXPORT_SESSION:
                        mSessionExporter.export(uri, exportCallback);
                        break;
                    case REQUEST_CODE_IMPORT_RECORDS:
                        mRecordsExporter.importData(uri, importCallback);
                        break;
                }
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
    public void exportRecords() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(Exporter.MIME_TYPE);
        intent.putExtra(Intent.EXTRA_TITLE, mRecordsExporter.defaultFilename());
        startActivityForResult(intent, REQUEST_CODE_EXPORT_RECORDS);
    }

    @Override
    public void importRecords() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(Exporter.MIME_TYPE);
        startActivityForResult(intent, REQUEST_CODE_IMPORT_RECORDS);
    }

    @Override
    public void exportSession() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(Exporter.MIME_TYPE);
        intent.putExtra(Intent.EXTRA_TITLE, mSessionExporter.defaultFilename());
        startActivityForResult(intent, REQUEST_CODE_EXPORT_SESSION);
    }
}
