package com.mbl.controllinecompanion.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppDatabase extends SQLiteOpenHelper {
    public AppDatabase(Context context) {
        super(context, DatabaseParams.DATABASE_NAME, null, DatabaseParams.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseParams.CREATE_TABLE_AIRCRAFTS);
        db.execSQL(DatabaseParams.CREATE_TABLE_FLIGHT_CONFIG);
        // Si tienes más tablas, aquí van todas.
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseParams.TABLE_AIRCRAFTS);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseParams.TABLE_FLIGHT_CONFIG);
        onCreate(db);
    }
}
