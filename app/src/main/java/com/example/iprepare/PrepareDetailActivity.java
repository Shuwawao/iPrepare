package com.example.iprepare;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class PrepareDetailActivity extends AppCompatActivity {

    private LinearLayout checklistContainer, tipsContainer;
    private ProgressBar detailProgress;
    private TextView disasterTitle;
    private SharedPreferences prefs;
    private String disasterType;
    private final List<CheckBox> checkBoxes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_detail);

        // 🔹 Toolbar Setup
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }

        checklistContainer = findViewById(R.id.checklistContainer);
        tipsContainer = findViewById(R.id.tipsContainer);
        detailProgress = findViewById(R.id.detailProgress);
        disasterTitle = findViewById(R.id.disasterTitle);

        prefs = getSharedPreferences("PreparednessPrefs", MODE_PRIVATE);

        disasterType = getIntent().getStringExtra("disasterType");
        if (disasterType != null) {
            disasterTitle.setText(disasterType + " Preparedness");
            loadChecklist(disasterType);
            loadTips(disasterType);
        }
    }

    // 🔹 CHECKLIST LOADER
    private void loadChecklist(String disasterType) {
        String[] checklistItems;

        switch (disasterType.toLowerCase()) {
            case "earthquake":
                checklistItems = new String[]{
                        "Secure heavy furniture and appliances to walls.",
                        "Prepare a go-bag with food, water, and flashlight.",
                        "Keep a whistle and first aid kit accessible.",
                        "Know safe spots (under sturdy tables or interior walls).",
                        "Identify exits and evacuation areas.",
                        "Practice Drop, Cover, and Hold drills.",
                        "Store breakable items on lower shelves.",
                        "Have extra batteries and powerbanks ready."
                };
                break;

            case "fire":
                checklistItems = new String[]{
                        "Install smoke detectors and check monthly.",
                        "Keep a fire extinguisher on each floor.",
                        "Plan and practice your home fire escape route.",
                        "Ensure exits are not blocked.",
                        "Unplug unused appliances.",
                        "Store matches and lighters away from children.",
                        "Learn how to use a fire extinguisher properly.",
                        "Place emergency contact numbers near phones."
                };
                break;

            case "flood":
                checklistItems = new String[]{
                        "Keep important documents in waterproof containers.",
                        "Store drinking water in sealed bottles.",
                        "Know evacuation routes to higher ground.",
                        "Unplug electrical appliances before evacuating.",
                        "Prepare food that doesn’t spoil easily.",
                        "Have a flashlight and batteries ready.",
                        "Move valuables to upper floors if possible.",
                        "Avoid walking or driving through floodwaters."
                };
                break;

            case "typhoon":
                checklistItems = new String[]{
                        "Monitor weather updates from PAGASA or NDRRMC.",
                        "Stock enough canned food and clean water for 3 days.",
                        "Charge all devices and powerbanks.",
                        "Secure outdoor furniture and loose objects.",
                        "Prepare a first aid kit and flashlight.",
                        "Check your house for roof leaks or weak areas.",
                        "Have emergency numbers written down.",
                        "Prepare extra clothes and blankets in waterproof bags."
                };
                break;

            case "volcanic eruption":
                checklistItems = new String[]{
                        "Prepare goggles and N95 masks for ashfall.",
                        "Close doors, windows, and vents to prevent ash entry.",
                        "Stock food and clean water for several days.",
                        "Charge phones and radios for updates.",
                        "Identify evacuation centers in your area.",
                        "Avoid low-lying areas and river valleys.",
                        "Store important documents in sealed containers.",
                        "Keep pets indoors during ashfall."
                };
                break;

            default:
                checklistItems = new String[]{"Stay alert and prepared for any emergency."};
        }

        // Load saved states + add checkboxes dynamically
        for (int i = 0; i < checklistItems.length; i++) {
            String key = disasterType.toLowerCase() + "_item_" + i;
            boolean checked = prefs.getBoolean(key, false);

            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(checklistItems[i]);
            checkBox.setTextSize(15f);
            checkBox.setTypeface(Typeface.SANS_SERIF);
            checkBox.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            checkBox.setLineSpacing(4f, 1f);
            checkBox.setPadding(12, 12, 12, 12);
            checkBox.setButtonTintList(android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));
            checkBox.setChecked(checked);

            int finalI = i;
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                prefs.edit().putBoolean(key, isChecked).apply();
                updateProgress();
            });

            checklistContainer.addView(checkBox);
            checkBoxes.add(checkBox);

            // Add subtle divider between items except last
            if (i < checklistItems.length - 1) {
                View divider = new View(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1);
                lp.setMargins(0, 4, 0, 4);
                divider.setLayoutParams(lp);
                divider.setBackgroundColor(ContextCompat.getColor(this, R.color.dividerColor));
                checklistContainer.addView(divider);
            }
        }

        updateProgress();
    }

    // 🔹 TIPS LOADER
    private void loadTips(String disasterType) {
        String[] tips;

        switch (disasterType.toLowerCase()) {
            case "earthquake":
                tips = new String[]{
                        "Stay calm — Drop, Cover, and Hold until shaking stops.",
                        "Do not run outside during strong shaking.",
                        "Check for injuries and hazards after the quake."
                };
                break;
            case "fire":
                tips = new String[]{
                        "Get low and crawl under smoke when escaping.",
                        "If clothes catch fire: Stop, Drop, and Roll.",
                        "Never go back inside a burning building."
                };
                break;
            case "flood":
                tips = new String[]{
                        "Avoid floodwaters — even shallow water can sweep you away.",
                        "After floods, clean and disinfect everything that got wet.",
                        "Do not use electrical devices until safe to do so."
                };
                break;
            case "typhoon":
                tips = new String[]{
                        "Stay indoors until authorities say it’s safe.",
                        "Avoid using candles — use flashlights instead.",
                        "Stay away from windows during strong winds."
                };
                break;
            case "volcanic eruption":
                tips = new String[]{
                        "Wear long sleeves and masks when going outside.",
                        "Avoid driving in heavy ashfall — it can stall engines.",
                        "Clean roofs regularly to prevent ash buildup collapse."
                };
                break;
            default:
                tips = new String[]{"Always have an emergency plan and kit ready."};
        }

        for (int i = 0; i < tips.length; i++) {
            TextView tipView = new TextView(this);
            tipView.setText("•  " + tips[i]);
            tipView.setTextSize(14f);
            tipView.setTypeface(Typeface.SANS_SERIF);
            tipView.setPadding(8, 10, 8, 10);
            tipView.setLineSpacing(3f, 1f);
            tipView.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            tipsContainer.addView(tipView);

            if (i < tips.length - 1) {
                View divider = new View(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1);
                lp.setMargins(0, 2, 0, 2);
                divider.setLayoutParams(lp);
                divider.setBackgroundColor(ContextCompat.getColor(this, R.color.dividerColor));
                tipsContainer.addView(divider);
            }
        }
    }

    // 🔹 PROGRESS TRACKER
    private void updateProgress() {
        int total = checkBoxes.size();
        int checked = 0;
        for (CheckBox box : checkBoxes) {
            if (box.isChecked()) checked++;
        }

        int percent = (total > 0) ? (checked * 100 / total) : 0;
        detailProgress.setProgress(percent);
        TextView progressLabel = findViewById(R.id.progressLabel);
        if (progressLabel != null) {
            progressLabel.setText("Readiness Progress: " + percent + "% (" + checked + "/" + total + " completed)");
        }
    }
}
