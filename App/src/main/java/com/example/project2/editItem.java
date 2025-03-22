package com.example.project2;

import android.content.Intent;
import android.os.Bundle;

import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class editItem extends AppCompatActivity {
    private LoginDatabase db; // Declare LoginDatabase variable
    private EditText editItemName, editItemQuantity; // Declare EditText variables

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editscreen); // Set the layout for the activity to editscreen.xml
        editItemName = findViewById(R.id.editItemName); //Save item name to editItemName variable
        editItemQuantity = findViewById(R.id.editItemQuantity); //Save item quantity to editItemQuantity variable
        db = new LoginDatabase(this); // Initialize the database
        Button saveButton = findViewById(R.id.editItem); //Save button to saveButton variable
        Button backButton = findViewById(R.id.button_back); //Back button to backButton variable

        Intent intent = getIntent(); //Get the intent that started this activity
        String itemName = intent.getStringExtra("ITEM_NAME"); //Get the item name from the intent
        int itemQuantity = intent.getIntExtra("ITEM_QUANTITY", 0); //Get the item quantity from the intent or 0 if not found
        int itemId = intent.getIntExtra("ITEM_ID", 0); //Get the item ID from the intent or 0 if not found
        editItemName.setText(itemName); //Set the text of editItemName to the item name
        editItemQuantity.setText(String.valueOf(itemQuantity)); //Set the text of editItemQuantity to the item quantity

        saveButton.setOnClickListener(v -> { //Save button click listener
            String updatedName = editItemName.getText().toString(); //Get the updated item name from editItemName
            int updatedQuantity = Integer.parseInt(editItemQuantity.getText().toString()); //Get the updated item quantity from editItemQuantity

            db.updateItem(itemId, updatedName, updatedQuantity); //Update the item in the database
            if(updatedQuantity == 0){ //If the item quantity is 0
                sendSmsNotification(updatedName); //Send a notification
            }

            //return to the inventory screen
            finish();
        });
        backButton.setOnClickListener(v -> { //Back button click listener
            finish(); //Finish the activity
        });



    }
    private void sendSmsNotification(String updatedName){
        String number = "123"; // Sets phone number for sms to send to
        String message = updatedName + "Is out of stock"; // Sets sms message to item name is out of stock
        try {
            SmsManager smsManager = SmsManager.getDefault(); //Initialize smsmanager
            smsManager.sendTextMessage(number, null, message, null, null); //Send text message
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show(); // If message sent succesful popup
        } catch (Exception e) { // If sms fails to send
            Toast.makeText(getApplicationContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();

        }
    }
}
