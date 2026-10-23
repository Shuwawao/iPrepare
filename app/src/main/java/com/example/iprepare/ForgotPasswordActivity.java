package com.example.iprepare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText emailInput;
    private Button resetButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Link UI components
        emailInput = findViewById(R.id.emailInput);
        resetButton = findViewById(R.id.resetButton);
        backButton = findViewById(R.id.backButton);

        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.forgotToolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> {
                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                finish();
            });
        }

        // 🔹 Reset password button logic
        resetButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(ForgotPasswordActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.sendPasswordResetEmail(email)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(ForgotPasswordActivity.this, "Password reset link sent to your email.", Toast.LENGTH_LONG).show();
                        // Optionally return to login after sending
                        startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ForgotPasswordActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });

        // 🔹 Back button
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
            finish();
        });
    }
}
