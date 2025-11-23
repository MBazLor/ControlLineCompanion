package com.mbl.controllinecompanion.model;

public class FlightConfig {
    int id;
    int timer;
    int throttle;
    boolean autoThrottle;
    float autoThrottleFactor;
    boolean adjustThrRpm;
    int rpmTarget;

    public FlightConfig(){
    }

    public FlightConfig(int timer, int throttle, boolean autoThrottle, float autoThrottleFactor, boolean adjustThrRpm, int rpmTarget) {
        this.timer = timer;
        this.throttle = throttle;
        this.autoThrottle = autoThrottle;
        this.autoThrottleFactor = autoThrottleFactor;
        this.adjustThrRpm = adjustThrRpm;
        this.rpmTarget = rpmTarget;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getTimer() {
        return timer;
    }
    public void setTimer(int timer) {
        this.timer = timer;
    }
    public int getThrottle() {
        return throttle;
    }
    public void setThrottle(int throttle) {
        this.throttle = throttle;
    }
    public boolean isAutoThrottle() {
        return autoThrottle;
    }
    public void setAutoThrottle(boolean autoThrottle) {
        this.autoThrottle = autoThrottle;
    }
    public float getAutoThrottleFactor() {
        return autoThrottleFactor;
    }
    public void setAutoThrottleFactor(float autoThrottleFactor) {
        this.autoThrottleFactor = autoThrottleFactor;
    }
    public boolean isAdjustThrRpm() {
        return adjustThrRpm;
    }
    public void setAdjustThrRpm(boolean adjustThrRpm) {
        this.adjustThrRpm = adjustThrRpm;
    }
    public int getRpmTarget() {
        return rpmTarget;
    }
    public void setRpmTarget(int rpmTarget) {
        this.rpmTarget = rpmTarget;
    }

}
