package com.example.myadmin;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class TrashActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EmailAdapter emailAdapter;
    private List<Email> emailList;
    private DatabaseReference trashRef;
    private String loggedInUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        emailList = new ArrayList<>();
        loggedInUserEmail = getIntent().getStringExtra("USER_EMAIL");

        fetchTrashEmails();
    }

    private void fetchTrashEmails() {
        trashRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(loggedInUserEmail.replace(".", ","))
                .child("trash");

        trashRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                emailList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Email email = data.getValue(Email.class);
                    if (email != null && data.getKey() != null) {
                        email.setEmailId(data.getKey());
                        emailList.add(email);
                    }
                }
                emailAdapter = new EmailAdapter(TrashActivity.this, emailList, loggedInUserEmail, email -> deletePermanently(email));
                recyclerView.setAdapter(emailAdapter);
                emailAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TrashActivity.this, "Failed to load trash emails", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deletePermanently(Email email) {
        trashRef.child(email.getEmailId()).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                emailList.remove(email);
                emailAdapter.notifyDataSetChanged();
                Toast.makeText(TrashActivity.this, "Email deleted permanently", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(TrashActivity.this, "Failed to delete email", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
