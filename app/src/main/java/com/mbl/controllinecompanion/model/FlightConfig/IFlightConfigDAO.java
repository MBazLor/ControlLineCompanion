package com.mbl.controllinecompanion.model.FlightConfig;

import android.database.sqlite.SQLiteDatabase;

import com.mbl.controllinecompanion.model.aircraft.Aircraft;

import java.util.List;

public interface IFlightConfigDAO {
    public void addFlightConfig(FlightConfig fc);
    public void updateFlightConfig(FlightConfig fc);
    public void deleteFlightConfig(FlightConfig fc);
    public FlightConfig getFlightConfig(int id);
}
