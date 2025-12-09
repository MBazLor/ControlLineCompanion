package com.mbl.controllinecompanion.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import com.mbl.controllinecompanion.tools.Chronometer;

public class TimedFlightFragment extends Fragment implements Chronometer.OnChronometerTickListener {

    @Override
    public void onChronometerTick(Chronometer chronometer) {

    }

    @Override
    public void onChronometerFinish(Chronometer chronometer) {
        Log.i("Chrono finished", "Stopping chrono and motor!");
        chronometer.stop();
        payload.setThrottle((short) 1000);
        connection.sendPayload();
    }

    public interface OnMotorStopListener{
        void onMotorStop();
    }
    private OnMotorStopListener motorStopListener;

   TextView btn_start, btn_motorStop;
   Slider sld_throttle;

   private Connection connection = null;
   private Payload payload = null ;
   private Aircraft aircraft;
   private Chronometer chrono;

    public TimedFlightFragment() {
        // Required empty public constructor
    }

    public static TimedFlightFragment newInstance() {
        TimedFlightFragment fragment = new TimedFlightFragment();
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
        return inflater.inflate(R.layout.fragment_timed_flight, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_start = view.findViewById(R.id.btn_start);
        btn_motorStop = view.findViewById(R.id.btn_motorStop);
        sld_throttle = view.findViewById(R.id.sld_throttle);
        chrono = view.findViewById(R.id.chronometer2);
        chrono.setMode(Chronometer.Mode.TIMER);

        chrono.setOnChronometerTickListener(this);

        if(chrono != null);
            chrono.setCountdownDuration(10000L);




        IFlightConfigDAO fcDao = new FlightConfigDaoSQLite(this.getContext());
        FlightConfig fc = fcDao.getFlightConfig(aircraft.getId());

        Log.i("fc", String.valueOf(fc.getThrottle()));
        sld_throttle.setValue(fc.getThrottle());

        btn_start.setOnClickListener(v -> {
            payload.setThrottle((short) sld_throttle.getValue());
            chrono.start();
            connection.sendPayload();
        });

        btn_motorStop.setOnClickListener( v -> {
            payload.setThrottle((short) 1000);
            connection.sendPayload();
            chrono.stop();
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof OnMotorStopListener){
            motorStopListener = (OnMotorStopListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnMotorStopListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        motorStopListener = null;
    }


    public void stopChronoFromActivity(){
        if(chrono != null){
            Log.i("Chrono finished", "Stopping chrono and motor!");
            chrono.stop();

        }
    }

}