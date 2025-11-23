package com.mbl.controllinecompanion.model.aircraft;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mbl.controllinecompanion.model.FlightConfig;

import java.util.Collections;
import java.util.List;

public class AircraftDaoSQLite extends SQLiteOpenHelper implements IAircraftDAO{
    private static final String DATABASE_NAME = "clcompanion.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_AIRCRAFTS = "aircrafts";

    public AircraftDaoSQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void addAircraft(Aircraft aircraft) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", aircraft.getName());
        values.put("wingspan", aircraft.getWingspan());
        values.put("image", aircraft.getImage());
        values.put("lineLength", aircraft.getLineLength());
        values.put("flightConfig", aircraft.getFlightConfig().getId());
        db.insert(TABLE_AIRCRAFTS, null, values);
        db.close();
    }

    @Override
    public void updateAircraft(Aircraft aircraft) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", aircraft.getName());
        values.put("wingspan", aircraft.getWingspan());
        values.put("image", aircraft.getImage());
        values.put("lineLength", aircraft.getLineLength());
        values.put("flightConfig", aircraft.getFlightConfig().getId());
        int filas = db.update(TABLE_AIRCRAFTS, values, "id=?", new String[]{String.valueOf(aircraft.getId())});
        db.close();
    }

    @Override
    public void deleteAircraft(Aircraft aircraft) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_AIRCRAFTS, "id=?", new String[]{String.valueOf(aircraft.getId())});
        db.close();
    }

    @Override
    public Aircraft getAircraft(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
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
        SQLiteDatabase db = this.getReadableDatabase();
        List<Aircraft> aircrafts = null;
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
        db.close();
        return Collections.emptyList();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_AIRCRAFTS + " ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT, "
                + "wingspan REAL, "
                + "image TEXT, "
                + "lineLength REAL, "
                + "flightConfig TEXT"
                + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        /*
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_AIRCRAFTS);
        onCreate(sqLiteDatabase);
        */
    }
}
