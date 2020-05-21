package com.italankin.fifteen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "puzzle15";
    public static final int VERSION = 8;

    public static final String KEY_TABLE = "records";
    public static final String KEY_ID = "id";
    public static final String KEY_WIDTH = "puzzle_width";
    public static final String KEY_HEIGHT = "puzzle_height";
    public static final String KEY_TIME = "time";
    public static final String KEY_MOVES = "moves";
    public static final String KEY_MODE = "mode";
    public static final String KEY_BLINDMODE = "hardmode";
    public static final String KEY_TIMESTAMP = "timestamp";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + KEY_TABLE + " (" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_MODE + " INTEGER," +
                KEY_WIDTH + " INTEGER," +
                KEY_HEIGHT + " INTEGER," +
                KEY_MOVES + " INTEGER," +
                KEY_TIME + " INTEGER," +
                KEY_BLINDMODE + " INTEGER," +
                KEY_TIMESTAMP + " INTEGER" +
                ");");
    }

    public void insert(int mode, int width, int height, int hardmode, int moves, long time) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(KEY_MODE, mode);
        cv.put(KEY_WIDTH, width);
        cv.put(KEY_HEIGHT, height);
        cv.put(KEY_MOVES, moves);
        cv.put(KEY_TIME, time);
        cv.put(KEY_BLINDMODE, hardmode);
        cv.put(KEY_TIMESTAMP, System.currentTimeMillis());

        long rowid = db.insert(KEY_TABLE, null, cv);

        Tools.log("Inserted values: rowid=" + rowid + " " + cv.toString());

        db.close();
    }

    public void delete(SQLiteDatabase db, int mode, int width, int height, int hardmode) {
        String selection = KEY_MODE + "=" + mode + " AND " +
                KEY_WIDTH + "=" + width + " AND " +
                KEY_HEIGHT + "=" + height + " AND " +
                KEY_BLINDMODE + "=" + hardmode;
        String limit = "LIMIT -1 OFFSET 10";

        String lastByTime = "SELECT id FROM (SELECT id FROM " + KEY_TABLE + " WHERE " + selection +
                " ORDER BY " + KEY_TIME + " ASC " + limit + ")";
        String lastByMoves = "SELECT id FROM (SELECT id FROM " + KEY_TABLE + " WHERE " + selection +
                " ORDER BY " + KEY_MOVES + " ASC " + limit + ")";

        // delete only those records, which are not part of any top 10
        String sql = "DELETE FROM " + KEY_TABLE + " WHERE " +
                KEY_ID + " IN (" + lastByTime + " INTERSECT " + lastByMoves + ")";

        Tools.log("Executed query: " + sql);

        db.execSQL(sql);
    }

    public Cursor query(int mode, int width, int height, int hardmode, int sort) {
        SQLiteDatabase db = getWritableDatabase();

        String[] columns = {
                KEY_MODE,
                KEY_WIDTH,
                KEY_HEIGHT,
                KEY_MOVES,
                KEY_TIME,
                KEY_BLINDMODE,
                KEY_TIMESTAMP
        };
        String selection = KEY_MODE + "=" + mode + " AND " +
                KEY_WIDTH + "=" + width + " AND " +
                KEY_HEIGHT + "=" + height + " AND " +
                KEY_BLINDMODE + "=" + hardmode;
        String limit = "10";
        String orderBy = ((sort == 1) ? KEY_TIME : KEY_MOVES) + ", " +
                ((sort != 1) ? KEY_TIME : KEY_MOVES) + " ASC";

        delete(db, mode, width, height, hardmode);

        return db.query(KEY_TABLE, columns, selection, null, null, null, orderBy, limit);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + KEY_TABLE);
            onCreate(db);

            Tools.log("Upgraded from " + oldVersion + " to " + newVersion);
        }
    }
}
