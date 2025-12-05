package com.mbl.controllinecompanion.model.FlightConfig;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.mbl.controllinecompanion.model.aircraft.Aircraft;
import com.mbl.controllinecompanion.model.database.AppDatabase;
import com.mbl.controllinecompanion.model.database.DatabaseParams;

public class FlightConfigDaoSQLite  implements IFlightConfigDAO{

    private static final String DATABASE_NAME = DatabaseParams.DATABASE_NAME;
    private static final int DATABASE_VERSION = DatabaseParams.DATABASE_VERSION;
    private static final String TABLE_FLIGHT_CONFIG = DatabaseParams.TABLE_FLIGHT_CONFIG;

    private final AppDatabase dbHelper;



    public FlightConfigDaoSQLite(Context context) {
        dbHelper = new AppDatabase(context);
        //super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Add a new FlightConfig to the database
     * @param fc
     * @return id of inserted element
     */
    @Override
    public void addFlightConfig(FlightConfig fc) {
        SQLiteDatabase db =dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("timer", fc.getTimer());
        values.put("throttle", fc.getThrottle());
        values.put("auto_throttle", fc.isAutoThrottle());
        values.put("auto_throttle_factor", fc.getAutoThrottleFactor());
        values.put("adjust_thr_rpm", fc.isAdjustThrRpm());
        values.put("rpm_target", fc.rpmTarget);
        int id = (int) db.insert(TABLE_FLIGHT_CONFIG, null, values);


    }

    @Override
    public void updateFlightConfig(FlightConfig fc) {
        SQLiteDatabase db =dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("timer", fc.getTimer());
        values.put("throttle", fc.getThrottle());
        values.put("auto_throttle", fc.isAutoThrottle());
        values.put("auto_throttle_factor", fc.getAutoThrottleFactor());
        values.put("adjust_thr_rpm", fc.isAdjustThrRpm());
        values.put("rpm_target", fc.rpmTarget);
        db.update(TABLE_FLIGHT_CONFIG, values, "id=?", new String[]{String.valueOf(fc.getId())});

    }

    @Override
    public void deleteFlightConfig(FlightConfig fc) {
        SQLiteDatabase db =dbHelper.getWritableDatabase();
        db.delete(TABLE_FLIGHT_CONFIG, "id=?", new String[]{String.valueOf(fc.getId())});
    }

    @Override
    public FlightConfig getFlightConfig(int id) {

        FlightConfig fc = new FlightConfig();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * from " + TABLE_FLIGHT_CONFIG +" WHERE " + "id = " + id ;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            fc.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            fc.setTimer(cursor.getInt(cursor.getColumnIndexOrThrow("timer")));
            fc.setThrottle(cursor.getInt(cursor.getColumnIndexOrThrow("throttle")));
            fc.setAutoThrottle(cursor.getInt(cursor.getColumnIndexOrThrow("auto_throttle")) == 1);
            fc.setAutoThrottleFactor(cursor.getFloat(cursor.getColumnIndexOrThrow("auto_throttle_factor")));
            fc.setAdjustThrRpm(cursor.getInt(cursor.getColumnIndexOrThrow("adjust_thr_rpm")) == 1);
            fc.setRpmTarget(cursor.getInt(cursor.getColumnIndexOrThrow("rpm_target")));
        }
        cursor.close();


        return fc;
    }

}
