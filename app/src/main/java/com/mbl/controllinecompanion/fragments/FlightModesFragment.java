package com.mbl.controllinecompanion.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.mbl.controllinecompanion.MainActivity;
import com.mbl.controllinecompanion.R;

import com.mbl.controllinecompanion.databinding.FragmentFlightModesBinding;

public class FlightModesFragment extends Fragment {

    private FragmentFlightModesBinding binding;

    Button btn_auto, btn_manual;
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFlightModesBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_manual = view.findViewById(R.id.button_manual_flight);

        btn_manual.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                //Llamada al método que cambia el fragment en main activity
                ((MainActivity) getActivity()).showFragment(new ManualFlightFragment());
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}