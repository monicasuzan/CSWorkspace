package com.example.myadmin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends Activity {

    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase reference for excelUsers node.
        databaseRef = FirebaseDatabase.getInstance().getReference("excelUsers");

        EditText emailField = findViewById(R.id.login_email);
        EditText passwordField = findViewById(R.id.login_password);
        Button loginButton = findViewById(R.id.btn_login);

        loginButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (validateEmail(email) && validatePassword(password)) {
                authenticateUser(email, password);
            } else {
                Toast.makeText(MainActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validatePassword(String password) {
        return !password.isEmpty();
    }

    private void authenticateUser(String email, String password) {
        // Admin check
        if (email.equals("admin@stellamariscollege.edu.in") && password.equals("Admin@123")) {
            startActivity(new Intent(MainActivity.this, AdminActivity.class));
            return;
        }

        String sanitizedEmail = email.replace(".", ",");
        databaseRef.child(sanitizedEmail).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    String dbPassword = snapshot.child("password").getValue(String.class);
                    String name = snapshot.child("name").getValue(String.class);

                    if (dbPassword != null && dbPassword.equals(password)) {
                        Toast.makeText(MainActivity.this, "Welcome " + name, Toast.LENGTH_SHORT).show();

                        // Pass the actual user email to the HomeActivity
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        intent.putExtra("USER_EMAIL", email);
                        startActivity(intent);

                    } else {
                        Toast.makeText(MainActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("LoginError", "Database error: " + task.getException().getMessage());
                Toast.makeText(MainActivity.this, "Database error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
