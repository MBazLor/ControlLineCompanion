package com.mbl.controllinecompanion.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.slider.Slider;
import com.mbl.controllinecompanion.R;
import com.mbl.controllinecompanion.model.Payload;
import com.mbl.controllinecompanion.model.connection.Connection;

public class ManualFlightFragment extends Fragment {

   Button btn_motorSend, btn_motorStop;
   Slider sld_throttle;

   private Connection connection = null;
   private Payload payload = null ;

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

        btn_motorSend.setOnClickListener(v -> {
            payload.setThrottle((short) sld_throttle.getValue());
            connection.sendPayload();
        });

        btn_motorStop.setOnClickListener( v -> {
            payload.setThrottle((short) 1000);
            connection.sendPayload();
        });

    }



}