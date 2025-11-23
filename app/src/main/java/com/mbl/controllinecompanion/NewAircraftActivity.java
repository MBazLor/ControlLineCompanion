package com.mbl.controllinecompanion;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.IOException;

public class NewAircraftActivity extends AppCompatActivity {

    Uri fotoUri;
    ImageView image;
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

        image = findViewById(R.id.image_aircraft);

        findViewById(R.id.button_gallery).setOnClickListener(v -> abrirGaleria());
        findViewById(R.id.button_photo).setOnClickListener(v -> tomarFoto());


    }

    private void abrirGaleria() {
        pickImageLauncher.launch("image/*");
    }

    private void tomarFoto() {
        try {
            File archivo = File.createTempFile("foto_avion_", ".jpg", getCacheDir());
            fotoUri = FileProvider.getUriForFile(this,
                    getPackageName() + ".provider", archivo);
            takePhotoLauncher.launch(fotoUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}