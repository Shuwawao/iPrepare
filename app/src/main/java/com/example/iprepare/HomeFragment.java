package com.example.iprepare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeFragment extends Fragment {

    private TextView tvWelcome;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvWelcome = view.findViewById(R.id.tvWelcome);

        // Get current Firebase user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Try to get the display name first
            String userName = user.getDisplayName();

            // If the display name is missing, use email before '@'
            if (userName == null || userName.isEmpty()) {
                String email = user.getEmail();
                if (email != null && email.contains("@")) {
                    userName = email.substring(0, email.indexOf('@'));
                } else {
                    userName = "User";
                }
            }

            tvWelcome.setText("Welcome, " + userName + "!");
        } else {
            tvWelcome.setText("Welcome to iPrepare!");
        }

        // 🔗 Enable card taps to navigate directly to topics
        View cardEarthquake = view.findViewById(R.id.homeCardEarthquake);
        View cardFlood = view.findViewById(R.id.homeCardFlood);
        View cardFire = view.findViewById(R.id.homeCardFire);
        View cardTyphoon = view.findViewById(R.id.homeCardTyphoon);

        if (cardEarthquake != null) {
            cardEarthquake.setOnClickListener(v -> openTopic("Earthquake"));
        }
        if (cardFlood != null) {
            cardFlood.setOnClickListener(v -> openTopic("Flood"));
        }
        if (cardFire != null) {
            cardFire.setOnClickListener(v -> openTopic("Fire"));
        }
        if (cardTyphoon != null) {
            cardTyphoon.setOnClickListener(v -> openTopic("Typhoon"));
        }

        return view;
    }

    private void openTopic(String topicName) {
        android.content.Intent intent = new android.content.Intent(requireContext(), TopicsActivity.class);
        intent.putExtra("TOPIC_NAME", topicName);
        startActivity(intent);
    }
}
