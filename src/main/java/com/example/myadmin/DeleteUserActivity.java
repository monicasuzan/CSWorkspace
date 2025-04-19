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

public class DeleteUserActivity extends AppCompatActivity {

    private EditText emailEditText;
    private Button deleteButton;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);

        emailEditText = findViewById(R.id.etDeleteUserEmail);
        deleteButton = findViewById(R.id.btnDeleteUser);

        usersRef = FirebaseDatabase.getInstance().getReference("excelUsers");

        deleteButton.setOnClickListener(v -> deleteUser());
    }

    private void deleteUser() {
        String emailPrefix = emailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(emailPrefix)) {
            emailEditText.setError("Please enter an email prefix");
            return;
        }

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean found = false;
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String email = userSnapshot.child("email").getValue(String.class);
                    if (email != null && email.startsWith(emailPrefix)) {
                        userSnapshot.getRef().removeValue();
                        found = true;
                    }
                }
                if (found) {
                    Toast.makeText(DeleteUserActivity.this, "Users deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DeleteUserActivity.this, "No users found with the given prefix", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DeleteUserActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}