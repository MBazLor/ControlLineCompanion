package com.mbl.controllinecompanion.controller;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.mbl.controllinecompanion.R;
import com.mbl.controllinecompanion.model.FlightConfig.FlightConfig;
import com.mbl.controllinecompanion.model.aircraft.Aircraft;
import com.mbl.controllinecompanion.model.aircraft.AircraftDaoSQLite;
import com.mbl.controllinecompanion.model.aircraft.IAircraftDAO;

import java.io.File;
import java.io.IOException;

public class NewAircraftActivity extends AppCompatActivity {

    Uri fotoUri;
    ImageView image;
    EditText nameField, wingspanField, lineLengthField;

    private IAircraftDAO aircraftDAO; //Data access object for aircraft
    Aircraft aircraft;
    private ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null) {
                            fotoUri = uri;
                            image.setImageURI(uri);
                        }
                    });

    private ActivityResultLauncher<Uri> takePhotoLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(),
                    success -> {
                        if (success && fotoUri != null) {
                            image.setImageURI(fotoUri);
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_aircraft);

        aircraftDAO = new AircraftDaoSQLite(this);

        image = findViewById(R.id.image_aircraft);
        nameField = findViewById(R.id.text_name);
        wingspanField = findViewById(R.id.text_wingspan);
        lineLengthField = findViewById(R.id.text_line_length);

        if (getIntent().getBooleanExtra("update", false)) {
            int aircraftId = getIntent().getIntExtra("aircraft_id", -1);
            aircraft = aircraftDAO.getAircraft(aircraftId);
            nameField.setText(aircraft.getName());
            image.setImageURI(Uri.parse(aircraft.getImage()));
            wingspanField.setText(String.valueOf(aircraft.getWingspan()));
            lineLengthField.setText(String.valueOf(aircraft.getLineLength()));
            fotoUri = Uri.parse(aircraft.getImage());
        }

        findViewById(R.id.button_gallery).setOnClickListener(v -> abrirGaleria());
        findViewById(R.id.button_photo).setOnClickListener(v -> tomarFoto());
        findViewById(R.id.button_save).setOnClickListener(v -> saveAircraft());
        findViewById(R.id.button_cancel).setOnClickListener(v -> finish());
    }

    private void abrirGaleria() {
        pickImageLauncher.launch("image/*");
    }

    private void tomarFoto() {
        try {
            // Carpeta propia y persistente para tus fotos
            File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File archivo = File.createTempFile(
                    "foto_avion_",
                    ".jpg",
                    dir
            );

            fotoUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    archivo
            );

            takePhotoLauncher.launch(fotoUri);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void saveAircraft(){
        if(aircraft == null){
            aircraft = new Aircraft();
            FlightConfig fc = new FlightConfig();
            aircraft.setFlightConfig(fc);
        }

        aircraft.setName(nameField.getText().toString());
        aircraft.setWingspan(Float.parseFloat(wingspanField.getText().toString()));
        aircraft.setLineLength(Float.parseFloat(lineLengthField.getText().toString()));
        if (fotoUri != null)
            aircraft.setImage(fotoUri.toString());
        if (getIntent().getBooleanExtra("update", false)){
            aircraftDAO.updateAircraft(aircraft);
        }
        else{
            aircraftDAO.addAircraft(aircraft);
        }

        finish();
    }

}