package com.example.donatehub;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;


public class splashscreen extends AppCompatActivity {

    private static final int SPLASH_TIMEOUT = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        ImageView logoImageView = findViewById(R.id.simplelogo);
        Animation slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        logoImageView.startAnimation(slideUpAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start your main activity or another activity after the splash screen
                Intent intent = new Intent(splashscreen.this, MainActivity.class);
                startActivity(intent);
                finish(); // Close the splash screen activity
            }
        }, SPLASH_TIMEOUT);
    }
}
