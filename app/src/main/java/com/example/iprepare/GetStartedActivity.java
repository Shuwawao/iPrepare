package com.example.iprepare;

import android.view.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class GetStartedActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ Make full screen (optional aesthetic)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();

        // ✅ If already logged in, skip Get Started screen
        if (currentUser != null && currentUser.isEmailVerified()) {
            db.collection("users").document(currentUser.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String role = documentSnapshot.getString("role");
                        Intent intent;
                        if ("admin".equals(role)) {
                            intent = new Intent(this, AdminPanelActivity.class);
                        } else {
                            intent = new Intent(this, MainActivity.class);
                        }
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        // If role fetch fails, still go to main as fallback
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    });
            return;
        }

        // ✅ If not logged in, show Get Started layout
        setContentView(R.layout.activity_get_started);

        Button getStartedButton = findViewById(R.id.getStartedButton);
        getStartedButton.setOnClickListener(v -> {
            Intent intent = new Intent(GetStartedActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });
    }
}
