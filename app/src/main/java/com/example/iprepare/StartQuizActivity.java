package com.example.iprepare;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StartQuizActivity extends AppCompatActivity {

    private TextView tvQuizTitle, tvQuestionText, tvQuestionNumber;
    private Button btnOptionA, btnOptionB, btnOptionC, btnOptionD, btnNext;
    private Toolbar quizToolbar;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private List<DocumentSnapshot> questionsList = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;
    private String selectedAnswer = "";
    private boolean answered = false;
    private String topicName = "Earthquake"; // default if not provided

    private final int BASE_COLOR = Color.parseColor("#009688"); // teal
    private final int CORRECT_COLOR = Color.parseColor("#4CAF50"); // green
    private final int WRONG_COLOR = Color.parseColor("#E53935"); // red

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_quiz);

        // ✅ Setup toolbar
        quizToolbar = findViewById(R.id.quizToolbar);
        setSupportActionBar(quizToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quiz");
        }
        quizToolbar.setNavigationOnClickListener(v -> onBackPressed());

        // ✅ Bind views
        tvQuizTitle = findViewById(R.id.tvQuizTitle);
        tvQuestionText = findViewById(R.id.tvQuestionText);
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        btnOptionA = findViewById(R.id.btnOptionA);
        btnOptionB = findViewById(R.id.btnOptionB);
        btnOptionC = findViewById(R.id.btnOptionC);
        btnOptionD = findViewById(R.id.btnOptionD);
        btnNext = findViewById(R.id.btnNext);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // ✅ Get topic name from intent
        if (getIntent().hasExtra("TOPIC_NAME")) {
            topicName = getIntent().getStringExtra("TOPIC_NAME");
        }

        // ✅ Firestore path: quizzes / {topicName lowercase} / questions
        CollectionReference questionsRef = db.collection("quizzes")
                .document(topicName.toLowerCase())
                .collection("questions");

        // ✅ Load questions
        questionsRef.orderBy("order", Query.Direction.ASCENDING).get()
                .addOnSuccessListener(query -> {
                    if (!query.isEmpty()) {
                        questionsList = query.getDocuments();
                        showQuestionWithAnimation();
                    } else {
                        Toast.makeText(this, "No questions found for " + topicName, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        // ✅ Option button listener
        View.OnClickListener optionClickListener = v -> {
            if (answered) return;
            selectedAnswer = v.getTag().toString();
            checkAnswer();
        };

        btnOptionA.setOnClickListener(optionClickListener);
        btnOptionB.setOnClickListener(optionClickListener);
        btnOptionC.setOnClickListener(optionClickListener);
        btnOptionD.setOnClickListener(optionClickListener);

        // ✅ Next button listener
        btnNext.setOnClickListener(v -> {
            if (!answered) {
                Toast.makeText(this, "Please select an answer first.", Toast.LENGTH_SHORT).show();
                return;
            }

            currentQuestionIndex++;
            if (currentQuestionIndex < questionsList.size()) {
                showQuestionWithAnimation();
            } else {
                finishQuiz();
            }
        });
    }

    /** 🎞️ Smooth fade-in animation when switching questions */
    private void showQuestionWithAnimation() {
        Animation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(400);

        showQuestion();
        tvQuestionText.startAnimation(fadeIn);
        tvQuestionNumber.startAnimation(fadeIn);
    }

    private void showQuestion() {
        answered = false;
        selectedAnswer = "";
        btnNext.setEnabled(false);
        resetButtonColors();

        DocumentSnapshot questionDoc = questionsList.get(currentQuestionIndex);
        Map<String, Object> data = questionDoc.getData();
        if (data == null) return;

        tvQuizTitle.setText(topicName + " Safety Quiz");
        tvQuestionText.setText((String) data.get("text"));
        tvQuestionNumber.setText("Question " + (currentQuestionIndex + 1) + " of " + questionsList.size());

        Map<String, String> choices = (Map<String, String>) data.get("choices");
        if (choices != null) {
            btnOptionA.setText("A. " + choices.get("A"));
            btnOptionB.setText("B. " + choices.get("B"));
            btnOptionC.setText("C. " + choices.get("C"));
            btnOptionD.setText("D. " + choices.get("D"));

            btnOptionA.setTag("A");
            btnOptionB.setTag("B");
            btnOptionC.setTag("C");
            btnOptionD.setTag("D");
        }

        btnNext.setText("Next");
    }

    /** ✅ Check and animate correct/wrong answer */
    private void checkAnswer() {
        answered = true;
        btnNext.setEnabled(true);

        DocumentSnapshot questionDoc = questionsList.get(currentQuestionIndex);
        String correctAnswer = questionDoc.getString("answer");

        if (correctAnswer == null) {
            Toast.makeText(this, "Missing answer in Firestore.", Toast.LENGTH_SHORT).show();
            return;
        }

        Button correctButton = getButtonByTag(correctAnswer);

        if (selectedAnswer.equalsIgnoreCase(correctAnswer)) {
            score += questionDoc.getLong("points") != null ? questionDoc.getLong("points").intValue() : 1;
            glowButton(getButtonByTag(selectedAnswer), CORRECT_COLOR);
            Toast.makeText(this, "✅ Correct!", Toast.LENGTH_SHORT).show();
        } else {
            glowButton(getButtonByTag(selectedAnswer), WRONG_COLOR);
            if (correctButton != null) glowButton(correctButton, CORRECT_COLOR);
            Toast.makeText(this, "❌ Wrong! Correct answer: " + correctAnswer, Toast.LENGTH_SHORT).show();
        }

        btnNext.setText(currentQuestionIndex == questionsList.size() - 1 ? "Finish" : "Next");
    }

    /** 🌟 Pulse color animation for feedback */
    private void glowButton(Button button, int glowColor) {
        ValueAnimator colorAnim = ValueAnimator.ofObject(new ArgbEvaluator(), BASE_COLOR, glowColor, BASE_COLOR);
        colorAnim.setDuration(800);
        colorAnim.addUpdateListener(anim -> {
            int color = (int) anim.getAnimatedValue();
            button.setBackgroundTintList(ColorStateList.valueOf(color));
        });
        colorAnim.start();
    }

    private Button getButtonByTag(String tag) {
        if (btnOptionA.getTag().toString().equalsIgnoreCase(tag)) return btnOptionA;
        if (btnOptionB.getTag().toString().equalsIgnoreCase(tag)) return btnOptionB;
        if (btnOptionC.getTag().toString().equalsIgnoreCase(tag)) return btnOptionC;
        if (btnOptionD.getTag().toString().equalsIgnoreCase(tag)) return btnOptionD;
        return null;
    }

    private void resetButtonColors() {
        btnOptionA.setBackgroundTintList(ColorStateList.valueOf(BASE_COLOR));
        btnOptionB.setBackgroundTintList(ColorStateList.valueOf(BASE_COLOR));
        btnOptionC.setBackgroundTintList(ColorStateList.valueOf(BASE_COLOR));
        btnOptionD.setBackgroundTintList(ColorStateList.valueOf(BASE_COLOR));
    }

    /** 🧮 Called when quiz finishes — saves result to Firestore */
    private void finishQuiz() {
        saveResultToFirestore();

        Intent intent = new Intent(StartQuizActivity.this, QuizResultActivity.class);
        intent.putExtra("TOPIC_NAME", topicName);
        intent.putExtra("SCORE", score);
        intent.putExtra("TOTAL_QUESTIONS", questionsList.size());
        startActivity(intent);
        finish();
    }

    /** 💾 Save quiz result to Firestore */
    private void saveResultToFirestore() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "⚠️ Login required to save progress.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("topic", topicName);
        resultData.put("score", score);
        resultData.put("totalQuestions", questionsList.size());
        resultData.put("timestamp", System.currentTimeMillis());

        db.collection("users")
                .document(userId)
                .collection("quiz_results")
                .document(topicName.toLowerCase())
                .set(resultData)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "✅ Result saved to profile!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "❌ Failed to save result: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
