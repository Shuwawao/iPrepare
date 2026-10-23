package com.example.iprepare;

import android.view.View;

import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ProfileActivity extends AppCompatActivity {

    private LinearLayout quizResultsContainer;
    private TextView tvUserName, tvUserEmail;
    private ProgressBar progressEarthquake, progressFlood, progressTyphoon, progressFire;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 🧭 Top App Bar
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }

        // UI elements
        quizResultsContainer = findViewById(R.id.quizResultsContainer);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        progressEarthquake = findViewById(R.id.progressEarthquake);
        progressFlood = findViewById(R.id.progressFlood);
        progressTyphoon = findViewById(R.id.progressTyphoon);
        progressFire = findViewById(R.id.progressFire);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Load user info + preparedness progress + quiz results
        loadUserInfo();
        loadPreparednessProgress();
        loadQuizResults();
    }

    /**
     * 👤 Load user display name & email
     */
    private void loadUserInfo() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            tvUserName.setText("User");
            tvUserEmail.setText("No email available");
            return;
        }

        String name = user.getDisplayName();
        String email = user.getEmail();

        tvUserEmail.setText(email != null ? email : "No email available");

        // If Firebase Auth name exists, show it immediately
        if (name != null && !name.isEmpty()) {
            tvUserName.setText(name);
        } else {
            // Otherwise fetch from Firestore
            db.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            String firestoreName = document.getString("name");
                            if (firestoreName != null && !firestoreName.isEmpty()) {
                                tvUserName.setText(firestoreName);
                            } else {
                                tvUserName.setText("User");
                            }
                        } else {
                            tvUserName.setText("User");
                        }
                    })
                    .addOnFailureListener(e -> tvUserName.setText("User"));
        }
    }

    /**
     * 🔄 Load preparedness progress (from SharedPreferences)
     */
    private void loadPreparednessProgress() {
        SharedPreferences prefs = getSharedPreferences("PreparednessPrefs", MODE_PRIVATE);

        int earthquake = getProgressFromPrefs(prefs, "earthquake");
        int flood = getProgressFromPrefs(prefs, "flood");
        int typhoon = getProgressFromPrefs(prefs, "typhoon");
        int fire = getProgressFromPrefs(prefs, "fire");

        updateProgressBars(earthquake, flood, typhoon, fire);
    }

    private int getProgressFromPrefs(SharedPreferences prefs, String disasterType) {
        int total = 0;
        int checked = 0;

        for (int i = 0; i < 50; i++) {
            String key = disasterType.toLowerCase() + "_item_" + i;
            if (prefs.contains(key)) {
                total++;
                if (prefs.getBoolean(key, false)) checked++;
            }
        }

        return total > 0 ? (checked * 100 / total) : 0;
    }

    private void updateProgressBars(int earthquake, int flood, int typhoon, int fire) {
        animateProgress(progressEarthquake, earthquake);
        animateProgress(progressFlood, flood);
        animateProgress(progressTyphoon, typhoon);
        animateProgress(progressFire, fire);
    }

    private void animateProgress(ProgressBar bar, int target) {
        ObjectAnimator animation = ObjectAnimator.ofInt(bar, "progress", 0, target);
        animation.setDuration(800);
        animation.start();
    }

    /**
     * 🧠 Load quiz results from Firestore
     */
    private void loadQuizResults() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        db.collection("users").document(user.getUid())
                .collection("quiz_results")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        TextView noResults = new TextView(this);
                        noResults.setText("No quiz results yet.");
                        noResults.setTextSize(16);
                        noResults.setTextColor(Color.parseColor("#666666"));
                        noResults.setPadding(8, 8, 8, 8);
                        quizResultsContainer.addView(noResults);
                        return;
                    }

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String topic = doc.getString("topic");
                        Long score = doc.getLong("score");
                        Long total = doc.getLong("totalQuestions");

                        if (topic == null || score == null || total == null) continue;

                        double percent = (double) score / total * 100;

                        GradientDrawable cardBg = new GradientDrawable();
                        cardBg.setCornerRadius(24);
                        cardBg.setColor(getColorByScore(percent));

                        LinearLayout card = new LinearLayout(this);
                        card.setOrientation(LinearLayout.VERTICAL);
                        card.setPadding(40, 30, 40, 30);
                        card.setBackground(cardBg);
                        card.setElevation(8f);
                        card.setGravity(Gravity.START);

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        params.setMargins(0, 0, 0, 32);
                        card.setLayoutParams(params);

                        TextView tvTopic = new TextView(this);
                        tvTopic.setText(topic + " Quiz");
                        tvTopic.setTextSize(20);
                        tvTopic.setTextColor(Color.parseColor("#212121"));
                        tvTopic.setTypeface(null, Typeface.BOLD);

                        TextView tvScore = new TextView(this);
                        tvScore.setText("Score: " + score + " / " + total + "  (" + (int) percent + "%)");
                        tvScore.setTextSize(16);
                        tvScore.setTextColor(Color.parseColor("#424242"));

                        TextView tvFeedback = new TextView(this);
                        tvFeedback.setText(getFeedbackMessage(percent));
                        tvFeedback.setTextSize(14);
                        tvFeedback.setTextColor(Color.parseColor("#555555"));

                        card.addView(tvTopic);
                        card.addView(tvScore);
                        card.addView(tvFeedback);

                        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
                        fadeIn.setDuration(400);
                        card.startAnimation(fadeIn);

                        quizResultsContainer.addView(card);
                    }
                })
                .addOnFailureListener(e -> {
                    TextView error = new TextView(this);
                    error.setText("Failed to load quiz results: " + e.getMessage());
                    quizResultsContainer.addView(error);
                });
    }

    private int getColorByScore(double percent) {
        if (percent >= 80) return Color.parseColor("#C8E6C9"); // green
        if (percent >= 50) return Color.parseColor("#FFF9C4"); // yellow
        return Color.parseColor("#FFCDD2"); // red
    }

    private String getFeedbackMessage(double percent) {
        if (percent >= 90) return "🌟 Excellent work!";
        if (percent >= 75) return "👏 Good job, keep it up!";
        if (percent >= 50) return "💪 Not bad, review a bit more.";
        return "📘 Keep practicing! You’ll improve.";
    }
}
