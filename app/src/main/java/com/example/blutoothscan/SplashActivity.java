package com.example.blutoothscan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            // Start MainActivity
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            // Finish the splash activity
            finish();
        }, 2000);
    }

}
