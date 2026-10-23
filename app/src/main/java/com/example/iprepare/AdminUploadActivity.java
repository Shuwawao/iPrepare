package com.example.iprepare;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

public class AdminUploadActivity extends AppCompatActivity {

    private EditText titleEditText, contentEditText, authorEditText, typeEditText;
    private Button uploadButton;
    private ProgressBar uploadProgress;
    private FirebaseFirestore db;
    private MaterialToolbar topAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_upload);

        // 🔗 Bind Views
        topAppBar = findViewById(R.id.topAppBar);
        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        authorEditText = findViewById(R.id.authorEditText);
        typeEditText = findViewById(R.id.typeEditText);
        uploadButton = findViewById(R.id.uploadButton);
        uploadProgress = findViewById(R.id.uploadProgress);

        // 🧭 Toolbar back button
        topAppBar.setNavigationOnClickListener(v -> onBackPressed());

        // ⚙️ Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // 📤 Upload button click
        uploadButton.setOnClickListener(view -> uploadAnnouncement());
    }

    private void uploadAnnouncement() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();
        String author = authorEditText.getText().toString().trim();
        String type = typeEditText.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty() || author.isEmpty() || type.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadProgress.setVisibility(View.VISIBLE);
        uploadButton.setEnabled(false);

        Map<String, Object> announcement = new HashMap<>();
        announcement.put("title", title);
        announcement.put("content", content);
        announcement.put("author", author);
        announcement.put("type", type);
        announcement.put("datePosted", FieldValue.serverTimestamp());

        db.collection("announcements")
                .add(announcement)
                .addOnSuccessListener(documentReference -> {
                    uploadProgress.setVisibility(View.GONE);
                    uploadButton.setEnabled(true);
                    Toast.makeText(this, "Announcement uploaded successfully!", Toast.LENGTH_SHORT).show();
                    titleEditText.setText("");
                    contentEditText.setText("");
                    authorEditText.setText("");
                    typeEditText.setText("");
                })
                .addOnFailureListener(e -> {
                    uploadProgress.setVisibility(View.GONE);
                    uploadButton.setEnabled(true);
                    Toast.makeText(this, "Failed to upload: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
