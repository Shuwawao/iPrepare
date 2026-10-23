package com.example.iprepare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class QuizResultActivity extends AppCompatActivity {

    private TextView scoreText, feedbackText;
    private Button retryButton, backButton;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private String topicName;
    private int score;
    private int totalQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        scoreText = findViewById(R.id.scoreText);
        feedbackText = findViewById(R.id.feedbackText);
        retryButton = findViewById(R.id.retryButton);
        backButton = findViewById(R.id.backButton);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // ✅ Get data from StartQuizActivity
        Intent intent = getIntent();
        topicName = intent.getStringExtra("TOPIC_NAME");
        score = intent.getIntExtra("SCORE", 0);
        totalQuestions = intent.getIntExtra("TOTAL_QUESTIONS", 0);

        // 🧮 Display score
        scoreText.setText("Score: " + score + " / " + totalQuestions);

        // 💬 Feedback message
        if (score == totalQuestions) {
            feedbackText.setText("Perfect! Great job!");
        } else if (score >= totalQuestions * 0.7) {
            feedbackText.setText("Good work! You did well.");
        } else {
            feedbackText.setText("Keep practicing. You’ll get better!");
        }

        // 💾 Save result to Firestore
        saveResultToFirestore();

        // 🔁 Retry quiz
        retryButton.setOnClickListener(v -> {
            Intent retryIntent = new Intent(QuizResultActivity.this, StartQuizActivity.class);
            retryIntent.putExtra("TOPIC_NAME", topicName);
            startActivity(retryIntent);
            finish();
        });

        // ⬅️ Back to Quiz tab (inside MainActivity)
        backButton.setOnClickListener(v -> {
            Intent backIntent = new Intent(QuizResultActivity.this, MainActivity.class);
            backIntent.putExtra("openFragment", "quiz"); // tells MainActivity to open Quiz tab
            startActivity(backIntent);
            finish();
        });
    }

    private void saveResultToFirestore() {
        String userId = auth.getCurrentUser().getUid();

        Map<String, Object> resultData = new HashMap<>();
        resultData.put("topic", topicName);
        resultData.put("score", score);
        resultData.put("totalQuestions", totalQuestions);
        resultData.put("timestamp", System.currentTimeMillis());

        // Save to user's collection
        db.collection("users")
                .document(userId)
                .collection("quiz_results")
                .document(topicName.toLowerCase())
                .set(resultData)
                .addOnSuccessListener(aVoid ->
                        feedbackText.append("\n\n(Result saved to profile)")
                )
                .addOnFailureListener(e ->
                        feedbackText.append("\n\n(Failed to save result: " + e.getMessage() + ")")
                );
    }
}
