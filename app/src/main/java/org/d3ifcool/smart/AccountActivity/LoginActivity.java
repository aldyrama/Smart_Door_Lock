package org.d3ifcool.smart.AccountActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.d3ifcool.smart.Data;
import org.d3ifcool.smart.Home.MainActivity;
import org.d3ifcool.smart.R;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    //Widget
    private EditText email, password;
    private Button login;
    private TextView txt_signup, txt_find_pass, count;
    private int counterInput = 0;
    private int countDown = 30;
    //Firebase
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setFlags(

                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,

                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN

        );

        viewLogin();

        //Initial firebase
        auth = FirebaseAuth.getInstance();

        String username = String.valueOf(auth);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (auth.getCurrentUser() != null){

            String temp[] = username.split("@");

            Data.user  = temp[0];

            startActivity(new Intent(LoginActivity.this, MainActivity.class));

        }

        //Action intent signUp
        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LoginActivity.this, RegistActivity.class));

                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }

        });

        //Action login
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ProgressDialog pd = new ProgressDialog(LoginActivity.this);

                pd.setMessage("Please wait...");

                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                pd.setCanceledOnTouchOutside(false);

                pd.setCancelable(false);

                pd.show();

                final String str_email = email.getText().toString();

                String str_password = password.getText().toString();

                if (TextUtils.isEmpty(str_email)) {

                    counterInput = 0;

                    email.setError("Email required!");

                    pd.hide();

                }

                else if (TextUtils.isEmpty(str_password)){

                        counterInput = 0;

                        password.setError("Password required!");

                        pd.hide();

                }

                else {

                    auth.signInWithEmailAndPassword(str_email, str_password)

                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {

                                        counterInput = 0;

                                        String temp[] = str_email.split("@");

                                        Data.user  = temp[0];

                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                                                .child(auth.getCurrentUser().getUid());

                                        reference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                pd.dismiss();

                                                String temp[] = str_email.split("@");

                                                Data.user  = temp[0];

                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                                startActivity(intent);

                                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                                                finish();

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                pd.dismiss();

                                            }

                                        });

                                    }

                                    else {

                                        counterInput++;

                                        pd.dismiss();

                                        Toast.makeText(LoginActivity.this, "Authentication failed, check email & password ", Toast.LENGTH_SHORT).show();

                                        if (counterInput == 7) {

                                            new CountDownTimer(30000, 1000) {

                                                @Override
                                                public void onTick(long millisUntilFinished) {

                                                    email.setEnabled(false);

                                                    password.setEnabled(false);

                                                    login.setEnabled(false);

                                                    txt_find_pass.setEnabled(false);

                                                    txt_signup.setEnabled(false);

                                                    count.setVisibility(View.VISIBLE);

                                                    count.setText("Try again in " + countDown + " seconds");

                                                    countDown--;

                                                }

                                                @Override
                                                public void onFinish() {

                                                    countDown = 30;

                                                    counterInput = 0;

                                                    email.setEnabled(true);

                                                    password.setEnabled(true);

                                                    login.setEnabled(true);

                                                    txt_signup.setEnabled(true);

                                                    txt_find_pass.setEnabled(true);

                                                    count.setVisibility(View.GONE);

                                                }

                                            }.start();

                                        }

                                    }

                                }

                            });

                }

            }

        });

    }

    public void getUsername(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child(Data.user);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String usernameget = dataSnapshot.getValue(String.class);

                Log.w(TAG, "Failed to read value." + usernameget);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

    //Initial widget
    private void viewLogin() {

        email = findViewById(R.id.email_login);

        password = findViewById(R.id.pass_login);

        login = findViewById(R.id.submit_login);

        txt_signup = findViewById(R.id.signup);

        txt_find_pass = findViewById(R.id.find_password);

        count = findViewById(R.id.countDownTime);

        txt_find_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity.this, FindPassword.class));

                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }

        });

    }

    //Checking connection
    private boolean haveNetwork() {

        boolean have_WIFI = false;

        boolean have_MOBILE = false;

        ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();

        for (NetworkInfo info:networkInfos){

            if (info.getTypeName().equalsIgnoreCase("WIFI"));

            if (info.isConnected())

                have_WIFI = true;

            if (info.getTypeName().equalsIgnoreCase("MOBILE"));

            if (info.isConnected())

                have_MOBILE = true;

        }

        return have_WIFI || have_MOBILE;

    }

    @Override
    public void finish() {

        super.finish();

    }

    @Override
    public void onBackPressed() {

        Intent out = new Intent(Intent.ACTION_MAIN);

        out.addCategory(Intent.CATEGORY_HOME);

        out.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(out);

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

}
