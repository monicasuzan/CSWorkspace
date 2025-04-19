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

public class SentEmailActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EmailAdapter emailAdapter;
    private List<Email> emailList;
    private DatabaseReference databaseRef;
    private String loggedInUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_email);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        emailList = new ArrayList<>();
        loggedInUserEmail = getIntent().getStringExtra("USER_EMAIL");

        databaseRef = FirebaseDatabase.getInstance().getReference("emails");
        fetchSentEmails();
    }

    private void fetchSentEmails() {
        databaseRef.orderByChild("sender").equalTo(loggedInUserEmail)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        emailList.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Email email = data.getValue(Email.class);
                            if (email != null) {
                                email.setEmailId(data.getKey());

                                // ✅ Skip emails that were deleted by the logged-in user
                                if (data.child("deletedFor").hasChild(loggedInUserEmail.replace(".", ","))) {
                                    continue;
                                }

                                emailList.add(email);
                            }
                        }
                        emailAdapter = new EmailAdapter(SentEmailActivity.this, emailList, loggedInUserEmail, email -> moveToTrash(email));
                        recyclerView.setAdapter(emailAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SentEmailActivity.this, "Failed to load emails", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void moveToTrash(Email email) {
        DatabaseReference trashRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(loggedInUserEmail.replace(".", ","))
                .child("trash")
                .child(email.getEmailId());

        trashRef.setValue(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                databaseRef.child(email.getEmailId()).child("deletedFor").child(loggedInUserEmail.replace(".", ",")).setValue(true)
                        .addOnCompleteListener(deleteTask -> {
                            if (deleteTask.isSuccessful()) {
                                emailList.remove(email);
                                emailAdapter.notifyDataSetChanged();
                                Toast.makeText(SentEmailActivity.this, "Email moved to trash", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
