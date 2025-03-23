package com.example.project2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;

import android.content.Intent;
import android.content.pm.PackageManager;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;


import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.core.CameraSelector;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.concurrent.ExecutionException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class camerascreen extends AppCompatActivity { // Estabishes camerascreen class
    private PreviewView previewView; // Establishes preview view
    int cameraFacing = CameraSelector.LENS_FACING_BACK; // Establishes camera facing to back facing camera
    private ExecutorService cameraExecutor; // Establishes camera executor
    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);
        cameraExecutor = Executors.newSingleThreadExecutor(); // Creates a single thread executor
        previewView = findViewById(R.id.viewFinder); // Sets preview view to viewFinder in camera.xml
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) { // Checks if camera permission is granted
            checkCameraPermissions(); // If not, requests permission
        } else { // If permission is granted, starts camera
            startCamera(cameraFacing); // Starts camera
        }

    }

    private void checkCameraPermissions(){ // Checks if camera permission is granted
        Dialog cameraDialog = new Dialog(this); // Displays dialog for camera request permission
        cameraDialog.setContentView(R.layout.camerarequest); // Sets content view to camerarequest.xml
        Button button_yes = cameraDialog.findViewById(R.id.button_yes); // Sets buttons to camerarequest.xml
        Button button_no = cameraDialog.findViewById(R.id.button_no); // Sets buttons to camerarequest.xml


        button_yes.setOnClickListener(v -> { // Set click listeners for button yes
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){ //If permission not already granted when yes is clicked
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, 1); //Request permission
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

    public void startCamera(int cameraFacing) { // Starts camera
        int aspectRatio = aspectRatio(previewView.getWidth(), previewView.getHeight()); // Gets aspect ratio of camera
        ListenableFuture<ProcessCameraProvider> listenableFuture = ProcessCameraProvider.getInstance(this); // Gets camera provider

        listenableFuture.addListener(() -> { // Adds listener to camera provider
                try{ // Tries to get camera provider
                    ProcessCameraProvider cameraProvider = listenableFuture.get(); // Gets camera provider
                    Preview preview = new Preview.Builder().setTargetAspectRatio(aspectRatio).build(); // Sets preview


                    CameraSelector cameraSelector = new CameraSelector.Builder() // Sets camera selector
                            .requireLensFacing(cameraFacing).build();

                    cameraProvider.unbindAll(); // Unbinds all cameras
                    preview.setSurfaceProvider(previewView.getSurfaceProvider()); // Sets surface provider

                    ImageAnalysis imageAnalysis = new ImageAnalysis.Builder() // Sets image analysis
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) // Sets backpressure strategy
                            .build(); // Builds image analysis

                    imageAnalysis.setAnalyzer(cameraExecutor, this::scanBarcode); // Sets analyzer to scan barcode uses camerax camera analysis

                    cameraProvider.bindToLifecycle(camerascreen.this, cameraSelector, preview, imageAnalysis); // Binds to lifecycle


                } catch (ExecutionException | InterruptedException e){ // Catches execution exception
                    e.printStackTrace();
                }
            }, ContextCompat.getMainExecutor(this)); // Gets main executor
        }

    private int aspectRatio(int width, int height){ // Gets aspect ratio
        double previewRatio = (double) Math.max(width, height) / Math.min(width, height); // Gets preview ratio
        if(Math.abs(previewRatio - 4.0 / 3.0) <= Math.abs(previewRatio - 16.0 / 9.0)) { // Checks if preview ratio is 4:3 or 16:9
            return AspectRatio.RATIO_4_3; // If 4:3, returns 4:3
        }
        return AspectRatio.RATIO_16_9; // Returns 16:9
    }

    @SuppressLint("UnsafeOptInUsageError")
    private void scanBarcode(ImageProxy imageProxy) { // Processes image frame from camera to detect barcode and extract data
        Log.d("BarcodeScanner", "Processing frame..."); // Logs processing frame to detect function is working
        @SuppressLint("UnsafeOptInUsageError") Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) { // Checks if media image is not null
            InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees()); // Creates input image from media image
            BarcodeScanner scanner = BarcodeScanning.getClient(); // Creates barcode scanner
            scanner.process(image) // Processes image
                    .addOnSuccessListener(barcodes -> { // Adds success listener on barcode success
                        for (Barcode barcode : barcodes) { // Loops through barcodes
                            String barcodeData = barcode.getRawValue(); // Gets barcode data from barcode
                            assert barcodeData != null; // Asserts barcode data is not null
                            String[] parts = barcodeData.split(":"); // Splits barcode data by : to get item number:item name
                            if(parts.length == 2){ // Checks if barcode data is valid holding both parts seperated by :
                                String itemNumber = parts[0]; // Gets item number from barcode data at [0]
                                String name = parts[1]; // Gets item name from barcode data at [1]

                                Intent resultIntent = new Intent(); // Creates result intent
                                resultIntent.putExtra("Item_number", itemNumber); // Puts item number and name into result intent
                                resultIntent.putExtra("Item_name", name);
                                setResult(RESULT_OK, resultIntent); // Sets result to ok
                                finish(); // Finishes activity returns to add item screen with data
                            } else{
                                Log.d("BarcodeScanner", "Improper barcode data"); // Logs error
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.e("BarcodeScanner", "Barcode scanning failed", e)) // Adds failure listener on barcode failure
                    .addOnCompleteListener(task -> imageProxy.close()); // Close image after processing
        }
    }
}




