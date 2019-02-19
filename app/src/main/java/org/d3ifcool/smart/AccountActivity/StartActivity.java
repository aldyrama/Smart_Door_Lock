package org.d3ifcool.smart.AccountActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.d3ifcool.smart.Data;
import org.d3ifcool.smart.Home.MainActivity;
import org.d3ifcool.smart.R;

public class StartActivity extends AppCompatActivity {

    TextView txt_appname;
    Button btn_signin, btn_signup;

    FirebaseAuth auth;
    String profileid;
    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        txt_appname = findViewById(R.id.txt_appname);
        btn_signin = findViewById(R.id.button_signin);
        btn_signup = findViewById(R.id.button_signup);

        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        String showEmail = firebaseUser.getEmail();
        String username = String.valueOf(auth);
        SharedPreferences prefs = getSharedPreferences("PREFS", MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");

        if (auth.getCurrentUser() != null){
            String temp[] = username.split("@");
            Data.user  = temp[0];
            startActivity(new Intent(StartActivity.this, MainActivity.class));
        }

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/Aaargh.ttf");
        txt_appname.setTypeface(typeface);
        txt_appname.setTypeface(null, Typeface.BOLD);

        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, RegistActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });
    }
}
