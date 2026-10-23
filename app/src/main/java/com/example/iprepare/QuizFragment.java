package com.example.iprepare;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

public class QuizFragment extends Fragment {

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        // 🟢 Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // 🟢 Reference CardViews instead of Buttons
        CardView earthquakeQuizButton = view.findViewById(R.id.earthquakeQuizButton);
        CardView floodQuizButton = view.findViewById(R.id.floodQuizButton);
        CardView fireQuizButton = view.findViewById(R.id.fireQuizButton);
        CardView typhoonQuizButton = view.findViewById(R.id.typhoonQuizButton);

        // 🟢 Set click listeners for each quiz
        earthquakeQuizButton.setOnClickListener(v -> openQuiz("Earthquake"));
        floodQuizButton.setOnClickListener(v -> openQuiz("Flood"));
        fireQuizButton.setOnClickListener(v -> openQuiz("Fire"));
        typhoonQuizButton.setOnClickListener(v -> openQuiz("Typhoon"));

        return view;
    }

    // 🧠 Opens quiz if data exists in Firestore
    private void openQuiz(String topicName) {
        String topicId = topicName.toLowerCase();

        db.collection("quizzes")
                .document(topicId)
                .collection("questions")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(requireContext(),
                                "No questions found for " + topicName,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(requireContext(), StartQuizActivity.class);
                        intent.putExtra("TOPIC_NAME", topicName);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(),
                            "Failed to load " + topicName + " quiz: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}
