package com.example.iprepare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;

public class EmergencyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        // 🧭 Back button setup
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // ☎️ Hotline buttons
        findViewById(R.id.hotline911).setOnClickListener(v -> dial("911"));
        findViewById(R.id.hotlineBFP).setOnClickListener(v -> dial("0284260219"));
        findViewById(R.id.hotlineDOH).setOnClickListener(v -> dial("0289426843"));
        findViewById(R.id.hotlinePNP).setOnClickListener(v -> dial("117"));
        findViewById(R.id.hotlineNDRRMC).setOnClickListener(v -> dial("0289115061"));
    }

    private void dial(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        startActivity(intent);
    }
}
