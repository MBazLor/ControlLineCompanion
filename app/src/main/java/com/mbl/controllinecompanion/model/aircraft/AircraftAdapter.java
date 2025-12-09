package com.mbl.controllinecompanion.model.aircraft;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.mbl.controllinecompanion.MainActivity;
import com.mbl.controllinecompanion.controller.NewAircraftActivity;
import com.mbl.controllinecompanion.R;
import com.mbl.controllinecompanion.fragments.OnAircraftSelectedListener;
import com.mbl.controllinecompanion.model.FlightConfig.FlightConfig;
import com.mbl.controllinecompanion.model.FlightConfig.FlightConfigDaoSQLite;

import java.util.List;

/**
 * Adapter used to configure properties to use in a recycler view
 */
public class AircraftAdapter extends RecyclerView.Adapter<AircraftAdapter.ViewHolder> {
    private List<Aircraft> aircraftList;
    private OnAircraftSelectedListener listener;

    public AircraftAdapter(List<Aircraft> aircraftList, OnAircraftSelectedListener listener) {
        this.aircraftList = aircraftList;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvWingspan,tvLineLength;
        ImageView ivImage;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvWingspan = itemView.findViewById(R.id.tv_wingspan);
            tvLineLength = itemView.findViewById(R.id.tvLineLength);
            ivImage = itemView.findViewById(R.id.iv_image);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_aicraft, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Aircraft aircraft = aircraftList.get(position);
        holder.tvName.setText(aircraft.getName());
        holder.tvWingspan.setText(String.valueOf(aircraft.getWingspan()));
        holder.tvLineLength.setText(String.valueOf(aircraft.getLineLength()));
        if(aircraft.getImage() != null) {
            holder.ivImage.setImageURI(Uri.parse(aircraft.getImage()));
        }
        holder.itemView.setOnClickListener(v -> {
            SharedPreferences prefs = v.getContext().getSharedPreferences(
                    "app_prefs", Context.MODE_PRIVATE);

            prefs.edit()
                    .putInt("selected_aircraft_id", aircraft.getId())
                    .apply();
            if(listener != null){
                listener.onAircraftSelected(aircraft.getId());
            }
            ((MainActivity) v.getContext()).getSupportFragmentManager().popBackStack();
        });

        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Select Option")
                    .setItems(new String[]{"Edit", "Delete"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                Context context = v.getContext();
                                Intent intent = new Intent(context, NewAircraftActivity.class);
                                intent.putExtra("aircraft_id", aircraft.getId());
                                intent.putExtra("update", true);

                                if (context instanceof Activity)
                                context.startActivity(intent);
                            } else {
                                //TODO move DAO instance to methods in class
                                FlightConfig fc = aircraft.getFlightConfig();
                                FlightConfigDaoSQLite fcDao = new FlightConfigDaoSQLite(v.getContext());
                                fcDao.deleteFlightConfig(fc);
                                AircraftDaoSQLite aircraftDao = new AircraftDaoSQLite(v.getContext());
                                aircraftDao.deleteAircraft(aircraft);
                                aircraftList.remove(position);
                                notifyItemRemoved(position);

                            }
                        }
                    })
                    .show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return aircraftList.size();
    }

    public void setData(List<Aircraft> newAircraftList) {
        this.aircraftList = newAircraftList;
        notifyDataSetChanged();
    }
}
