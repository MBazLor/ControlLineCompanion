package com.mbl.controllinecompanion.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.slider.Slider;
import com.mbl.controllinecompanion.R;
import com.mbl.controllinecompanion.model.FlightConfig.FlightConfig;
import com.mbl.controllinecompanion.model.FlightConfig.FlightConfigDaoSQLite;
import com.mbl.controllinecompanion.model.FlightConfig.IFlightConfigDAO;
import com.mbl.controllinecompanion.model.Payload;
import com.mbl.controllinecompanion.model.aircraft.Aircraft;
import com.mbl.controllinecompanion.model.aircraft.AircraftDaoSQLite;
import com.mbl.controllinecompanion.model.aircraft.IAircraftDAO;
import com.mbl.controllinecompanion.model.connection.Connection;

public class ManualFlightFragment extends Fragment {

   TextView btn_motorSend, btn_motorStop;
   Slider sld_throttle;

   private Connection connection = null;
   private Payload payload = null ;
   private Aircraft aircraft;

    public ManualFlightFragment() {
        // Required empty public constructor
    }

    public static ManualFlightFragment newInstance() {
        ManualFlightFragment fragment = new ManualFlightFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connection = Connection.getInstance();
        payload = Payload.getInstance();

        SharedPreferences prefs = getActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        int selectedAircraftId = prefs.getInt("selected_aircraft_id", -1);
        if(selectedAircraftId != -1){
            IAircraftDAO aircraftDAO = new AircraftDaoSQLite(this.getContext());
            aircraft = aircraftDAO.getAircraft(selectedAircraftId);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manual_flight, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_motorSend = view.findViewById(R.id.btn_motorSend);
        btn_motorStop = view.findViewById(R.id.btn_motorStop);
        sld_throttle = view.findViewById(R.id.sld_throttle);


        IFlightConfigDAO fcDao = new FlightConfigDaoSQLite(this.getContext());
        FlightConfig fc = fcDao.getFlightConfig(aircraft.getId());

        Log.i("fc", String.valueOf(fc.getThrottle()));
        sld_throttle.setValue(fc.getThrottle());

        btn_motorSend.setOnClickListener(v -> {
            payload.setThrottle((short) sld_throttle.getValue());
            fc.setThrottle((short) sld_throttle.getValue());

            connection.sendPayload();
        });

        btn_motorStop.setOnClickListener( v -> {
            payload.setThrottle((short) 1000);
            connection.sendPayload();
        });

        sld_throttle.addOnSliderTouchListener( new Slider.OnSliderTouchListener(){
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {

            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                fc.setThrottle((short) sld_throttle.getValue());
                fcDao.updateFlightConfig(fc);
            }
        });

    }



}