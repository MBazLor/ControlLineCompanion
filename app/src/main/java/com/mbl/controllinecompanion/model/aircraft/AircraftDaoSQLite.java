package com.mbl.controllinecompanion.model.aircraft;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mbl.controllinecompanion.model.FlightConfig.FlightConfig;
import com.mbl.controllinecompanion.model.FlightConfig.FlightConfigDaoSQLite;
import com.mbl.controllinecompanion.model.FlightConfig.IFlightConfigDAO;
import com.mbl.controllinecompanion.model.database.AppDatabase;
import com.mbl.controllinecompanion.model.database.DatabaseParams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AircraftDaoSQLite  implements IAircraftDAO{
    private static final String DATABASE_NAME = DatabaseParams.DATABASE_NAME;
    private static final int DATABASE_VERSION = DatabaseParams.DATABASE_VERSION;
    private static final String TABLE_AIRCRAFTS = DatabaseParams.TABLE_AIRCRAFTS;
    private final AppDatabase dbHelper;
    public AircraftDaoSQLite(Context context) {
        dbHelper = new AppDatabase(context);
    }

    @Override
    public void addAircraft(Aircraft aircraft) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            //First, insert FlightConfig
            FlightConfig fc = aircraft.getFlightConfig();
            ContentValues fcValues = new ContentValues();
            fcValues.put("timer", fc.getTimer());
            fcValues.put("throttle", fc.getThrottle());
            fcValues.put("auto_throttle", fc.isAutoThrottle() ? 1 : 0);
            fcValues.put("auto_throttle_factor", fc.getAutoThrottleFactor());
            fcValues.put("adjust_thr_rpm", fc.isAdjustThrRpm() ? 1 : 0);
            fcValues.put("rpm_target", fc.getRpmTarget());
            //Get inserted FlightConfig id
            //TODO extract table name to configuration file
            long fcId = db.insert(DatabaseParams.TABLE_FLIGHT_CONFIG, null, fcValues);
            if(fcId == -1){
                throw new Exception("Error inserting FlightConfig when creating Aircraft");
            }
            fc.setId((int)fcId);
            //Then, insert Aircraft
            ContentValues values = new ContentValues();
            values.put("name", aircraft.getName());
            values.put("wingspan", aircraft.getWingspan());
            values.put("image", aircraft.getImage());
            values.put("line_length", aircraft.getLineLength());
            values.put("flight_config", aircraft.getFlightConfig().getId());
            db.insert(TABLE_AIRCRAFTS, null, values);
            db.setTransactionSuccessful();


        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            db.endTransaction();
        }

    }

    @Override
    public void updateAircraft(Aircraft aircraft) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", aircraft.getName());
        values.put("wingspan", aircraft.getWingspan());
        values.put("image", aircraft.getImage());
        values.put("line_length", aircraft.getLineLength());
        values.put("flight_config", aircraft.getFlightConfig().getId());
        int filas = db.update(TABLE_AIRCRAFTS, values, "id=?", new String[]{String.valueOf(aircraft.getId())});

    }

    @Override
    public void deleteAircraft(Aircraft aircraft) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_AIRCRAFTS, "id=?", new String[]{String.valueOf(aircraft.getId())});
    }

    @Override
    public Aircraft getAircraft(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * from " + TABLE_AIRCRAFTS +" WHERE id = " + id ;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            Aircraft aircraft = new Aircraft();
            aircraft.setId(cursor.getInt(0));
            aircraft.setName(cursor.getString(1));
            aircraft.setWingspan(cursor.getFloat(2));
            aircraft.setImage(cursor.getString(3));
            aircraft.setLineLength(cursor.getFloat(4));
            FlightConfig flightConfig = new FlightConfig();
            flightConfig.setId(cursor.getInt(5));
            aircraft.setFlightConfig(flightConfig);
            return aircraft;
        }
        return null;
    }

    @Override
    public List<Aircraft> getAllAircraft() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Aircraft> aircrafts = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_AIRCRAFTS;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Aircraft aircraft = new Aircraft();
                aircraft.setId(cursor.getInt(0));
                aircraft.setName(cursor.getString(1));
                aircraft.setWingspan(cursor.getFloat(2));
                aircraft.setImage(cursor.getString(3));
                aircraft.setLineLength(cursor.getFloat(4));
                FlightConfig flightConfig = new FlightConfig();
                flightConfig.setId(cursor.getInt(5));
                aircraft.setFlightConfig(flightConfig);
                aircrafts.add(aircraft);
                } while (cursor.moveToNext());
        }
        cursor.close();
        return aircrafts;
    }
}
