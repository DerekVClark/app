package com.example.project2;

import android.app.Dialog;

import android.content.pm.PackageManager;

import android.os.Bundle;
import android.widget.Button;


import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Preview;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.core.CameraSelector;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;


public class camerascreen extends AppCompatActivity {
    private PreviewView previewView;
    int cameraFacing = CameraSelector.LENS_FACING_BACK;
    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);
        previewView = findViewById(R.id.viewFinder);
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            checkCameraPermissions();
        } else {
            startCamera(cameraFacing);
        }

    }

    private void checkCameraPermissions(){
        Dialog cameraDialog = new Dialog(this);
        cameraDialog.setContentView(R.layout.camerarequest);
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

    public void startCamera(int cameraFacing) {
        int aspectRatio = aspectRatio(previewView.getWidth(), previewView.getHeight());
        ListenableFuture<ProcessCameraProvider> listenableFuture = ProcessCameraProvider.getInstance(this);

        listenableFuture.addListener(() -> {
                try{
                    ProcessCameraProvider cameraProvider = (ProcessCameraProvider) listenableFuture.get();
                    Preview preview = new Preview.Builder().setTargetAspectRatio(aspectRatio).build();

                    //ImageCapture imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                            //.setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();

                    CameraSelector cameraSelector = new CameraSelector.Builder()
                            .requireLensFacing(cameraFacing).build();

                    cameraProvider.unbindAll();
                    preview.setSurfaceProvider(previewView.getSurfaceProvider());
                    cameraProvider.bindToLifecycle(camerascreen.this, cameraSelector, preview);


                } catch (ExecutionException | InterruptedException e){
                    e.printStackTrace();
                }
            }, ContextCompat.getMainExecutor(this));
        }

    private int aspectRatio(int width, int height){
        double previewRatio = (double) Math.max(width, height) / Math.min(width, height);
        if(Math.abs(previewRatio - 4.0 / 3.0) <= Math.abs(previewRatio - 16.0 / 9.0)) {
            return AspectRatio.RATIO_4_3;
        }
        return width;
    }
}




