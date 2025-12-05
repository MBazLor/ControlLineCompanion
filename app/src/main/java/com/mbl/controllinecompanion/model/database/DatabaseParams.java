package com.mbl.controllinecompanion.model.database;

public final class DatabaseParams {

    private DatabaseParams() {}

    public static final String DATABASE_NAME = "clcompanion.db";
    public static final int DATABASE_VERSION = 5;

    public static final String TABLE_FLIGHT_CONFIG = "flight_config";
    public static final String TABLE_AIRCRAFTS = "aircrafts";

    public static final String CREATE_TABLE_FLIGHT_CONFIG =
            "CREATE TABLE " + TABLE_FLIGHT_CONFIG + " ( "
                    +" id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "timer INTEGER,"
                    + "throttle INTEGER,"
                    + "auto_throttle INTEGER,"
                    + "auto_throttle_factor REAL,"
                    + "adjust_thr_rpm INTEGER,"
                    + "rpm_target INTEGER"
                    + ")";

    public static final String CREATE_TABLE_AIRCRAFTS =
            "CREATE TABLE " + TABLE_AIRCRAFTS + " ( "
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "name TEXT, "
                    + "wingspan REAL, "
                    + "image TEXT, "
                    + "line_length REAL, "
                    + "flight_config TEXT"
                    + ")";

}
