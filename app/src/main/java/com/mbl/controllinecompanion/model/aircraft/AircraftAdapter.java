package com.mbl.controllinecompanion.model.aircraft;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mbl.controllinecompanion.R;

import java.util.List;

/**
 * Adapter used to configure properties to use in a recycler view
 */
public class AircraftAdapter extends RecyclerView.Adapter<AircraftAdapter.ViewHolder> {
    private List<Aircraft> aircraftList;

    public AircraftAdapter(List<Aircraft> aircraftList) {
        this.aircraftList = aircraftList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvWingspan;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvWingspan = itemView.findViewById(R.id.tv_wingspan);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_aicraft, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Aircraft aircraft = aircraftList.get(position);
        holder.tvName.setText(aircraft.getName());
        holder.tvWingspan.setText(String.valueOf(aircraft.getWingspan()));
    }

    @Override
    public int getItemCount() {
        return aircraftList.size();
    }
}
