package com.example.myadmin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewAchieversActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AchieverAdapter adapter;
    private List<Achievement> achieversList = new ArrayList<>();
    private DatabaseReference databaseReference;
    private Button btnAddAchievement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_achievers);

        recyclerView = findViewById(R.id.recyclerViewAchievements);
        btnAddAchievement = findViewById(R.id.btn_add_achievement);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference = FirebaseDatabase.getInstance().getReference("achievers");

        btnAddAchievement.setOnClickListener(v -> {
            startActivity(new Intent(ViewAchieversActivity.this, AddAchievementActivity.class));
        });

        // Fetch only the latest 5 achievers
        databaseReference.orderByChild("timestamp").limitToLast(5).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                achieversList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Achievement achievement = dataSnapshot.getValue(Achievement.class);
                    achieversList.add(achievement);
                }

                // Reverse list to show newest first
                Collections.reverse(achieversList);

                adapter = new AchieverAdapter(achieversList, ViewAchieversActivity.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
