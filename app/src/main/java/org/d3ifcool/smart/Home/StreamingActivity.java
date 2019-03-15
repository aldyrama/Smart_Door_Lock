package org.d3ifcool.smart.Home;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.andretietz.android.controller.ActionView;
import com.andretietz.android.controller.DirectionView;
import com.andretietz.android.controller.InputView;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.d3ifcool.smart.R;

public class StreamingActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    ProgressDialog pd;
    public static final String API_KEY = "AIzaSyCLxKEuRRW_Amn4ETl3OLqJY4qEvS6UmAk";
    public static final String VIDEO_ID = "9Yam5B_iasY";
//    String videoURL = "https://youtu.be/SLfzeEqr0M4";

    //Firebse
    FirebaseDatabase database;
    FirebaseUser firebaseUser;
    DatabaseReference myRef, myRef0;

    int count = 0;

    int upInteger = 0;
    int leftInteger = 0;
    private static final float MAX_BUG_SPEED_DP_PER_S = 300f;

    ImageButton actionUp, actionBottom, actionLeft, actionRight, actiondefault;
    TextView horizontal, vertical;



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        );

        YouTubePlayerView youTubePlayerView = (YouTubePlayerView) findViewById(R.id.videoView);
        youTubePlayerView.initialize(API_KEY, this);

        actionUp = findViewById(R.id.action_up);
        actionBottom = findViewById(R.id.action_bottom);
        actionLeft = findViewById(R.id.action_left);
        actionRight = findViewById(R.id.action_right);
        actiondefault = findViewById(R.id.action_center);

        horizontal = findViewById(R.id.count_horizontal);
        vertical = findViewById(R.id.count_vertical);

         database = FirebaseDatabase.getInstance();
         myRef = database.getReference().child("vertical");
         myRef0 = database.getReference().child("horizontal");

         actionUp.setOnTouchListener(new View.OnTouchListener() {
             @Override
             public boolean onTouch(View v, MotionEvent event) {
                 increaseInteger(v);
                 return true;
             }
         });

         actionBottom.setOnTouchListener(new View.OnTouchListener() {
             @Override
             public boolean onTouch(View v, MotionEvent event) {
                 decreaseInteger(v);
                 return true;
             }
         });

         actionLeft.setOnTouchListener(new View.OnTouchListener() {
             @Override
             public boolean onTouch(View v, MotionEvent event) {
                decreaseIntegerleft(v);
                return true;
             }
         });

         actionRight.setOnTouchListener(new View.OnTouchListener() {
             @Override
             public boolean onTouch(View v, MotionEvent event) {
                 increaseIntegerleft(v);
                 return true;
             }
         });

         actiondefault.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 upInteger = 0;
                 leftInteger = 0;
                 myRef.setValue(upInteger);
                 myRef0.setValue(leftInteger);
                 horizontal.setText(Integer.toString(leftInteger));
                 vertical.setText(Integer.toString(upInteger));
             }
         });

    }

    public void increaseInteger(View view) {
        upInteger = upInteger + 1;
        display(upInteger);

        if (upInteger >= 360 ){
            upInteger = 360;

        }

    }public void decreaseInteger(View view) {
        upInteger = upInteger - 1;
        display(upInteger);

        if (upInteger <= -360){
            upInteger = -360;
        }
    }

    private void display(int number) {
        vertical = findViewById(R.id.count_vertical);
        myRef.setValue(number);
        vertical.setText(Integer.toString(number));
    }

    public void increaseIntegerleft(View view) {
        leftInteger = leftInteger + 1;
        displayleft(leftInteger);
        if (leftInteger >= 360 ){
            leftInteger = 360;
        }

    }public void decreaseIntegerleft(View view) {
        leftInteger = leftInteger - 1;
        displayleft(leftInteger);

        if (leftInteger <= -360){
            leftInteger = -360;
        }
    }

    private void displayleft(int number) {
        myRef0.setValue(number);
        horizontal.setText(Integer.toString(number));
    }


    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
        Toast.makeText(this, "Failured to Initialize!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        player.setPlayerStateChangeListener(playerStateChangeListener);
//        player.setPlaybackEventListener(playbackEventListener);
        player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION);
        player.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
        player.play();
        player.loadVideo(VIDEO_ID);

        if (!wasRestored) {
            player.cueVideo(VIDEO_ID);
        }
    }


    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {
        @Override
        public void onBuffering(boolean arg0) {
        }
        @Override
        public void onPaused() {
        }
        @Override
        public void onPlaying() {
        }
        @Override
        public void onSeekTo(int arg0) {
        }
        @Override
        public void onStopped() {
        }
    };

    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
        @Override
        public void onAdStarted() {
        }
        @Override
        public void onError(YouTubePlayer.ErrorReason arg0) {
        }
        @Override
        public void onLoaded(String arg0) {
        }
        @Override
        public void onLoading() {
        }
        @Override
        public void onVideoEnded() {
        }
        @Override
        public void onVideoStarted() {
        }
    };

    @Override
    public void onBackPressed() {
       startActivity(new Intent(StreamingActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }
}
