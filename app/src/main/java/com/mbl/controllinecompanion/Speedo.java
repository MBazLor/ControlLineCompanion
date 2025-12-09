package com.mbl.controllinecompanion;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mbl.controllinecompanion.tools.Chronometer;

public class Speedo extends AppCompatActivity {

    TextView btn_start, btn_lap, txt_speed, input_cable_length;
    boolean isRunning = false;
    boolean timeIsSet = false;
    long currentTime ;
    long cableLength = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speedo);

        Chronometer chrono = (Chronometer) findViewById(R.id.txt_chrono);

        btn_start = findViewById(R.id.btn_start);
        btn_lap = findViewById(R.id.btn_lap);
        txt_speed = findViewById(R.id.txt_speed);

        input_cable_length = findViewById(R.id.input_cable_length);


        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRunning){
                    chrono.stop();
                    isRunning = false;
                    btn_start.setText("Start");
                }
                else {
                    //chrono.setBase(SystemClock.elapsedRealtime());
                    chrono.start();
                    isRunning = true;
                    btn_start.setText("Stop");
                }
            }
        });

        btn_lap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!timeIsSet ) {
                    currentTime = chrono.getTimeElapsed();
                    timeIsSet = true;
                    return;
                }

                if(input_cable_length.getText().toString().equals("")){
                    cableLength = 20;

                }else{
                    cableLength = Long.parseLong(input_cable_length.getText().toString());
                }


                long lapTimeMillis = chrono.getTimeElapsed() - currentTime;
                double lapTimeSecs = (double)lapTimeMillis/1000.0;
                double distanceMeters = 2 * 3.1416 * cableLength;

                Log.d("Cable length: ", String.valueOf(cableLength));
                Log.d("Distance: ", String.valueOf(distanceMeters));
                Log.d("Lap time", String.valueOf(lapTimeMillis));
                Log.d("Lap time secs", String.valueOf(lapTimeSecs));

                double speedMS = distanceMeters /  lapTimeSecs;

                Log.d("speed m/s", String.valueOf(speedMS));
                Log.d("Speed km/h", String.valueOf(speedMS * 3.6));
                txt_speed.setText(String.format("%.1f", speedMS * 3.6));
                currentTime = chrono.getTimeElapsed();
            }
        });
    }
}
