package com.example.iprepare;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private EditText emailInput, passwordInput;
    private ImageView togglePasswordVisibility;
    private android.widget.ProgressBar loginProgress;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 🔁 Check if user is already logged in
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            if (currentUser.isEmailVerified()) {
                // Fetch user role and redirect
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
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Error checking role: " + e.getMessage(), Toast.LENGTH_LONG).show()
                        );
            }
            return; // stop showing login screen if already logged in
        }

        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);
        loginProgress = findViewById(R.id.loginProgress);
        Button loginButton = findViewById(R.id.loginButton);
        Button registerButton = findViewById(R.id.registerButton);
        TextView forgotPasswordText = findViewById(R.id.forgotPasswordText);

        // 👁 Toggle password visibility
        togglePasswordVisibility.setOnClickListener(v -> {
            if (isPasswordVisible) {
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePasswordVisibility.setImageResource(R.drawable.ic_eye_closed);
                isPasswordVisible = false;
            } else {
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePasswordVisibility.setImageResource(R.drawable.ic_eye_open);
                isPasswordVisible = true;
            }
            passwordInput.setSelection(passwordInput.getText().length());
        });

        // 🔐 Login button
        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            loginProgress.setVisibility(android.view.View.VISIBLE);
            loginButton.setEnabled(false);

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        loginProgress.setVisibility(android.view.View.GONE);
                        loginButton.setEnabled(true);
                        handleLogin(task);
                    });
        });

        // 🧍 Register button
        registerButton.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );

        // ❓ Forgot password
        forgotPasswordText.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class))
        );
    }

    private void handleLogin(Task<?> task) {
        if (task.isSuccessful()) {
            FirebaseUser user = auth.getCurrentUser();
            if (user == null) return;

            if (!user.isEmailVerified()) {
                Toast.makeText(this, "Please verify your email before logging in.", Toast.LENGTH_LONG).show();
                user.sendEmailVerification()
                        .addOnCompleteListener(t -> {
                            if (t.isSuccessful()) {
                                Toast.makeText(this, "Verification email re-sent. Please check your inbox.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(this, "Failed to resend verification email.", Toast.LENGTH_LONG).show();
                            }
                        });
                auth.signOut();
                return;
            }

            // ✅ Fetch role from Firestore
            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> handleUserRole(documentSnapshot))
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error fetching role: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );

        } else {
            Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void handleUserRole(DocumentSnapshot documentSnapshot) {
        if (!documentSnapshot.exists()) {
            Toast.makeText(this, "No user data found.", Toast.LENGTH_SHORT).show();
            return;
        }

        String role = documentSnapshot.getString("role");
        Intent intent;
        if ("admin".equals(role)) {
            Toast.makeText(this, "Welcome, Admin!", Toast.LENGTH_SHORT).show();
            intent = new Intent(this, AdminPanelActivity.class);
        } else {
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            intent = new Intent(this, MainActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
