package com.example.iprepare;

import android.view.View;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION = 2500L; // 2.5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) throws Resources.NotFoundException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView splashLogo = findViewById(R.id.splashLogo);

        // 🔹 Load and start the fade-in + zoom animation
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in_zoom);
        if (animation != null && splashLogo != null) {
            splashLogo.startAnimation(animation);
        }

        // 🔹 Use main thread Looper to avoid deprecation warnings
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, GetStartedActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }
}
