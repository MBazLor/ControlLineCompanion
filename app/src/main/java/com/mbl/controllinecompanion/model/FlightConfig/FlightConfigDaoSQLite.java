package com.mbl.controllinecompanion.model.FlightConfig;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.mbl.controllinecompanion.model.aircraft.Aircraft;

public class FlightConfigDaoSQLite extends SQLiteOpenHelper implements IFlightConfigDAO{

    private static final String DATABASE_NAME = "clcompanion.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_FLIGHT_CONFIG = "flight_config";

    public FlightConfigDaoSQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void addFlightConfig(FlightConfig fc) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("timer", fc.getTimer());
        values.put("throttle", fc.getThrottle());
        values.put("auto_throttle", fc.isAutoThrottle());
        values.put("auto_throttle_factor", fc.getAutoThrottleFactor());
        values.put("adjust_thr_rpm", fc.isAdjustThrRpm());
        values.put("rpm_target", fc.rpmTarget);
        db.insert(TABLE_FLIGHT_CONFIG, null, values);
        db.close();

    }

    @Override
    public void updateFlightConfig(FlightConfig fc) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("timer", fc.getTimer());
        values.put("throttle", fc.getThrottle());
        values.put("auto_throttle", fc.isAutoThrottle());
        values.put("auto_throttle_factor", fc.getAutoThrottleFactor());
        values.put("adjust_thr_rpm", fc.isAdjustThrRpm());
        values.put("rpm_target", fc.rpmTarget);
        db.update(TABLE_FLIGHT_CONFIG, values, "id=?", new String[]{String.valueOf(fc.getId())});
        db.close();
    }

    @Override
    public void deleteFlightConfig(FlightConfig fc) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FLIGHT_CONFIG, "id=?", new String[]{String.valueOf(fc.getId())});
    }

    @Override
    public FlightConfig getFlightConfig(int id) {

        FlightConfig fc = new FlightConfig();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * from " + TABLE_FLIGHT_CONFIG +" WHERE id = " + id ;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            fc.setId(cursor.getInt(0));
            fc.setTimer(cursor.getInt(1));
            fc.setThrottle(cursor.getInt(2));
            fc.setAutoThrottle(cursor.getInt(3) == 1);
            fc.setAutoThrottleFactor(cursor.getFloat(4));
            fc.setAdjustThrRpm(cursor.getInt(5) == 1);
            fc.setRpmTarget(cursor.getInt(6));
        }
        db.close();

        return fc;
    }

    //TODO is needed?
    @Override
    public int getIdFromDb() {
        return 0;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE " + TABLE_FLIGHT_CONFIG + " ( "
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "timer INTEGER,"
                        + "throttle INTEGER,"
                        + "auto_throttle INTEGER,"
                        + "auto_throttle_factor REAL,"
                        + "adjust_thr_rpm INTEGER,"
                        + "rpm_target INTEGER"
                        + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


}
