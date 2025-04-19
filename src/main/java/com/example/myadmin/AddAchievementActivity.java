package com.example.myadmin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class AddAchievementActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imagePreview;
    private EditText etName, etDetails;
    private Button btnSelectImage, btnSubmit;
    private DatabaseReference databaseReference;
    private String encodedImage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_achievement);

        imagePreview = findViewById(R.id.imagePreview);
        etName = findViewById(R.id.etName);
        etDetails = findViewById(R.id.etDetails);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSubmit = findViewById(R.id.btnSubmit);

        databaseReference = FirebaseDatabase.getInstance().getReference("achievers");

        // Select Image
        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // Submit Data to Firebase
        btnSubmit.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String details = etDetails.getText().toString().trim();

            if (name.isEmpty() || details.isEmpty() || encodedImage.isEmpty()) {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
                return;
            }

            String id = databaseReference.push().getKey();
            long timestamp = System.currentTimeMillis(); // Capture timestamp

            Achievement achievement = new Achievement(id, name, details, encodedImage, timestamp);
            databaseReference.child(id).setValue(achievement)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Achievement added successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedBitmap = BitmapFactory.decodeStream(imageStream);
                imagePreview.setImageBitmap(selectedBitmap);

                // Convert Image to Base64
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] byteArray = baos.toByteArray();
                encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
