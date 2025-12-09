package com.mbl.controllinecompanion;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.mbl.controllinecompanion.controller.MainActivityInterface;
import com.mbl.controllinecompanion.fragments.AircraftListFragment;
import com.mbl.controllinecompanion.fragments.FirstFragment;
import com.mbl.controllinecompanion.fragments.OnAircraftSelectedListener;
import com.mbl.controllinecompanion.fragments.TimedFlightFragment;
import com.mbl.controllinecompanion.model.Payload;
import com.mbl.controllinecompanion.model.aircraft.Aircraft;
import com.mbl.controllinecompanion.model.aircraft.AircraftDaoSQLite;
import com.mbl.controllinecompanion.model.connection.Connection;
import com.mbl.controllinecompanion.model.connection.ConnectionListener;
import com.mbl.controllinecompanion.model.database.AppDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ConnectionListener, MainActivityInterface, OnAircraftSelectedListener, TimedFlightFragment.OnMotorStopListener {

    TextView btn_connect, status_text,tv_plane_name;
    ImageView iv_plane_image;
    ImageButton btn_options;
    Connection connection;
    Payload payload;
    Thread connectorThread;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connection = Connection.getInstance(); //Connection singleton instance Runnable on individual thread
        connection.addListener(this);

        payload = Payload.getInstance(); //Singleton instance that manages data sent to receiver

        tv_plane_name = findViewById(R.id.tv_plane_name);
        iv_plane_image = findViewById(R.id.iv_plane_image);
        status_text = findViewById(R.id.status_text);
        btn_options = findViewById(R.id.button_options);
        btn_connect = findViewById(R.id.button_connect);
        btn_options.setOnClickListener(this);
        btn_connect.setOnClickListener(this);
        //debug
        AppDatabase dbHelper = new AppDatabase(this);
        dbHelper.getReadableDatabase();

        refreshUi();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragments_container, new FirstFragment())
                    .commit();
        }
    }

    /**
     * Manage click events on buttons
     *
     * @param view
     */

    @Override
    public void onClick(View view) {
        int id = view.getId();
        //If button connect, create a new connection thread
        if (id == R.id.button_connect) {
            Log.d("MainActivity", "connection status: " + connection.getStatus());
            if (connection.getStatus()) {//if is connected
                connection.shutDown();
                runOnUiThread(() -> {
                    status_text.setTextColor(getResources().getColor(R.color.red));
                    status_text.setText("Disconnected");
                    btn_connect.setEnabled(true);
                    btn_connect.setText("Connect");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        btn_connect.setBackground(getDrawable(R.drawable.custom_button_background));
                    }
                });
            } else {
                runOnUiThread(() -> {
                    status_text.setTextColor(getResources().getColor(R.color.orange));
                    status_text.setText("Connecting...");
                    btn_connect.setEnabled(false);
                });
                connectorThread = new Thread(connection);
                connectorThread.start();
            }
        } else if (id == R.id.button_options) {
            Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragments_container);
            if (!(current instanceof AircraftListFragment)){
                showFragment(new AircraftListFragment());
            }

        }
    }


    /**
     * Connection listener methods.
     * Performs an action when connected event occurs
     * In this case we change the status and button text
     */
    @Override
    public void onConnected() {
        try {
            runOnUiThread(() -> {
                status_text.setText("Connected");
                status_text.setTextColor(0xFF01FF00);
                btn_connect.setText("Disconnect");
                btn_connect.setEnabled(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    btn_connect.setBackground(getDrawable(R.drawable.custom_button_background_red));
                }
            });
        } catch (Exception e) {
            Log.d("connection", e.toString());
        }
    }

    @Override
    public void onError(String error) {
        try {
            runOnUiThread(() -> {
                status_text.setText("Disconnected");
                status_text.setTextColor(0xFFF02828);
                btn_connect.setText("Connect");
                btn_connect.setEnabled(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    btn_connect.setBackground(getDrawable(R.drawable.custom_button_background));
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Connection error").setMessage("Unable to connect to receiver. Check WIFI is active and connected to the right network");
                builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                builder.create().show();

            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Méthod to change fragments.
     * This allows one fragment to call another.
     *
     * @param fragment
     */
    public void showFragment(Fragment fragment) {

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                )
                .replace(R.id.fragments_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Interface method to get connection status from a fragment.
     *
     * @return
     */
    @Override
    public boolean getConnectionStatus() {
        return this.connection.getStatus();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                Log.d("VolumeButton", "Botón Subir Volumen presionado");
                payload.setThrottle((short) 1000);
                connection.sendPayload();
                onMotorStop();
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                payload.setThrottle((short) 1000);
                connection.sendPayload();
                onMotorStop();
                return true; // Similar al caso de Subir Volumen
        }
        // Si no es una tecla de volumen que te interese, deja que el sistema la maneje
        return super.onKeyDown(keyCode, event);
    }



    private void refreshUi(){
        SharedPreferences prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        int selectedAircraftId = prefs.getInt("selected_aircraft_id", -1);
        if(selectedAircraftId != -1){
            AircraftDaoSQLite AircraftDaoSQLite = new AircraftDaoSQLite(this);
            Aircraft aircraft = AircraftDaoSQLite.getAircraft(selectedAircraftId);
            if(aircraft != null){
                tv_plane_name.setText(aircraft.getName());
                iv_plane_image.setImageURI(Uri.parse(aircraft.getImage()));
            }
        }
    }

    @Override
    public void onAircraftSelected(int aircraftId) {
        refreshUi();
    }

    @Override
    public void onMotorStop() {
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragments_container);
        if (current instanceof TimedFlightFragment) {
            ((TimedFlightFragment) current).stopChronoFromActivity();
        }

    }
}