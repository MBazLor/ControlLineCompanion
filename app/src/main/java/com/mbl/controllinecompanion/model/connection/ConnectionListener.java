package com.mbl.controllinecompanion.model.connection;

public interface ConnectionListener {
    void onConnected();
    void onError(String error);
}
