package com.example.project2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class zeroItem extends AppCompatActivity {

    private LoginDatabase db; // Declare LoginDatabase variable
    private LinearLayout linearLayout; // Declare LinearLayout variable

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.zeroes); // Set the layout for this activity to zeroes.xml
        Intent intent = getIntent(); // Get intent
        String username = intent.getStringExtra("username"); // Get passed in username
        db = new LoginDatabase(this); // Initializes database
        linearLayout = findViewById(R.id.itemList); // Initialize LinearLayout through id
        Button backButton = findViewById(R.id.button_back); // Initialize back button through id

        loadInventory(username); //Load inventory from database passing in username
        backButton.setOnClickListener(v -> { // Set on click listener for back button
            Intent intent1 = new Intent(zeroItem.this, Inventory.class); // Set intent for inventory
            intent1.putExtra("username", username); // Set username in intent
            startActivity(intent1); // Start inventory activity
        });


    }

    @Override
    protected void onResume() { // Override onResume to update inventory
        super.onResume(); // Call superclass onResume
        loadInventory(getIntent().getStringExtra("username")); // Load inventory with username from intent
    }

    @SuppressLint("DefaultLocale")
    private void loadInventory(String username) { // Load inventory from database given username
        Cursor cursor = db.getZeroItems(username); // Get cursor from database utilizing get zeros method
        linearLayout.removeAllViews(); // Remove all views from linear layout
        if (cursor != null && cursor.getCount() > 0) { // If cursor is not null and items exists
            if (cursor.moveToFirst()) { // Move to first item in zeros
                do {
                    try {
                        // Get values from database columns
                        String itemName = cursor.getString(cursor.getColumnIndexOrThrow("item_name"));
                        int itemQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                        int itemId = cursor.getInt(cursor.getColumnIndexOrThrow("item_id"));

                        // Dynamically create a new card view
                        View cardView = getLayoutInflater().inflate(R.layout.card_layout, linearLayout, false); // Inflate card layout
                        // Initialize views within the card
                        TextView itemNameView = cardView.findViewById(R.id.item_name);
                        TextView itemNumberView = cardView.findViewById(R.id.item_number);
                        TextView itemQuantityView = cardView.findViewById(R.id.item_quantity);
                        Button deleteButton = cardView.findViewById(R.id.button_delete);


                        // Set the data from the inventory to the appropriate view
                        itemNameView.setText(String.format("%s%s", getString(R.string.item_name), itemName));
                        itemNumberView.setText(String.format("%s%d", getString(R.string.item_number), itemId));
                        itemQuantityView.setText(String.valueOf(itemQuantity));

                        // Add the card to the LinearLayout
                        linearLayout.addView(cardView);

                        cardView.setOnClickListener(v -> { // Set on click listener for card
                            Intent intent2 = new Intent(zeroItem.this, editItem.class); // Set intent for edit item
                            intent2.putExtra("ITEM_NAME", itemName); // Set item name in intent
                            intent2.putExtra("ITEM_QUANTITY", itemQuantity); // Set item quantity in intent
                            intent2.putExtra("ITEM_ID", itemId); // Set item id in intent
                            startActivity(intent2); // Start edit item activity
                        });
                        deleteButton.setOnClickListener(v -> { // Set on click listener for delete button
                            db.deleteItem(itemId); // Delete item from database
                            loadInventory(username); // Reload inventory

                        });


                    } catch (IllegalArgumentException e) {
                        e.printStackTrace(); // Log the error in case a column is missing
                    }
                } while (cursor.moveToNext()); // Move to next item in cursor
            }
        }
    }
}
