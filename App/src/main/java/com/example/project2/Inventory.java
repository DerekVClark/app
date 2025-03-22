package com.example.project2;



import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class Inventory extends AppCompatActivity {
    private LoginDatabase db; // Declare LoginDatabase variable
    private LinearLayout linearLayout; // Declare LinearLayout variable


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory); //Set screen to inventory.xml
        Intent intent = getIntent(); // Get the intent that started this activity
        String username = intent.getStringExtra("username"); // Get the username from the intent
        db = new LoginDatabase(this); // Initialize LoginDatabase
        linearLayout = findViewById(R.id.itemList); // Initialize LinearLayout
        Button button_add = findViewById(R.id.button_add); // Initialize Button for transitioning to add screen
        Button button_zero = findViewById(R.id.button_zero); // Initialize Button for transitioning to zero screen
        Button button_sms = findViewById(R.id.button_sms); // Initialize Button for sms popup
        loadInventory(username); // Load the inventory for the user based on the logged in username


        button_add.setOnClickListener(v -> { // Set click listener for the add button
            Intent intent1 = new Intent(Inventory.this, addItem.class); // Create an intent to transition to the add screen
            intent1.putExtra("username", username); // Pass the username to the add screen
            startActivity(intent1); //Start activity
        });
        button_zero.setOnClickListener(v -> { // Set click listener for the zero button
            Intent intent3 = new Intent(Inventory.this, zeroItem.class); // Create an intent to transition to the zero screen
            intent3.putExtra("username", username); // Pass the username to the zero screen
            startActivity(intent3);
        });
        button_sms.setOnClickListener(v->{ // Sms button to display popup
            displaySmsPopup(); // Popup to display
        });





    }
    @Override
    protected void onResume() { // Override onResume to refresh the inventory when the activity is resumed
        super.onResume();
        loadInventory(getIntent().getStringExtra("username"));
    }

    private void displaySmsPopup(){ // Display popup to request sms permission
        Dialog smsDialog = new Dialog(this); // Create dialog
        smsDialog.setContentView(R.layout.smsrequest); // Set content view to smsrequest.xml
        Button button_yes = smsDialog.findViewById(R.id.button_yes); // Initialize buttons for yes and no
        Button button_no = smsDialog.findViewById(R.id.button_no);

        button_yes.setOnClickListener(v -> { // Set click listeners for button yes
            if(ContextCompat.checkSelfPermission(Inventory.this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){ //If permission not already granted when yes is clicked
                ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.SEND_SMS}, 1); //Request permission
            } else{ //If permission already granted when yes is clicked
                Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show(); //Display toast permission already granted

            }
            smsDialog.dismiss(); //dismiss dialog
        });
        button_no.setOnClickListener(v ->{ // Set click listener for button no
            smsDialog.dismiss(); //dismiss dialog
        });

        smsDialog.show(); //show dialog

    }
    @SuppressLint("DefaultLocale")
    private void loadInventory(String username) { // Load the inventory for the user based on the logged in username
        Cursor cursor = db.getUserItems(username); // Gets user items
        linearLayout.removeAllViews(); // Clear the linear layout of any previous views before loading the new ones
        if (cursor != null && cursor.getCount() > 0) { // Check if cursor is not null and has items
            if (cursor.moveToFirst()) { //Moves to first item
                do {
                    try {
                        // Set variables for each column data including name quantity and id of item
                        String itemName = cursor.getString(cursor.getColumnIndexOrThrow("item_name"));
                        int itemQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                        int itemId = cursor.getInt(cursor.getColumnIndexOrThrow("item_id"));

                        // Dynamically create a new card view
                        View cardView = getLayoutInflater().inflate(R.layout.card_layout, linearLayout, false); // Inflate card layout
                        // Get the views from the card view
                        TextView itemNameView = cardView.findViewById(R.id.item_name);
                        TextView itemNumberView = cardView.findViewById(R.id.item_number);
                        TextView itemQuantityView = cardView.findViewById(R.id.item_quantity);
                        Button deleteButton = cardView.findViewById(R.id.button_delete);



                        // Set the data from the inventory to the card values
                        itemNameView.setText(String.format("%s%s", getString(R.string.item_name), itemName));
                        itemNumberView.setText(String.format("%s%d", getString(R.string.item_number), itemId));
                        itemQuantityView.setText(String.valueOf(itemQuantity));

                        // Add the card to the LinearLayout
                        linearLayout.addView(cardView);

                        cardView.setOnClickListener(v ->{ // Set click listener for card view to transition to edit screen
                            Intent intent2 = new Intent(Inventory.this, editItem.class); // Create an intent to transition to the edit screen
                            intent2.putExtra("ITEM_NAME", itemName); // Pass the item name to the edit screen
                            intent2.putExtra("ITEM_QUANTITY", itemQuantity); // Pass the item quantity to the edit screen
                            intent2.putExtra("ITEM_ID", itemId); // Pass the item id to the edit screen
                            startActivity(intent2); // Start edit screen
                        });
                        deleteButton.setOnClickListener(v -> { // Set click listener for delete button to delete item from database
                            db.deleteItem(itemId); // Delete item from database using deleteItem method
                            loadInventory(username); // Reload inventory after deletion

                        });


                    } catch (IllegalArgumentException e) {
                        e.printStackTrace(); // Log the error in case a column is missing
                    }
                } while (cursor.moveToNext()); // Move to the next item in the cursor
            }
    }
}
}