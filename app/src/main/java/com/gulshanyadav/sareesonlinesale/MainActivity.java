package com.gulshanyadav.sareesonlinesale;

import android.app.ActionBar;
import android.app.Activity;
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
import android.view.View;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView superImageView;
    ProgressBar superProgressBar;
    DatabaseReference UsersRef;
    WebView superWebView;
    RelativeLayout noConnectionLayout;
    TextView messageTextView;
    Button retryButton;

    private ProgressDialog loadingBar;
    TextView actionbartitle;
    ImageView back,forward,refresh,share;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        //getSupportActionBar().setElevation(0);
        View view = getSupportActionBar().getCustomView();
        actionbartitle = view.findViewById(R.id.title);
        back = view.findViewById(R.id.back_button);
        forward = view.findViewById(R.id.forward_button);
        refresh = view.findViewById(R.id.refresh_button);
        share = view.findViewById(R.id.share_button);

        superImageView = findViewById(R.id.myImageView);
        superProgressBar = findViewById(R.id.myProgressBar);
        superWebView = findViewById(R.id.myWebView);
        noConnectionLayout = (RelativeLayout)findViewById(R.id.noconnection_relativeLayout);
        messageTextView = (TextView)findViewById(R.id.message);
        retryButton = (Button)findViewById(R.id.retry_button);

        loadingBar = new ProgressDialog(this);

        superProgressBar.setMax(100);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                superWebView.reload();
            }
        });


        initButtons();

        superWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                initButtons();
                if(!isNetworkAvailable()){
                    superWebView.setVisibility(View.GONE);
                    noConnectionLayout.setVisibility(View.VISIBLE);
                }else {
                    loadingBar.setTitle("Loading");
                    loadingBar.show();
                    superWebView.setVisibility(View.VISIBLE);
                    noConnectionLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                initButtons();
                if(!isNetworkAvailable()){
                    superWebView.setVisibility(View.GONE);
                    noConnectionLayout.setVisibility(View.VISIBLE);
                }else {
                    loadingBar.dismiss();
                    superWebView.setVisibility(View.VISIBLE);
                    noConnectionLayout.setVisibility(View.GONE);
                }
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
                initButtons();
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
//                getActionBar().setTitle(title);
                initButtons();
                if(!isNetworkAvailable()){
                    actionbartitle.setText("No Internet Connection!");
                }else {
                    actionbartitle.setText(title);
                }
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

        checkInternetConnection();

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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoForward();
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                superWebView.reload();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareApp();
            }
        });

    }

    private void initButtons() {
        if (superWebView.canGoForward()) {
            forward.setEnabled(true);
            forward.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_forward_black_24dp));
        }else {
            forward.setEnabled(false);
            forward.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_forward_grey_24dp));
        }
        if(superWebView.canGoBack()){
            back.setEnabled(true);
            back.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        }else{
            back.setEnabled(false);
            back.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back_grey_24dp));
        }
    }


    private void checkInternetConnection() {
        if(!isNetworkAvailable()){
            superWebView.setVisibility(View.GONE);
            noConnectionLayout.setVisibility(View.VISIBLE);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater myMenuInflater = getMenuInflater();
        myMenuInflater.inflate(R.menu.super_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        back.setEnabled(true);
        switch (item.getItemId()) {

            case R.id.myMenuFive:
                superWebView.loadUrl("http://9999071999.com/education/Weather.php");
                break;

            case R.id.myMenuSix:
                superWebView.loadUrl("http://9999071999.com/education/Timepass.php");
                break;

            case R.id.myMenuSeven:
                superWebView.loadUrl("http://9999071999.com/education/Greetings.php");
                break;

            case R.id.myMenuEight:
                superWebView.loadUrl("http://9999071999.com/education/Status.php");
                break;

            case R.id.myMenuNine:
                superWebView.loadUrl("http://9999071999.com/education/Status.php");
                break;

            case R.id.myMenuTen:
                superWebView.loadUrl("http://9999071999.com/education/Viralvideos.php");
                break;

            case R.id.myMenuEleven:
                superWebView.loadUrl("http://9999071999.com/education/Join.php");
                break;

            case R.id.myMenuTwelve:
                superWebView.loadUrl("http://9999071999.com/education/Discount.php");
                break;

            case R.id.myMenuThirteen:
                superWebView.loadUrl("http://9999071999.com/education/quiz.php");
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