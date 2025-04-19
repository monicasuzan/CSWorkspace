package com.example.myadmin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Buttons launching respective activities.
        findViewById(R.id.viewUsersButton).setOnClickListener(v ->
                startActivity(new Intent(this, ViewUsersActivity.class)));

        findViewById(R.id.deleteUserButton).setOnClickListener(v ->
                startActivity(new Intent(this, DeleteUserActivity.class)));

        findViewById(R.id.passwordResetButton).setOnClickListener(v ->
                startActivity(new Intent(this, ChangePasswordActivity.class)));

        Button uploadExcelButton = findViewById(R.id.uploadExcelButton);
        uploadExcelButton.setOnClickListener(v ->
                startActivity(new Intent(this, UploadActivity.class)));
    }
}
