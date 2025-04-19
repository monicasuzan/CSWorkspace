package com.example.myadmin;

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

public class ViewEmailActivity extends AppCompatActivity {

//    private TextView textViewSubject, textViewMessage, textViewFileName;
//    private Button buttonViewAttachment;
//
//    private String fileName, fileData;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_view_email);
//
//        textViewSubject = findViewById(R.id.textViewSubject);
//        textViewMessage = findViewById(R.id.textViewMessage);
//        textViewFileName = findViewById(R.id.textViewFileName);
//        buttonViewAttachment = findViewById(R.id.buttonViewAttachment);
//
//        Intent intent = getIntent();
//        String subject = intent.getStringExtra("subject");
//        String message = intent.getStringExtra("message");
//        fileName = intent.getStringExtra("fileName");
//        fileData = intent.getStringExtra("fileData");
//
//        textViewSubject.setText("Subject: " + subject);
//        textViewMessage.setText("Message: " + message);
//
//        if (fileName != null && !fileName.isEmpty()) {
//            textViewFileName.setText("Attachment: " + fileName);
//            buttonViewAttachment.setVisibility(View.VISIBLE);
//        } else {
//            textViewFileName.setText("No Attachment");
//            buttonViewAttachment.setVisibility(View.GONE);
//        }
//
//        buttonViewAttachment.setOnClickListener(v -> openFile());
//    }
//
//    private void openFile() {
//        try {
//            byte[] decodedBytes = Base64.decode(fileData, Base64.DEFAULT);
//            File file = new File(getExternalFilesDir(null), fileName);
//            FileOutputStream fos = new FileOutputStream(file);
//            fos.write(decodedBytes);
//            fos.close();
//
//            // Get content URI using FileProvider
//            Uri fileUri = FileProvider.getUriForFile(this, "com.example.myadmin.fileprovider", file);
//
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setDataAndType(fileUri, "/");
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            startActivity(intent);
//
//        } catch (IOException e) {
//            Toast.makeText(this, "Failed to open file", Toast.LENGTH_SHORT).show();
//        }
//    }
}