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

public class InboxActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EmailAdapter emailAdapter;
    private List<Email> emailList;
    private DatabaseReference databaseRef;
    private String loggedInUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        emailList = new ArrayList<>();
        loggedInUserEmail = getIntent().getStringExtra("USER_EMAIL");

        databaseRef = FirebaseDatabase.getInstance().getReference("emails");

        fetchEmails();
    }

    private void fetchEmails() {
        databaseRef.orderByChild("recipient").equalTo(loggedInUserEmail)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        emailList.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Email email = data.getValue(Email.class);
                            if (email != null && data.getKey() != null) {
                                email.setEmailId(data.getKey());

                                // skip emails that were deleted by the logged-in user
                                if (data.child("deletedFor").hasChild(loggedInUserEmail.replace(".", ","))) {
                                    continue;
                                }

                                emailList.add(email);
                            }
                        }
                        emailAdapter = new EmailAdapter(InboxActivity.this, emailList, loggedInUserEmail, email -> moveToTrash(email));
                        recyclerView.setAdapter(emailAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(InboxActivity.this, "Failed to load emails", Toast.LENGTH_SHORT).show();
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
                //marks the email as deleted for this user
                databaseRef.child(email.getEmailId()).child("deletedFor").child(loggedInUserEmail.replace(".", ",")).setValue(true)
                        .addOnCompleteListener(deleteTask -> {
                            if (deleteTask.isSuccessful()) {
                                //removes the email from the list and updates the UI
                                emailList.remove(email);
                                emailAdapter.notifyDataSetChanged();
                                Toast.makeText(InboxActivity.this, "Email moved to trash", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
