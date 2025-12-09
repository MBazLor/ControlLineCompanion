package com.mbl.controllinecompanion.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.mbl.controllinecompanion.MainActivity;
import com.mbl.controllinecompanion.R;
import com.mbl.controllinecompanion.databinding.FragmentFirstBinding;
import com.mbl.controllinecompanion.tools.ToolsActivity;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    TextView btn_fly, btn_stats, btn_tools;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_stats = view.findViewById(R.id.button_stats);
        btn_tools = view.findViewById(R.id.button_tools);
        btn_fly = view.findViewById(R.id.button_fly);


        btn_fly.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                //Llamada al método que cambia el fragment en main activity
                ((MainActivity) getActivity()).showFragment(new FlightModesFragment());
            }
        });

        btn_tools.setOnClickListener( v ->{
            Intent i = new Intent(getContext(), ToolsActivity.class);
            startActivity(i);
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}