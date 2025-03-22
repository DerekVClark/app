package com.example.project2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

public class register extends AppCompatActivity {

    private EditText editUsernameText, editPasswordText; // Declare EditText variables for registering new accounts
    private LoginDatabase db; // Declare LoginDatabase variable
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.registerscreen); // Set the layout for the activity to registerscreen.xml
        editUsernameText = findViewById(R.id.editUsernameText); // Find the EditText views by their id username field
        editPasswordText = findViewById(R.id.editPasswordText); // Find the EditText views by their id password field
        Button registerButton = findViewById(R.id.registerButton); // Find the Button views by their id registerButton
        Button button_back = findViewById(R.id.button_back); // Find the Button views by their id button_back
        db = new LoginDatabase(this); // Create an instance of LoginDatabase

        button_back.setOnClickListener(v -> { // Set a click listener for the button_back Button
            Intent intent = new Intent(register.this, login.class); // Create an intent to navigate to the login activity
            startActivity(intent); // Start the login activity
        });

        registerButton.setOnClickListener(v -> { // Set a click listener for the registerButton Button
            if (editUsernameText.getText().toString().isEmpty() || editPasswordText.getText().toString().isEmpty()) { // Check if the EditText fields are empty
                editUsernameText.setError("Please enter username"); // Set an error message for the username EditText
                editPasswordText.setError("Please enter password"); // Set an error message for the password EditText
            } else if (db.checkExistsUser(editUsernameText.getText().toString())){ // Check if the username already exists in the database
                Toast.makeText(register.this, "User already exists", Toast.LENGTH_SHORT).show(); // If user exusts show a toast message informing user they already have an account
            } else { // If user does not exist add user to database
                boolean insert = db.insertData(editUsernameText.getText().toString(), editPasswordText.getText().toString()); // Insert user data into the database
                if (insert) {
                    Toast.makeText(register.this, "User added successfully", Toast.LENGTH_SHORT).show(); // Show a toast message informing user they have been added
                    Intent intent = new Intent(register.this, login.class); // Create an intent to navigate to the login activity
                    startActivity(intent); // Start the login activity
                }
            }
        });
    }

}
