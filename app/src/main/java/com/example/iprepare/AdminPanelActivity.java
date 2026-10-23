package com.example.iprepare;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;

public class AdminPanelActivity extends AppCompatActivity {

    private Button uploadAnnouncementButton, viewUpdatesBtn, logoutButton;
    private ImageView adminLogo;
    private TextView welcomeText;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        // 🔗 Bind views
        adminLogo = findViewById(R.id.adminLogo);
        welcomeText = findViewById(R.id.welcomeText);
        uploadAnnouncementButton = findViewById(R.id.uploadAnnouncementButton);
        viewUpdatesBtn = findViewById(R.id.viewUpdatesBtn);
        logoutButton = findViewById(R.id.logoutButton);

        auth = FirebaseAuth.getInstance();

        // Upload announcements
        uploadAnnouncementButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminPanelActivity.this, AdminUploadActivity.class);
            startActivity(intent);
        });

        // View updates
        viewUpdatesBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AdminPanelActivity.this, UpdatesActivity.class);
            intent.putExtra("isAdmin", true);
            startActivity(intent);
        });

        // 🚪 Logout
        logoutButton.setOnClickListener(v -> {
            auth.signOut(); // 👈 This logs out from Firebase completely

            Intent intent = new Intent(AdminPanelActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
