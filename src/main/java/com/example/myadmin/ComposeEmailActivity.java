package com.example.myadmin;

import    android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ComposeEmailActivity extends AppCompatActivity {

    private EditText etRecipient, etSubject, etMessage;
    private TextView tvSender, tvAttachment;
    private Button btnAttach, btnSend;
    private String attachmentBase64 = "";
    private String attachmentName = "";
    private DatabaseReference databaseRef;
    private String currentUserEmail;
    private static final int PICK_FILE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_email);

        currentUserEmail = getIntent().getStringExtra("USER_EMAIL");
        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            Toast.makeText(this, "User email not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvSender = findViewById(R.id.tvSender);
        etRecipient = findViewById(R.id.etRecipient);
        etSubject = findViewById(R.id.etSubject);
        etMessage = findViewById(R.id.etMessage);
        tvAttachment = findViewById(R.id.tvFileStatus);
        btnAttach = findViewById(R.id.btnAttach);
        btnSend = findViewById(R.id.btnSend);

        tvSender.setText("From: " + currentUserEmail);
        databaseRef = FirebaseDatabase.getInstance().getReference("emails");

        btnAttach.setOnClickListener(v -> openFileChooser());
        btnSend.setOnClickListener(v -> sendEmails());
    }
    //Opens the file selection dialog
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    //checks if the file is selected properly
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri fileUri = data.getData();
            //Reads the file into a byte array.
            try {
                InputStream inputStream = getContentResolver().openInputStream(fileUri);
                byte[] bytes = new byte[inputStream.available()];
                inputStream.read(bytes);
                inputStream.close();
                //Converts the file into Base64 format (attachmentBase64)
                attachmentBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);
                attachmentName = getFileName(fileUri);
                //Updates the UI with the attachment name.
                tvAttachment.setText("Attached: " + attachmentName);
            } catch (IOException e) {
                Toast.makeText(this, "File attachment failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void sendEmails() {
        String recipientsInput = etRecipient.getText().toString().trim();
        String subject = etSubject.getText().toString().trim();
        String message = etMessage.getText().toString().trim();

        if (subject.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "Subject and Message cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] recipients = recipientsInput.split(",");
        Map<String, Object> emailBatch = new HashMap<>();

        for (String recipient : recipients) {
            recipient = recipient.trim();
            if (!recipient.endsWith("@stellamariscollege.edu.in")) {
                Toast.makeText(this, "Invalid recipient: " + recipient, Toast.LENGTH_SHORT).show();
                continue;
            }

            //Generates a unique email ID and stores the email details
            String emailId = databaseRef.push().getKey();
            Map<String, Object> emailData = new HashMap<>();
            emailData.put("emailId", emailId);
            emailData.put("sender", currentUserEmail);
            emailData.put("recipient", recipient);
            emailData.put("subject", subject);
            emailData.put("message", message);
            emailData.put("timestamp", ServerValue.TIMESTAMP);

            //If an attachment exists, stores file name and Base64 data
            if (!attachmentBase64.isEmpty()) {
                Map<String, String> attachment = new HashMap<>();
                attachment.put("fileName", attachmentName);
                attachment.put("fileData", attachmentBase64);
                emailData.put("attachment", attachment);
            }

            //stores emails in batch
            emailBatch.put(emailId, emailData);
        }

        //saves all the mails in the database and shows success or failure message
        if (!emailBatch.isEmpty()) {
            databaseRef.updateChildren(emailBatch)
                    .addOnSuccessListener(aVoid -> Toast.makeText(ComposeEmailActivity.this, "Emails Sent Successfully!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(ComposeEmailActivity.this, "Failed to send emails!", Toast.LENGTH_SHORT).show());
        }
    }
}