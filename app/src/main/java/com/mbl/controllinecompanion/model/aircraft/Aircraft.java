package com.mbl.controllinecompanion.model.aircraft;

import com.mbl.controllinecompanion.model.FlightConfig.FlightConfig;

public class Aircraft {

    int id;
    String name;
    float wingspan;
    String image;
    //Recorded Flights
    float lineLength;
    FlightConfig flightConfig;
    public Aircraft() {
    }
    public Aircraft(String name, float wingspan, String image, float lineLength, FlightConfig flightConfig) {
        this.name = name;
        this.wingspan = wingspan;
        this.image = image;
        this.lineLength = lineLength;
        this.flightConfig = flightConfig;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public float getWingspan() {
        return wingspan;
    }
    public void setWingspan(float wingspan) {
        this.wingspan = wingspan;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public float getLineLength() {
        return lineLength;
    }
    public void setLineLength(float lineLength) {
        this.lineLength = lineLength;
    }
    public FlightConfig getFlightConfig() {
        return flightConfig;
    }
    public void setFlightConfig(FlightConfig flightConfig) {
        this.flightConfig = flightConfig;
    }


}
