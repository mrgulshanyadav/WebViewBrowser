package com.gulshanyadav.sareesonlinesale;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import pl.droidsonroids.gif.GifImageView;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 2500;

    private ProgressBar progressBar;

    private int i = 0;
    private GifImageView gifImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();

        gifImageView = (GifImageView)findViewById(R.id.loader);
        progressBar = (ProgressBar)findViewById(R.id.splash_progress_bar);
        gifImageView.setImageResource(R.drawable.sareesimg);

        progressBar.setProgress(0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
            }

        },SPLASH_TIME_OUT);

        new CountDownTimer(SPLASH_TIME_OUT, 30) {
            @Override
            public void onTick(long millisUntilFinished) {
                i++;
                progressBar.setProgress(i);
            }
            @Override
            public void onFinish() {
                i++;
                progressBar.setProgress(100);
            }
        }.start();



    }

}
