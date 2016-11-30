package com.example.android.onestop.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zixiao on 11/1/2016.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "notes.db";

    // Table Names
    public static final String TABLE_EVENT = "table_event"; // Clues table store all information needed for the game

    // Common column name
    private static final String KEY_ID = "_id";

    // event table column names
    public static final String EVENT_COLUMN_SOURCE = "source";
    public static final String EVENT_COLUMN_START_TIME = "start_time";
    public static final String EVENT_COLUMN_END_TIME = "end_time";
    public static final String EVENT_COLUMN_CATEGORY = "category";
    public static final String EVENT_COLUMN_EVENT_NAME = "event_name";
    public static final String EVENT_COLUMN_SPOT_NAME = "spot_name";
    public static final String EVENT_COLUMN_ADDR = "address";
    public static final String EVENT_COLUMN_LAT = "latitude";
    public static final String EVENT_COLUMN_LNG = "longitude";

    // Table create statements
    // clue table create statement
    private static final String EVENT_TABLE_CREATE =
            "CREATE TABLE " + TABLE_EVENT + " ( " +
                    KEY_ID + " INTEGER PRIMARY KEY," +
                    EVENT_COLUMN_EVENT_NAME + " TEXT," +
                    EVENT_COLUMN_SOURCE + " TEXT," +
                    EVENT_COLUMN_START_TIME + " TEXT," +
                    EVENT_COLUMN_END_TIME + " TEXT," +
                    EVENT_COLUMN_CATEGORY + " TEXT," +
                    EVENT_COLUMN_SPOT_NAME + " TEXT," +
                    EVENT_COLUMN_ADDR + " INTEGER," +
                    EVENT_COLUMN_LAT + " REAL," +
                    EVENT_COLUMN_LNG + " REAL" + ")";


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(EVENT_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS contacts" + TABLE_EVENT);
        onCreate(db);
    }

    // ========= EVENT table methods =============================================================
    public long insertEvent (String ename, String source, String starttime, String endtime, String c, String spot_name, String addr, Double lat, Double lng) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EVENT_COLUMN_EVENT_NAME, ename);
        values.put(EVENT_COLUMN_SOURCE, source);
        values.put(EVENT_COLUMN_START_TIME, starttime);
        values.put(EVENT_COLUMN_END_TIME, endtime);
        values.put(EVENT_COLUMN_CATEGORY, c);
        values.put(EVENT_COLUMN_SPOT_NAME, spot_name);
        values.put(EVENT_COLUMN_ADDR, addr);
        values.put(EVENT_COLUMN_LAT, lat);
        values.put(EVENT_COLUMN_LNG, lng);

        // id of event in database
        long id = db.insert(TABLE_EVENT, null, values);

        return id;
    }

    public long insertEvent (OneStopEvent e) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EVENT_COLUMN_EVENT_NAME, e.getEvent_name());
        values.put(EVENT_COLUMN_SOURCE, e.getSource());
        values.put(EVENT_COLUMN_START_TIME, e.getStart_time());
        values.put(EVENT_COLUMN_END_TIME, e.getEnd_time());
        values.put(EVENT_COLUMN_CATEGORY, e.getCategory());
        values.put(EVENT_COLUMN_SPOT_NAME, e.getSpot_name());
        values.put(EVENT_COLUMN_ADDR, e.getAddress());
        values.put(EVENT_COLUMN_LAT, e.getLatitude());
        values.put(EVENT_COLUMN_LNG, e.getLongitude());

        // id of event in database
        long id = db.insert(TABLE_EVENT, null, values);

        return id;
    }

    public int updateEvent (OneStopEvent e) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EVENT_COLUMN_EVENT_NAME, e.getEvent_name());
        values.put(EVENT_COLUMN_SOURCE, e.getSource());
        values.put(EVENT_COLUMN_START_TIME, e.getStart_time());
        values.put(EVENT_COLUMN_END_TIME, e.getEnd_time());
        values.put(EVENT_COLUMN_CATEGORY, e.getCategory());
        values.put(EVENT_COLUMN_SPOT_NAME, e.getSpot_name());
        values.put(EVENT_COLUMN_ADDR, e.getAddress());
        values.put(EVENT_COLUMN_LAT, e.getLatitude());
        values.put(EVENT_COLUMN_LNG, e.getLongitude());

       return db.update(TABLE_EVENT, values, KEY_ID + " = ?", new String[] {Integer.toString(e.getId())});
    }

    public int deleteEvent (int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_EVENT, KEY_ID + " = ?", new String[] { Integer.toString(id)});
    }

    public int deleteAllEvents () {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_EVENT, "", new String[] {});
    }

    public int deleteAllEventsBySouce (String source) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_EVENT, EVENT_COLUMN_SOURCE + " = ?", new String[] { source });

    }

    public OneStopEvent getEvent (int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_EVENT + " WHERE " + KEY_ID + " = " + id, null);

        if (cursor != null) cursor.moveToFirst();

        OneStopEvent e = new OneStopEvent();
        e.setId(cursor.getInt(cursor.getColumnIndex(this.KEY_ID)));
        e.setEvent_name(cursor.getString(cursor.getColumnIndex(this.EVENT_COLUMN_EVENT_NAME)));
        e.setSource(cursor.getString(cursor.getColumnIndex(this.EVENT_COLUMN_SOURCE)));
        e.setStart_time(cursor.getString(cursor.getColumnIndex(this.EVENT_COLUMN_START_TIME)));
        e.setEnd_time(cursor.getString(cursor.getColumnIndex(this.EVENT_COLUMN_END_TIME)));
        e.setCategory(cursor.getString(cursor.getColumnIndex(this.EVENT_COLUMN_CATEGORY)));
        e.setSpot_name(cursor.getString(cursor.getColumnIndex(this.EVENT_COLUMN_SPOT_NAME)));
        e.setAddress(cursor.getString(cursor.getColumnIndex(this.EVENT_COLUMN_ADDR)));
        e.setLatitude(cursor.getDouble(cursor.getColumnIndex(this.EVENT_COLUMN_LAT)));
        e.setLongitude(cursor.getDouble(cursor.getColumnIndex(this.EVENT_COLUMN_LNG)));

        return e;
    }

    public List<OneStopEvent> getAllEvents() {
        List<OneStopEvent> oneStopEvents = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                this.TABLE_EVENT,
                null, null, null, null, null, null);
        cursor.moveToFirst(); // redirect to the first entry

        while (cursor.isAfterLast() == false) {
            OneStopEvent e = new OneStopEvent();
            e.setId(cursor.getInt(cursor.getColumnIndex(this.KEY_ID)));
            e.setEvent_name(cursor.getString(cursor.getColumnIndex(this.EVENT_COLUMN_EVENT_NAME)));
            e.setSource(cursor.getString(cursor.getColumnIndex(this.EVENT_COLUMN_SOURCE)));
            e.setStart_time(cursor.getString(cursor.getColumnIndex(this.EVENT_COLUMN_START_TIME)));
            e.setEnd_time(cursor.getString(cursor.getColumnIndex(this.EVENT_COLUMN_END_TIME)));
            e.setCategory(cursor.getString(cursor.getColumnIndex(this.EVENT_COLUMN_CATEGORY)));
            e.setSpot_name(cursor.getString(cursor.getColumnIndex(this.EVENT_COLUMN_SPOT_NAME)));
            e.setAddress(cursor.getString(cursor.getColumnIndex(this.EVENT_COLUMN_ADDR)));
            e.setLatitude(cursor.getDouble(cursor.getColumnIndex(this.EVENT_COLUMN_LAT)));
            e.setLongitude(cursor.getDouble(cursor.getColumnIndex(this.EVENT_COLUMN_LNG)));

            oneStopEvents.add(e);
            cursor.moveToNext();
        }

        return oneStopEvents;
    }

    public List<OneStopEvent> getAllCluesBySource (String source) {
        List<OneStopEvent> oneStopEvents = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " +
                TABLE_EVENT + " WHERE " + EVENT_COLUMN_SOURCE + " = " + source ;


        Log.d("database", selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst(); // redirect to the first entry

        while (cursor.isAfterLast() == false) {
            OneStopEvent e = new OneStopEvent();
            e.setId(cursor.getInt(cursor.getColumnIndex(this.KEY_ID)));
            e.setEvent_name(cursor.getString(cursor.getColumnIndex(this.EVENT_COLUMN_EVENT_NAME)));
            e.setSource(cursor.getString(cursor.getColumnIndex(this.EVENT_COLUMN_SOURCE)));
            e.setStart_time(cursor.getString(cursor.getColumnIndex(this.EVENT_COLUMN_START_TIME)));
            e.setEnd_time(cursor.getString(cursor.getColumnIndex(this.EVENT_COLUMN_END_TIME)));
            e.setCategory(cursor.getString(cursor.getColumnIndex(this.EVENT_COLUMN_CATEGORY)));
            e.setSpot_name(cursor.getString(cursor.getColumnIndex(this.EVENT_COLUMN_SPOT_NAME)));
            e.setAddress(cursor.getString(cursor.getColumnIndex(this.EVENT_COLUMN_ADDR)));
            e.setLatitude(cursor.getDouble(cursor.getColumnIndex(this.EVENT_COLUMN_LAT)));
            e.setLongitude(cursor.getDouble(cursor.getColumnIndex(this.EVENT_COLUMN_LNG)));

            oneStopEvents.add(e);
            cursor.moveToNext();
        }

        Log.d("database", "#### Find oneStopEvents by source: find " + oneStopEvents.size());

        return oneStopEvents;
    }


    public int getEventCount () {
        SQLiteDatabase db = this.getReadableDatabase();
        int num = (int) DatabaseUtils.queryNumEntries(db, TABLE_EVENT);
        return num;
    }

}

