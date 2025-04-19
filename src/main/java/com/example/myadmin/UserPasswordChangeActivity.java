package com.example.myadmin;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserPasswordChangeActivity extends AppCompatActivity {

    private EditText emailEditText, newPasswordEditText;
    private Button updatePasswordButton;
    private DatabaseReference usersRef;
    private String userEmail; // Logged-in user's email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_password_change);

        emailEditText = findViewById(R.id.et_email);
        newPasswordEditText = findViewById(R.id.et_new_password);
        updatePasswordButton = findViewById(R.id.btn_update_password);

        // Get logged-in user's email from intent
        userEmail = getIntent().getStringExtra("USER_EMAIL");

        if (userEmail != null) {
            emailEditText.setText(userEmail);
        } else {
            Toast.makeText(this, "Error: User email not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Firebase reference
        usersRef = FirebaseDatabase.getInstance().getReference("excelUsers");

        updatePasswordButton.setOnClickListener(v -> updatePassword());
    }

    private void updatePassword() {
        String newPassword = newPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(newPassword) || newPassword.length() < 6) {
            newPasswordEditText.setError("Password must be at least 6 characters");
            return;
        }

        // Update password in Firebase Database
        usersRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        userSnapshot.getRef().child("password").setValue(newPassword);
                    }
                    Toast.makeText(UserPasswordChangeActivity.this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(UserPasswordChangeActivity.this, "User not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserPasswordChangeActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
