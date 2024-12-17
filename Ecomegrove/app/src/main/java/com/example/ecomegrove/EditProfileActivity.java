package com.example.ecomegrove;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editName, editEmail, editUsername, editPassword;
    private String nameUser, emailUser, usernameUser, passwordUser;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize EditTexts
        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);

        // Initialize Firebase Database reference
        reference = FirebaseDatabase.getInstance().getReference("Users");

        // Call the method to display data
        showData();
    }

    public void showData() {
        // Get intent data
        Intent intent = getIntent();

        // Retrieve the intent data and handle potential null values
        nameUser = intent.getStringExtra("name");
        emailUser = intent.getStringExtra("email");
        usernameUser = intent.getStringExtra("username");
        passwordUser = intent.getStringExtra("password");

        // Add null checks for usernameUser before using it
        if (usernameUser == null || usernameUser.isEmpty()) {
            Toast.makeText(this, "Username is missing!", Toast.LENGTH_SHORT).show();
            return; // Don't continue if username is null or empty
        }

        // Set retrieved values to the EditTexts
        editName.setText(nameUser);
        editEmail.setText(emailUser);
        editUsername.setText(usernameUser);
        editPassword.setText(passwordUser);

        // Fetch data from Firebase and handle it (if needed)
        reference.child(usernameUser).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Handle successful data fetch from Firebase
                // Example: You can extract more data and display it
            } else {
                Toast.makeText(EditProfileActivity.this, "Failed to retrieve data!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
