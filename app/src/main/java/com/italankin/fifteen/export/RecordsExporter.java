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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class RecordsExporter implements Exporter {

    private static final String DEFAULT_FILENAME = "15-puzzle-records.csv";
    private static final char DELIMITER = ';';
    private static final String DELIMITER_STR = Character.toString(DELIMITER);

    private static final Pattern TIME_FORMAT_MIN_SEC = Pattern.compile("^(\\d+):(\\d{2})$");
    private static final Pattern TIME_FORMAT_MIN_SEC_MS = Pattern.compile("^(\\d+):(\\d{2})\\.(\\d)$");
    private static final Pattern TIME_FORMAT_MIN_SEC_MS_LONG = Pattern.compile("^(\\d+):(\\d{2})\\.(\\d{3})$");
    private static final Pattern TIME_FORMAT_SEC_MS_LONG = Pattern.compile("^(\\d+)\\.(\\d{3})$");

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
    public void importData(Uri uri, Callback callback) {
        executor.execute(new ImportTask(uri, callback));
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

                    int exportedCount = 0;
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
                                exportedCount++;
                            } while (cursor.moveToNext());
                        }
                    }
                    int total = exportedCount;
                    handler.post(() -> callback.onSuccess(total));
                }
            } catch (Exception e) {
                Logger.e(e, "RecordsExporter export error:");
                handler.post(callback::onError);
            }
        }
    }

    private class ImportTask implements Runnable {
        private final Uri uri;
        private final Callback callback;

        ImportTask(Uri uri, Callback callback) {
            this.uri = uri;
            this.callback = callback;
        }

        @Override
        public void run() {
            try {
                ContentResolver resolver = context.getContentResolver();
                try (BufferedReader r = new BufferedReader(new InputStreamReader(resolver.openInputStream(uri)))) {
                    String[] types = context.getResources().getStringArray(R.array.game_types);
                    String line;
                    int importedCount = 0;
                    while ((line = r.readLine()) != null) {
                        // line format:
                        // type;hard mode;width;height;time;moves;date
                        String[] entries = line.split(DELIMITER_STR);
                        if (entries.length != 7) {
                            continue;
                        }
                        int type = indexOfString(types, entries[0]);
                        if (type == -1) {
                            continue;
                        }
                        int hard = safeParseInt(entries[1]);
                        if (hard != 0 && hard != 1) {
                            Logger.d("Failed to process line='%s': invalid hard=%d", line, hard);
                            continue;
                        }
                        int width = safeParseInt(entries[2]);
                        if (width < Constants.MIN_GAME_WIDTH || width > Constants.MAX_GAME_WIDTH) {
                            Logger.d("Failed to process line='%s': invalid width=%d", line, width);
                            continue;
                        }
                        int height = safeParseInt(entries[3]);
                        if (height < Constants.MIN_GAME_HEIGHT || height > Constants.MAX_GAME_HEIGHT) {
                            Logger.d("Failed to process line='%s': invalid height=%d", line, height);
                            continue;
                        }
                        long time = parseTime(entries[4]);
                        if (time <= 0) {
                            Logger.d("Failed to process line='%s': invalid time=%d", line, time);
                            continue;
                        }
                        int moves = safeParseInt(entries[5]);
                        if (moves <= 0) {
                            Logger.d("Failed to process line='%s': invalid moves=%d", line, moves);
                            continue;
                        }
                        long timestamp = parseTimestamp(entries[6]);
                        if (timestamp <= 0) {
                            Logger.d("Failed to process line='%s': invalid timestamp=%d", line, timestamp);
                            continue;
                        }
                        dbHelper.insert(type, width, height, hard, moves, time, timestamp);
                        importedCount++;
                    }
                    int total = importedCount;
                    handler.post(() -> callback.onSuccess(total));
                }
            } catch (Exception e) {
                Logger.e(e, "RecordsExporter import error:");
                handler.post(callback::onError);
            }
        }

        private long parseTime(String s) {
            Matcher matcher = TIME_FORMAT_MIN_SEC.matcher(s);
            if (matcher.find()) {
                int min = safeParseInt(matcher.group(1));
                int sec = safeParseInt(matcher.group(2));
                if (sec >= 60) {
                    return 0;
                }
                return (sec + min * 60L) * 1000L;
            }
            matcher = TIME_FORMAT_MIN_SEC_MS.matcher(s);
            if (matcher.find()) {
                int min = safeParseInt(matcher.group(1));
                int sec = safeParseInt(matcher.group(2));
                if (sec >= 60) {
                    return 0;
                }
                int ms = safeParseInt(matcher.group(3));
                return ms * 100L + (sec + min * 60L) * 1000L;
            }
            matcher = TIME_FORMAT_MIN_SEC_MS_LONG.matcher(s);
            if (matcher.find()) {
                int min = safeParseInt(matcher.group(1));
                int sec = safeParseInt(matcher.group(2));
                if (sec >= 60) {
                    return 0;
                }
                int ms = safeParseInt(matcher.group(3));
                return ms + (sec + min * 60L) * 1000L;
            }
            matcher = TIME_FORMAT_SEC_MS_LONG.matcher(s);
            if (matcher.find()) {
                int sec = safeParseInt(matcher.group(1));
                int ms = safeParseInt(matcher.group(2));
                return ms + sec * 1000L;
            }
            return 0;
        }

        private int indexOfString(String[] e, String s) {
            for (int i = 0; i < e.length; i++) {
                if (e[i].equals(s)) {
                    return i;
                }
            }
            return -1;
        }

        private int safeParseInt(String s) {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException ignored) {
                return -1;
            }
        }

        private long parseTimestamp(String s) {
            try {
                Date date = Settings.dateFormat.parse(s);
                if (date == null) {
                    return -1;
                }
                return date.getTime();
            } catch (ParseException ignored) {
                return -1;
            }
        }
    }
}
