package com.example.project2;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class forgotpassword extends AppCompatActivity {
    private EditText editUsernameText; // Declare EditText variables
    private LoginDatabase db; // Declare LoginDatabase variable
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.forgotpassword); // Set the layout for the activity to mainloginscreen.xml
        TextView textView = findViewById(R.id.textView2); // Establish textView variable as textView editText
        editUsernameText = findViewById(R.id.editUsernameText); // Establish editUsernameText variable as editUsernameText editText
        Button forgotButton = findViewById(R.id.submitButton); // Establish loginButton variable as loginButton button
        Button backButton = findViewById(R.id.backButton); // Establish registerButton variable as registerButton button
        db = new LoginDatabase(this); // Establish db variable as LoginDatabase


        forgotButton.setOnClickListener(v -> { // Set onClickListener for submit
            String username = editUsernameText.getText().toString(); // Get text from editUsernameText and store it in username variable
            Cursor cursor = db.getUserPassword(username);
            if (editUsernameText.getText().toString().isEmpty()) { // Check if username is filled in
                editUsernameText.setError("Please enter username");
            }else if (cursor != null && cursor.moveToFirst()) { // Checks if user is in database
                String password = cursor.getString(0);
                cursor.close();
                textView.setText(password);
            } else { // If user is not valid
                Toast.makeText(forgotpassword.this, "Username does not exist", Toast.LENGTH_SHORT).show(); // Display toast message invalid username
            }
        });
        backButton.setOnClickListener(v -> { // Set onClickListener for registerButton
            Intent intent1 = new Intent(forgotpassword.this, login.class); // Create intent to login activity

            startActivity(intent1); // Start the login activity
        });

    }
}
