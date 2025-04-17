package com.ai.callattender;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CallLogDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "callLogs.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_CALLS = "calls";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NUMBER = "number";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_IS_SPAM = "is_spam";
    private static final String COLUMN_SUMMARY = "summary";

    public CallLogDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CALLS_TABLE = "CREATE TABLE " + TABLE_CALLS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NUMBER + " TEXT,"
                + COLUMN_TIME + " INTEGER,"
                + COLUMN_IS_SPAM + " INTEGER,"
                + COLUMN_SUMMARY + " TEXT)";
        db.execSQL(CREATE_CALLS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALLS);
        onCreate(db);
    }


    public void addCallLog(CallLogEntry call) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NUMBER, call.getPhoneNumber());
        values.put(COLUMN_TIME, call.getCallTime().getTime());
        values.put(COLUMN_IS_SPAM, call.isSpam() ? 1 : 0);
        values.put(COLUMN_SUMMARY, call.getCallSummary());
        db.insert(TABLE_CALLS, null, values);
        db.close();
    }
    // Insert some sample call logs (only if you want testing data)

    public List<CallLogEntry> getAllCallLogs() {
        List<CallLogEntry> callLogs = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CALLS + " ORDER BY " + COLUMN_TIME + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                CallLogEntry call = new CallLogEntry(
                        cursor.getString(1),
                        new Date(cursor.getLong(2)),
                        cursor.getInt(3) == 1,
                        cursor.getString(4));
                callLogs.add(call);
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return callLogs;
    }
}