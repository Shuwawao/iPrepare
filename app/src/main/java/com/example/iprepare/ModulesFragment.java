package com.example.iprepare;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class ModulesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_modules, container, false);

        // 🔘 Find disaster cards instead of buttons
        CardView cardEarthquake = view.findViewById(R.id.cardEarthquake);
        CardView cardFlood = view.findViewById(R.id.cardFlood);
        CardView cardFire = view.findViewById(R.id.cardFire);
        CardView cardTyphoon = view.findViewById(R.id.cardTyphoon);

        // 🔗 Set onClick listeners to open TopicsActivity
        cardEarthquake.setOnClickListener(v -> openModule("Earthquake"));
        cardFlood.setOnClickListener(v -> openModule("Flood"));
        cardFire.setOnClickListener(v -> openModule("Fire"));
        cardTyphoon.setOnClickListener(v -> openModule("Typhoon"));

        return view;
    }

    private void openModule(String topic) {
        Intent intent = new Intent(requireContext(), TopicsActivity.class);
        intent.putExtra("TOPIC_NAME", topic);
        startActivity(intent);
    }
}
