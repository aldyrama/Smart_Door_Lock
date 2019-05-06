package org.d3ifcool.smart.AccountActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.d3ifcool.smart.R;


public class FindPassword extends AppCompatActivity {

    //Widget
    Button findPass;
    EditText email;
    //Firebase
    FirebaseAuth auth;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        getWindow().setFlags(

                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,

                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN

        );

        //Initial Widget
        findPass = findViewById(R.id.find_pass);

        email = findViewById(R.id.email);
        //Initial firebase
        auth = FirebaseAuth.getInstance();

        pd = new ProgressDialog(this, R.style.MyAlertDialogStyle);

        //Action find password
        findPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailInput = email.getText().toString().trim();

                if (TextUtils.isEmpty(emailInput)){

                    email.setError("Email required!");

                }

                else {

                    pd.setMessage("Please wait");

                    pd.show();

                    auth.sendPasswordResetEmail(email.getText().toString())

                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    pd.hide();

                                    if (task.isSuccessful()){

                                        Toast.makeText(FindPassword.this, "Password send to your email", Toast.LENGTH_SHORT).show();

                                    }

                                    else {

                                        Toast.makeText(FindPassword.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                    }

                                }

                            });

                }

            }

        });

    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(FindPassword.this, LoginActivity.class));

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    public void onClickView(View view) {

        switch (view.getId()){

            case R.id.btn_prev :

                startActivity(new Intent(FindPassword.this, LoginActivity.class));

                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        }

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
