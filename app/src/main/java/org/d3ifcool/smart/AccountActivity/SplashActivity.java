package org.d3ifcool.smart.AccountActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.d3ifcool.smart.R;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener {

    TextView protxt;
    private long ms = 0, splashTime = 1600;
    private boolean splashActiv = true, paused = false;
    Dialog dialog;
    TextView retryapp, exitapp, check;
    Handler mHandler;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setStatustBarColor(R.color.colorWhite);

        final LinearLayout lr = findViewById(R.id.lr);

        protxt = findViewById(R.id.prolockname);



        Typeface font = Typeface.createFromAsset(getAssets(), "font/Fontspring_DEMO_microsquare_bold.ttf");
        protxt.setTypeface(font);

        final Thread thread = new Thread(){
            @Override
            public void run() {
                try{
                    while (splashActiv && ms<splashTime){
                        if (!paused){
                            ms = ms + 100;
                            sleep(100);

                        }
                    }
                }catch (Exception e){

                }finally {
                    if (!isOnline()){
                        Snackbar snackbar = Snackbar
                                .make(lr,"No internet access", Snackbar.LENGTH_INDEFINITE)
                                .setAction("Retry", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        recreate();



                                    }
                                });

                        snackbar.show();
                    }

                    else {
                        goMain();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }
            }
        };

        thread.start();
    }



    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @SuppressLint("ResourceAsColor")
    private void setStatustBarColor(@ColorRes int statustBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            int color = ContextCompat.getColor(this, statustBarColor);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
            window.setTitleColor(R.color.black);
        }
    }

    private void goMain() {
        startActivity(new Intent(SplashActivity.this, StartActivity.class));
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    @Override
    public void onClick(View v) {

    }
}
