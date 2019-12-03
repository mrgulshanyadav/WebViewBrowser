package com.gulshanyadav.sareesonlinesale;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView superImageView;
    ProgressBar superProgressBar;
    DatabaseReference UsersRef;
    WebView superWebView;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        superImageView = findViewById(R.id.myImageView);
        superProgressBar = findViewById(R.id.myProgressBar);
        superWebView = findViewById(R.id.myWebView);

        loadingBar = new ProgressDialog(this);

        superProgressBar.setMax(100);

        superWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                loadingBar.setTitle("Loading");
                loadingBar.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loadingBar.dismiss();
            }
        });

        superWebView.getSettings().setSupportZoom(true);
        superWebView.getSettings().setBuiltInZoomControls(true);
        superWebView.getSettings().setDisplayZoomControls(true);
        superWebView.getSettings().setJavaScriptEnabled(true);
        superWebView.loadUrl("http://newexchangeoffer.com/search.php?query=sarees");
//        superWebView.loadUrl("https://google.com");

        superWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                superProgressBar.setProgress(newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                getSupportActionBar().setTitle(title);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
                superImageView.setImageBitmap(icon);
            }
        });

        superWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri myUri = Uri.parse(url);
                Intent superIntent = new Intent(Intent.ACTION_VIEW);
                superIntent.setData(myUri);
                startActivity(superIntent);
            }
        });


        if(!isNetworkAvailable()){
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "No Internet Connection!", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setApplicationId(getResources().getString(R.string.firebase_app_id)) // Required for Analytics.
                    .setApiKey(getResources().getString(R.string.firebase_api_key)) // Required for Auth.
                    .setDatabaseUrl("https://sareesonlinesale-5e81e.firebaseio.com/") // Required for RTDB.
                    .build();

            FirebaseApp myapp = null;
            boolean hasBeenInitialized=false;
            List<FirebaseApp> firebaseApps = FirebaseApp.getApps(getApplicationContext());
            for(FirebaseApp app : firebaseApps){
                if(app.getName().equals("sec")){
                    hasBeenInitialized=true;
                    myapp = app;
                }
            }

            if(!hasBeenInitialized) {
                myapp = FirebaseApp.initializeApp(getApplicationContext(), options,"sec");
            }

            FirebaseDatabase secondaryDatabase = FirebaseDatabase.getInstance(myapp);
            UsersRef = secondaryDatabase.getReference().child("Allow");
            UsersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if (!dataSnapshot.child("access").getValue().toString().equalsIgnoreCase("yes")) {
                            Toast.makeText(MainActivity.this, dataSnapshot.child("access").getValue().toString(), Toast.LENGTH_SHORT).show();
                            finish();
                            finishAffinity();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (Exception e){  }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater myMenuInflater = getMenuInflater();
        myMenuInflater.inflate(R.menu.super_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.myMenuOne:
                onBackPressed();
                break;

            case R.id.myMenuTwo:
                GoForward();
                break;

            case R.id.myMenuThree:
                superWebView.reload();
                break;

            case R.id.myMenuFour:
                shareApp();
                break;

        }
        return true;
    }

    private void GoForward() {
        if (superWebView.canGoForward()) {
            superWebView.goForward();
        } else {
            Toast.makeText(this, "Can't go further!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (superWebView.canGoBack()) {
            superWebView.goBack();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Exit App");
            dialog.setMessage("Browser has nothing to go back, so what next?");
            dialog.setPositiveButton("EXIT ME", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            dialog.setCancelable(false);
            dialog.setNegativeButton("STAY HERE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();

        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void shareApp(){
        final String appPackageName = getPackageName();
        Intent sharingintent = new Intent(android.content.Intent.ACTION_SEND);
        sharingintent.setType("text/plain");
        String shareBody = "Install Saree Online Sale "+"https://play.google.com/store/apps/details?id=" + appPackageName;
        sharingintent.putExtra(Intent.EXTRA_SUBJECT,"Saree Online Sale");
        sharingintent.putExtra(Intent.EXTRA_TEXT,shareBody);
        startActivity(Intent.createChooser(sharingintent,"Share via"));
    }

}