package com.mbl.controllinecompanion.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.mbl.controllinecompanion.NewAircraftActivity;
import com.mbl.controllinecompanion.R;
import com.mbl.controllinecompanion.model.aircraft.Aircraft;
import com.mbl.controllinecompanion.model.aircraft.AircraftAdapter;
import com.mbl.controllinecompanion.model.aircraft.AircraftDaoSQLite;
import com.mbl.controllinecompanion.model.aircraft.IAircraftDAO;

import java.util.List;


public class AircraftListFragment extends Fragment {


    Button btn_new, btn_update;
    List<Aircraft> aircrafts;
    IAircraftDAO aircraftDAO;

    AircraftAdapter adapter;
    RecyclerView recyclerView;

    private OnAircraftSelectedListener listener;

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
        View view = inflater.inflate(R.layout.fragment_aircraft_list, container, false);

        recyclerView = view.findViewById(R.id.rv_aircraft_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        //Get all aircrafts from database
        aircraftDAO = new AircraftDaoSQLite(this.getContext());
        aircrafts = aircraftDAO.getAllAircraft();

        adapter = new AircraftAdapter(aircrafts, listener);
        recyclerView.setAdapter(adapter);

        // Inflate the layout for this fragment
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_new = view.findViewById(R.id.button_new);
        btn_update = view.findViewById(R.id.btn_update);


        btn_new.setOnClickListener( v -> {
            Intent i = new Intent(getActivity(),NewAircraftActivity.class);
            startActivity(i);
        });
        btn_update.setOnClickListener(v -> refreshList());
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnAircraftSelectedListener) {
            listener = (OnAircraftSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " debe implementar OnAircraftSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    public void refreshList(){
        adapter.setData(aircraftDAO.getAllAircraft());
    }

}