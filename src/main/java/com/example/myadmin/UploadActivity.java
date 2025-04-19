package com.example.myadmin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.InputStream;

public class UploadActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;
    private Uri fileUri;
    private TextView txtFileName;
    private DatabaseReference excelUsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        Button btnChooseFile = findViewById(R.id.btnChooseFile);
        Button btnUpload = findViewById(R.id.btnUpload);
        txtFileName = findViewById(R.id.txtFileName);

        excelUsersRef = FirebaseDatabase.getInstance().getReference("excelUsers");

        btnChooseFile.setOnClickListener(v -> openFileChooser());
        btnUpload.setOnClickListener(v -> uploadFile());
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();
            txtFileName.setText("Selected: " + fileUri.getLastPathSegment());
        }
    }

    private void uploadFile() {
        if (fileUri == null) {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            return;
        }

        try (InputStream inputStream = getContentResolver().openInputStream(fileUri);
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            // Iterate over rows; assuming first row is header.
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                String name = row.getCell(0).getStringCellValue().trim();
                String email = row.getCell(1).getStringCellValue().trim();
                String role = row.getCell(2).getStringCellValue().trim();
                String password = row.getCell(3).getStringCellValue().trim();

                String sanitizedEmail = email.replace(".", ",");
                // Using the User model for upload.
                excelUsersRef.child(sanitizedEmail).setValue(new User(name, email, role, password));
            }
            Toast.makeText(this, "File uploaded to Firebase", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error reading file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
