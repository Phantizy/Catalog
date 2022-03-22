package com.example.catalog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import java.io.File;

public class NotesImageActivity extends AppCompatActivity {
    private final static String APP_DIR = "Catalog";
    private final static String PICTURE_FILE = "image_001.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_image);
    }

    // method came from activity_notes_image.xml -> *btnCaptureImage*
    public void onCaptureImage(View view) {
        // Store photo in public pictures directory on SD card in our own subdirectory
        File publicPix = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
        );
        File imageFolder = new File(publicPix, APP_DIR);

        if(!imageFolder.isDirectory()){
            imageFolder.mkdirs();
        }

        // Start a camera intent to save picture to file
        File image = new File(imageFolder, PICTURE_FILE);
        Uri uriSavedImage = Uri.fromFile(image);
        Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        startActivityForResult(imageIntent, 0);
    }
}