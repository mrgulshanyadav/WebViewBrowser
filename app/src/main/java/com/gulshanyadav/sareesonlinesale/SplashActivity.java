package com.gulshanyadav.sareesonlinesale;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import pl.droidsonroids.gif.GifImageView;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 2500;

    private ProgressBar progressBar;

    private int i = 0;
    private GifImageView gifImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();

        gifImageView = (GifImageView)findViewById(R.id.loader);
        progressBar = (ProgressBar)findViewById(R.id.splash_progress_bar);

        progressBar.setProgress(0);

        if(!isNetworkAvailable()){
//            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "No Internet Connection!", Snackbar.LENGTH_LONG);
//            snackbar.show();
            gifImageView.setImageResource(R.drawable.sareesimg);
        }else{
            Glide.with(this).asGif().load("http://9999071999.com/education/images/loop1.gif").into(gifImageView);
        }

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

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
