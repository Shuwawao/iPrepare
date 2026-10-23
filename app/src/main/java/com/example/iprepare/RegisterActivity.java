package com.example.iprepare;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.measurement.api.AppMeasurementSdk;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private EditText regName, regEmail, regPassword, regConfirmPassword;
    private ImageView toggleRegPasswordVisibility, toggleConfirmPasswordVisibility;
    private android.widget.ProgressBar regProgress;
    private Button registerNowButton;

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        regName = findViewById(R.id.regName);
        regEmail = findViewById(R.id.regEmail);
        regPassword = findViewById(R.id.regPassword);
        regConfirmPassword = findViewById(R.id.regConfirmPassword);
        toggleRegPasswordVisibility = findViewById(R.id.toggleRegPasswordVisibility);
        toggleConfirmPasswordVisibility = findViewById(R.id.toggleConfirmPasswordVisibility);
        regProgress = findViewById(R.id.regProgress);
        registerNowButton = findViewById(R.id.registerNowButton);
        Button backToLogin = findViewById(R.id.backToLogin);

        // 👁 Toggle main password
        toggleRegPasswordVisibility.setOnClickListener(v -> {
            if (isPasswordVisible) {
                regPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                toggleRegPasswordVisibility.setImageResource(R.drawable.ic_eye_closed);
            } else {
                regPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                toggleRegPasswordVisibility.setImageResource(R.drawable.ic_eye_open);
            }
            regPassword.setSelection(regPassword.getText().length());
            isPasswordVisible = !isPasswordVisible;
        });

        // 👁 Toggle confirm password
        toggleConfirmPasswordVisibility.setOnClickListener(v -> {
            if (isConfirmPasswordVisible) {
                regConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                toggleConfirmPasswordVisibility.setImageResource(R.drawable.ic_eye_closed);
            } else {
                regConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                toggleConfirmPasswordVisibility.setImageResource(R.drawable.ic_eye_open);
            }
            regConfirmPassword.setSelection(regConfirmPassword.getText().length());
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
        });

        // 📝 Register button
        registerNowButton.setOnClickListener(v -> registerUser());

        // 🔙 Back to Login
        backToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        final String name = regName.getText().toString().trim();
        final String email = regEmail.getText().toString().trim();
        String password = regPassword.getText().toString().trim();
        String confirmPassword = regConfirmPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (regProgress != null) regProgress.setVisibility(android.view.View.VISIBLE);
        if (registerNowButton != null) registerNowButton.setEnabled(false);

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    String userId = user.getUid();

                    Map<String, Object> userData = new HashMap<>();
                    userData.put(AppMeasurementSdk.ConditionalUserProperty.NAME, name);
                    userData.put("email", email);
                    userData.put("role", "user");

                    db.collection("users").document(userId).set(userData)
                        .addOnSuccessListener(unused -> sendVerificationEmail(user))
                        .addOnFailureListener(e -> {
                            if (regProgress != null) regProgress.setVisibility(android.view.View.GONE);
                            if (registerNowButton != null) registerNowButton.setEnabled(true);
                            Toast.makeText(this, "Failed to save user info: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
                }
            } else {
                if (regProgress != null) regProgress.setVisibility(android.view.View.GONE);
                if (registerNowButton != null) registerNowButton.setEnabled(true);
                String error = (task.getException() != null) ? task.getException().getMessage() : "Unknown error";
                Toast.makeText(this, "Registration failed: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Verification email sent! Please check your inbox.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_LONG).show();
            }

            auth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
