package com.example.project2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class addItem extends AppCompatActivity {
    private EditText editItemNumber, editItemName, editItemQuantity; // Declare EditText variables
    private LoginDatabase db; // Declare LoginDatabase variable
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.addscreen); // Set the layout for the activity to loadscreen.xml
        editItemNumber = findViewById(R.id.editItemNumber); // Sets itemNUmber to the editItemNumber variable
        editItemName = findViewById(R.id.editItemName); // Sets itemName to the editItemName variable
        editItemQuantity = findViewById(R.id.editItemQuantity); // Sets itemQuantity to the editItemQuantity variable
        Intent intent = getIntent(); // Get the intent that started this activity
        String username = intent.getStringExtra("username"); // Get the username from the intent
        db = new LoginDatabase(this); // Create a new instance of LoginDatabase
        // Declare Button variables
        Button addItem = findViewById(R.id.editItem);
        Button button_back = findViewById(R.id.button_back);
        Button camera = findViewById(R.id.barcode_scanner);

        addItem.setOnClickListener(v -> { // Set a click listener for the addItem button
            String itemNumber = editItemNumber.getText().toString(); // Get the text from the editItemNumber EditText and store it in the itemNumber variable
            String itemName = editItemName.getText().toString(); // Get the text from the editItemName EditText and store it in the itemName variable
            String itemQuantity = editItemQuantity.getText().toString(); // Get the text from the editItemQuantity EditText and store it in the itemQuantity variable

            if (itemNumber.isEmpty() || itemName.isEmpty() || itemQuantity.isEmpty()) { // Check if any of the fields are empty
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show(); // Display a toast message all fields must be filled
            } else {
                boolean inserted = db.insertItem(Integer.parseInt(itemNumber), itemName, Integer.parseInt(itemQuantity), username); // Insert the item into the database and store the result in the inserted variable

                if (inserted) { // Check if the item was inserted successfully
                    Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show(); // Display a toast message that the item was added successfully
                    finish(); // Close the activity
                } else { // If the item was not inserted successfully
                    Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show(); // Display a toast message that the item was not added successfully
                }
            }

        });
        button_back.setOnClickListener(v -> { // Set a click listener for the button_back button
            Intent intent1 = new Intent(addItem.this, Inventory.class); // Create a new intent to start the Inventory activity
            intent1.putExtra("username", username); // Pass the username to the Inventory activity
            startActivity(intent1); // Start the Inventory activity
        });

        camera.setOnClickListener(v -> { // Set a click listener for the camera button
            Intent intent1 = new Intent(addItem.this, camerascreen.class); // Creates a new intent to start the camera activity
            startActivityForResult(intent1, 1); // Waits for barcode data
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) { // If result code ok
            if (data != null) {
                String itemNumber = data.getStringExtra("Item_number"); // Gets item number from barcode data
                String itemName = data.getStringExtra("Item_name"); // Gets item name from barcode data


                editItemNumber.setText(itemNumber); // Sets text field to item number
                editItemName.setText(itemName); //Sets text field to item name
            }
        }
    }
}
