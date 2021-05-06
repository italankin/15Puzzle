package com.italankin.fifteen.export;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import com.italankin.fifteen.Constants;
import com.italankin.fifteen.DBHelper;
import com.italankin.fifteen.Logger;
import com.italankin.fifteen.R;
import com.italankin.fifteen.Settings;
import com.italankin.fifteen.Tools;

import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class RecordsExporter implements Exporter {

    private static final String DEFAULT_FILENAME = "15-puzzle-records.csv";
    private static final char DELIMITER = ';';

    private final Context context;
    private final DBHelper dbHelper;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler;

    public RecordsExporter(Context context, DBHelper dbHelper) {
        this.context = context;
        this.dbHelper = dbHelper;
        this.handler = new Handler(context.getMainLooper());
    }

    @Override
    public void export(Uri uri, Callback callback) {
        executor.execute(new ExportTask(uri, callback));
    }

    @Override
    public String defaultFilename() {
        return DEFAULT_FILENAME;
    }

    private class ExportTask implements Runnable {
        private final Uri uri;
        private final Callback callback;

        ExportTask(Uri uri, Callback callback) {
            this.uri = uri;
            this.callback = callback;
        }

        @Override
        public void run() {
            try {
                ContentResolver resolver = context.getContentResolver();
                try (OutputStreamWriter w = new OutputStreamWriter(resolver.openOutputStream(uri), UTF_8)) {
                    w.write(context.getString(R.string.export_type));
                    w.write(DELIMITER);

                    w.write(context.getString(R.string.export_hard));
                    w.write(DELIMITER);

                    w.write(context.getString(R.string.export_width));
                    w.write(DELIMITER);

                    w.write(context.getString(R.string.export_height));
                    w.write(DELIMITER);

                    w.write(context.getString(R.string.export_time));
                    w.write(DELIMITER);

                    w.write(context.getString(R.string.export_moves));
                    w.write(DELIMITER);

                    w.write(context.getString(R.string.export_date));
                    w.write('\n');

                    try (Cursor cursor = dbHelper.queryAll()) {
                        if (cursor.moveToFirst()) {
                            int indexType = cursor.getColumnIndex(DBHelper.KEY_TYPE);
                            int indexHard = cursor.getColumnIndex(DBHelper.KEY_HARDMODE);
                            int indexWidth = cursor.getColumnIndex(DBHelper.KEY_WIDTH);
                            int indexHeight = cursor.getColumnIndex(DBHelper.KEY_HEIGHT);
                            int indexTime = cursor.getColumnIndex(DBHelper.KEY_TIME);
                            int indexMoves = cursor.getColumnIndex(DBHelper.KEY_MOVES);
                            int indexDate = cursor.getColumnIndex(DBHelper.KEY_TIMESTAMP);
                            String[] types = context.getResources().getStringArray(R.array.game_types);
                            do {
                                w.write(types[cursor.getInt(indexType)]);
                                w.write(DELIMITER);

                                w.write(String.valueOf(cursor.getInt(indexHard)));
                                w.write(DELIMITER);

                                w.write(String.valueOf(cursor.getInt(indexWidth)));
                                w.write(DELIMITER);

                                w.write(String.valueOf(cursor.getInt(indexHeight)));
                                w.write(DELIMITER);

                                w.write(Tools.timeToString(Constants.TIME_FORMAT_SEC_MS_LONG, cursor.getInt(indexTime)));
                                w.write(DELIMITER);

                                w.write(String.valueOf(cursor.getInt(indexMoves)));
                                w.write(DELIMITER);

                                Date d = new Date(cursor.getLong(indexDate));
                                String timestamp = Settings.dateFormat.format(d);
                                w.write(timestamp);
                                w.write('\n');
                            } while (cursor.moveToNext());
                        }
                    }
                    handler.post(callback::onExportSuccess);
                }
            } catch (Exception e) {
                Logger.e(e, "RecordsExporter error:");
                handler.post(callback::onExportError);
            }
        }
    }
}
