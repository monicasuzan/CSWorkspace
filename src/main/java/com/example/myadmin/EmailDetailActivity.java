package com.example.myadmin;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class EmailDetailActivity extends AppCompatActivity {

    private TextView senderTextView, recipientTextView, subjectTextView, messageTextView, attachmentNameTextView, timestampTextView;
    private Button downloadButton;
    private Email email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_detail);

        senderTextView = findViewById(R.id.senderText);
        recipientTextView = findViewById(R.id.recipientText);
        subjectTextView = findViewById(R.id.subjectText);
        messageTextView = findViewById(R.id.messageText);
        attachmentNameTextView = findViewById(R.id.attachmentText);
        timestampTextView = findViewById(R.id.timestampText);  // Add this to your layout XML too
        downloadButton = findViewById(R.id.downloadButton);

        email = (Email) getIntent().getSerializableExtra("email");

        if (email != null) {
            senderTextView.setText("From: " + email.getSender());
            recipientTextView.setText("To: " + email.getRecipient());
            subjectTextView.setText("Subject: " + email.getSubject());
            messageTextView.setText(email.getMessage());

            // Format and show timestamp
            String formattedTime = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new Date(email.getTimestamp()));
            timestampTextView.setText("Sent: " + formattedTime);

            Map<String, String> attachment = email.getAttachment();
            if (attachment != null && attachment.containsKey("fileName") && attachment.containsKey("fileData")) {
                String fileName = attachment.get("fileName");
                attachmentNameTextView.setText("Attachment: " + fileName);
                downloadButton.setVisibility(View.VISIBLE);

                downloadButton.setOnClickListener(v -> downloadAttachment(fileName, attachment.get("fileData")));
            } else {
                attachmentNameTextView.setText("No Attachment");
                downloadButton.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(this, "Error loading email", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadAttachment(String fileName, String fileData) {
        try {
            if (fileData == null || fileData.isEmpty() || fileName == null || fileName.trim().isEmpty()) {
                Toast.makeText(this, "Invalid attachment data", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!fileName.contains(".")) {
                fileName += ".txt"; // Default extension if no extension is present
            }

            byte[] decodedBytes = Base64.decode(fileData, Base64.DEFAULT);

            // Save to getExternalFilesDir to match your provider_paths.xml
            File downloadDir = new File(getExternalFilesDir(null), "Download/MyAppDownloads");
            if (!downloadDir.exists()) {
                downloadDir.mkdirs();
            }

            File outputFile = new File(downloadDir, fileName);
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                fos.write(decodedBytes);
            }

            Toast.makeText(this, "Downloaded to internal storage: " + fileName, Toast.LENGTH_SHORT).show();
            openFile(outputFile);

        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Base64 decoding error!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(this, "Failed to save file", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void openFile(File file) {
        try {
            Uri fileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(fileUri, getMimeType(file.getName()));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No app found to open this file", Toast.LENGTH_SHORT).show();
        }
    }

    private String getMimeType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
            case "png": return "image/*";
            case "pdf": return "application/pdf";
            case "doc":
            case "docx": return "application/msword";
            case "xls":
            case "xlsx": return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "txt": return "text/plain";
            default: return "*/*";
        }
    }
}
