package com.example.rentalmobilmulia;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.facebook.shimmer.ShimmerFrameLayout;

public class SplashFragment extends AppCompatActivity {

    private static final int SPLASH_DURATION = 8000;
    private TextView typewriterText;
    private final String fullText = "MULIA RENTCAR";
    private int index = 0;
    private final Handler textHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_splash);

        ShimmerFrameLayout shimmer = findViewById(R.id.shimmer_layout);
        shimmer.startShimmer();

        typewriterText = findViewById(R.id.typewriter);
        startTypewriterAnimation();

        ImageView logo = findViewById(R.id.logo);
        Animation fadeBright = AnimationUtils.loadAnimation(this, R.anim.logo_fade_bright);
        logo.startAnimation(fadeBright);

// Tambah delay terakhir agar efek "menyala terang" lebih terasa
        new Handler().postDelayed(() -> {
            shimmer.stopShimmer();
            startActivity(new Intent(SplashFragment.this, Login.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, SPLASH_DURATION + 500); // Tambah 0.5 detik ekstra

    }

    private void startTypewriterAnimation() {
        index = 0;
        typewriterText.setText("");
        textHandler.postDelayed(typewriterRunnable, 150);
    }

    private final Runnable typewriterRunnable = new Runnable() {
        @Override
        public void run() {
            if (index < fullText.length()) {
                typewriterText.setText(fullText.substring(0, index + 1));
                index++;
                textHandler.postDelayed(this, 120);
            }
        }
    };
}