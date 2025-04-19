package com.example.myadmin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    private String currentUserEmail;
    private String currentUserName;
    private LinearLayout achieversContainer;
    private DatabaseReference databaseReference;
    private Button btnReadMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        // Retrieve email from Intent
        currentUserEmail = getIntent().getStringExtra("USER_EMAIL");

        // Reference to Firebase users to get the name
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("excelUsers")
                .child(currentUserEmail.replace(".", ","))
                .child("name");

        // Set default welcome message
        TextView welcomeText = findViewById(R.id.welcome_text);
        welcomeText.setText("Welcome, User");

        // Fetch user name from Firebase
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentUserName = snapshot.getValue(String.class);
                    welcomeText.setText("Welcome, " + currentUserName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Initialize buttons
        Button composeButton = findViewById(R.id.btn_compose);
        Button inboxButton = findViewById(R.id.btn_inbox);
        Button sentButton = findViewById(R.id.btn_sent);
        Button trashButton = findViewById(R.id.btn_trash);
        Button changePasswordButton = findViewById(R.id.btn_change_password);
        btnReadMore = findViewById(R.id.btn_read_more);

        // Initialize achievers container
        achieversContainer = findViewById(R.id.achieversContainer);
        databaseReference = FirebaseDatabase.getInstance().getReference("achievers");

        // Fetch only the first 5 achievers for horizontal preview
        databaseReference.limitToLast(5).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                achieversContainer.removeAllViews(); // Clear existing views

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Achievement achievement = dataSnapshot.getValue(Achievement.class);
                    if (achievement != null) {
                        addAchieverCard(achievement);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Read More button to view all achievers
        btnReadMore.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ViewAchieversActivity.class));
        });

        // Email Navigation Buttons
        composeButton.setOnClickListener(v -> openActivity(ComposeEmailActivity.class));
        inboxButton.setOnClickListener(v -> openActivity(InboxActivity.class));
        sentButton.setOnClickListener(v -> openActivity(SentEmailActivity.class));
        trashButton.setOnClickListener(v -> openActivity(TrashActivity.class));
        changePasswordButton.setOnClickListener(v -> openActivity(UserPasswordChangeActivity.class));
    }

    // Function to dynamically create achiever cards
    private void addAchieverCard(Achievement achievement) {
        // CardView for each achiever
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(350, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(16, 8, 16, 8);
        cardView.setLayoutParams(cardParams);
        cardView.setRadius(16f);
        cardView.setCardElevation(6f);
        cardView.setBackgroundColor(Color.WHITE);

        // Inner LinearLayout
        LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);
        layout.setGravity(Gravity.CENTER);

        // ImageView for Achiever's Photo
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(250, 250);
        imageParams.gravity = Gravity.CENTER;
        imageView.setLayoutParams(imageParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // Decode Base64 Image
        String base64Image = achievement.getImageBase64();
        if (base64Image != null && !base64Image.isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                if (decodedBitmap != null) {
                    imageView.setImageBitmap(decodedBitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // TextView for Achiever's Name
        TextView nameText = new TextView(this);
        nameText.setText(achievement.getName());
        nameText.setTextSize(18);
        nameText.setTypeface(null, Typeface.BOLD);
        nameText.setTextColor(Color.BLACK);
        nameText.setGravity(Gravity.CENTER);
        nameText.setPadding(0, 10, 0, 5);

        // TextView for Achiever's Achievement
        TextView achievementText = new TextView(this);
        achievementText.setText(achievement.getDetails());
        achievementText.setTextSize(14);
        achievementText.setGravity(Gravity.CENTER);
        achievementText.setTextColor(Color.DKGRAY);

        // Add views to layout
        layout.addView(imageView);
        layout.addView(nameText);
        layout.addView(achievementText);

        // Add layout to CardView
        cardView.addView(layout);

        // Add CardView to horizontal scroll container
        achieversContainer.addView(cardView);
    }

    private void openActivity(Class<?> activityClass) {
        Intent intent = new Intent(HomeActivity.this, activityClass);
        intent.putExtra("USER_EMAIL", currentUserEmail);
        startActivity(intent);
    }
}