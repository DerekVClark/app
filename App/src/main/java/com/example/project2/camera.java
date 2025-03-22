package com.example.project2;

import android.app.Dialog;

import android.content.pm.PackageManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class camera extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        checkCameraPermissions();
    }

    private void checkCameraPermissions(){
        Dialog cameraDialog = new Dialog(this);
        cameraDialogue.setContentView(R.layout.cameraRequest);
        Button button_yes = cameraDialog.findViewById(R.id.button_yes);
        Button button_no = cameraDialog.findViewById(R.id.button_no);

        button_yes.setOnClickListener(v -> { // Set click listeners for button yes
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){ //If permission not already granted when yes is clicked
                ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.CAMERA}, 1); //Request permission
            } else{ //If permission already granted when yes is clicked
                Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show(); //Display toast permission already granted

            }
            cameraDialog.dismiss(); //dismiss dialog
        });
        button_no.setOnClickListener(v ->{ // Set click listener for button no
            cameraDialog.dismiss(); //dismiss dialog
        });

        cameraDialog.show(); //show dialog

    }
}


