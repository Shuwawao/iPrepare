package com.example.iprepare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class PrepareFragment extends Fragment {

    private CardView cardEarthquake, cardFire, cardFlood, cardTyphoon;
    private ProgressBar overallProgressBar;
    private TextView overallProgressPercent, overallMessage;
    private SharedPreferences prefs;

    // 🔹 All disaster types to track
    private final String[] disasters = {"earthquake", "fire", "flood", "typhoon"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prepare, container, false);

        // 🔹 Initialize UI elements
        cardEarthquake = view.findViewById(R.id.cardEarthquake);
        cardFire = view.findViewById(R.id.cardFire);
        cardFlood = view.findViewById(R.id.cardFlood);
        cardTyphoon = view.findViewById(R.id.cardTyphoon);
        overallProgressBar = view.findViewById(R.id.overallProgressBar);
        overallProgressPercent = view.findViewById(R.id.overallProgressPercent);
        overallMessage = view.findViewById(R.id.overallMessage);

        prefs = requireContext().getSharedPreferences("PreparednessPrefs", requireContext().MODE_PRIVATE);

        // 🔹 Set up card click listeners
        cardEarthquake.setOnClickListener(v -> openChecklist("Earthquake"));
        cardFire.setOnClickListener(v -> openChecklist("Fire"));
        cardFlood.setOnClickListener(v -> openChecklist("Flood"));
        cardTyphoon.setOnClickListener(v -> openChecklist("Typhoon"));

        // 🔹 Show initial overall progress
        updateOverallProgress();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh when returning to this fragment
        updateOverallProgress();
    }

    private void openChecklist(String disasterType) {
        Intent intent = new Intent(getActivity(), PrepareDetailActivity.class);
        intent.putExtra("disasterType", disasterType);
        startActivity(intent);
    }

    private void updateOverallProgress() {
        int total = 0;
        int count = 0;

        for (String disaster : disasters) {
            int progress = calculateProgressFor(disaster);
            total += progress;
            count++;
        }

        int overallPercent = (count > 0) ? total / count : 0;

        overallProgressBar.setProgress(overallPercent);
        overallProgressPercent.setText(overallPercent + "% Ready");

        // 🔹 Dynamic motivational message
        if (overallPercent < 30) {
            overallMessage.setText("You're just getting started — take small steps toward preparedness!");
        } else if (overallPercent < 70) {
            overallMessage.setText("Good progress! Keep improving your readiness.");
        } else if (overallPercent < 100) {
            overallMessage.setText("Almost there! A few more steps to complete preparedness.");
        } else {
            overallMessage.setText("Excellent! You are fully prepared for any situation.");
        }
    }

    private int calculateProgressFor(String disasterKey) {
        int totalItems = 0;
        int checkedItems = 0;

        for (String key : prefs.getAll().keySet()) {
            if (key.startsWith(disasterKey + "_item_")) {
                totalItems++;
                if (prefs.getBoolean(key, false)) {
                    checkedItems++;
                }
            }
        }

        if (totalItems == 0) return 0;
        return (int) ((checkedItems / (float) totalItems) * 100);
    }
}
