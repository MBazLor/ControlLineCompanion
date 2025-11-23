package com.mbl.controllinecompanion.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mbl.controllinecompanion.MainActivity;
import com.mbl.controllinecompanion.NewAircraftActivity;
import com.mbl.controllinecompanion.R;


public class AircraftListFragment extends Fragment {


    Button btn_new;
    public static AircraftListFragment newInstance() {
        AircraftListFragment fragment = new AircraftListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_aircraft_list, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_new = view.findViewById(R.id.button_new);

        btn_new.setOnClickListener( v -> {
            Intent i = new Intent(getActivity(),NewAircraftActivity.class);
            startActivity(i);
        });
    }
}