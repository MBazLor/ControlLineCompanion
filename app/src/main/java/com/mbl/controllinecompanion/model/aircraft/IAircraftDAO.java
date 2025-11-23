package com.mbl.controllinecompanion.model.aircraft;

import java.util.List;

public interface IAircraftDAO {

    public void addAircraft(Aircraft aircraft);
    public void updateAircraft(Aircraft aircraft);
    public void deleteAircraft(Aircraft aircraft);
    public Aircraft getAircraft(int id);
    public List<Aircraft> getAllAircraft();
}
