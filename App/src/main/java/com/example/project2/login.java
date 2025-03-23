package com.example.project2;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class login extends AppCompatActivity {
    private EditText editUsernameText, editPasswordText; // Declare EditText variables
    private LoginDatabase db; // Declare LoginDatabase variable
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mainloginscreen); // Set the layout for the activity to mainloginscreen.xml
        editUsernameText = findViewById(R.id.editUsernameText); // Establish editUsernameText variable as editUsernameText editText
        editPasswordText = findViewById(R.id.editPasswordText); // Establish editPasswordText variable as editPasswordText editText
        // Declare Button variables
        Button loginButton = findViewById(R.id.loginButton); // Establish loginButton variable as loginButton button
        Button registerButton = findViewById(R.id.registerButton); // Establish registerButton variable as registerButton button
        Button forgotPasswordButton = findViewById(R.id.forgotPasswordButton); // Establish forgot password button variable as forgot password button
        db = new LoginDatabase(this); // Establish db variable as LoginDatabase

        loginButton.setOnClickListener(v -> { // Set onClickListener for loginButton
            String username = editUsernameText.getText().toString(); // Get text from editUsernameText and store it in username variable
            String password = editPasswordText.getText().toString(); // Get text from editPasswordText and store it in password variable
            if (editUsernameText.getText().toString().isEmpty() || editPasswordText.getText().toString().isEmpty()) { // Check if username or password is empty
                editUsernameText.setError("Please enter username");
                editPasswordText.setError("Please enter password");
            }else if (db.checkUser(username, password)) { // Check if user is valid
                Intent intent = new Intent(login.this, Inventory.class); // Create intent to Inventory activity
                intent.putExtra("username", username); // Pass the username to the next activity
                startActivity(intent); // Start the Inventory activity
            } else { // If user is not valid
                Toast.makeText(login.this, "Username or password does not exist", Toast.LENGTH_SHORT).show(); // Display toast message invalid login
            }
        });
        registerButton.setOnClickListener(v -> { // Set onClickListener for registerButton
            Intent intent = new Intent(login.this, register.class); // Create intent to register activity

            startActivity(intent); // Start the register activity
        });

        forgotPasswordButton.setOnClickListener(v -> { // Set onClickListener for forgot password button
            Intent intent1 = new Intent(login.this, forgotpassword.class); // Create intent to forgot password activity
            startActivity(intent1);
        });
    }
}
