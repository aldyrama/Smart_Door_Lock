package org.d3ifcool.smart.AccountActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.d3ifcool.smart.Family.FamilyActivity;
import org.d3ifcool.smart.R;


public class ChangePassActivity extends AppCompatActivity implements View.OnClickListener {

    //Widget
    private Button submit;
    private EditText repass, newpass;
    private ProgressDialog loading;
    //Firebase
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        getWindow().setFlags(

                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,

                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN

        );

        //Initial widget
        auth = FirebaseAuth.getInstance();

        submit = findViewById(R.id.submit_pass);

        repass = findViewById(R.id.repass);

        newpass = findViewById(R.id.pass);

        loading = new ProgressDialog(this, R.style.MyAlertDialogStyle);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        setDataToView(user);

        //Action change password
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newPass = newpass.getText().toString().trim();

                String rePass = repass.getText().toString().trim();

                loading.setMessage("Please wait...");

                loading.show();

                loading.setCanceledOnTouchOutside(true);

                if (user != null && !newpass.getText().toString().trim().equals("")) {

                    if (newpass.getText().toString().trim().length() < 6) {

                        newpass.setError("Password too short, enter minimum 6 characters");

                        loading.hide();

                    }

                    else if (!rePass.equals(newPass)){

                        loading.hide();

                        Toast.makeText(ChangePassActivity.this, "Your password do not match with your confirm password", Toast.LENGTH_SHORT).show();

                    }

                    else {

                        user.updatePassword(newpass.getText().toString().trim())

                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            Toast.makeText(ChangePassActivity.this, "Password is updated, sign in with new password!",
                                                    Toast.LENGTH_SHORT).show();

                                            signOut();

                                            startActivity(new Intent(ChangePassActivity.this, LoginActivity.class));

                                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                                           loading.hide();

                                        }

                                        else {

                                            Toast.makeText(ChangePassActivity.this, "Failed to update password!",
                                                    Toast.LENGTH_SHORT).show();

                                            loading.hide();

                                        }

                                    }

                                });

                    }

                }

                else if (newpass.getText().toString().trim().equals("")) {

                    newpass.setError("Enter password");

                    loading.hide();

                }

            }

        });

    }

    private void setDataToView(FirebaseUser user) {

    }

    // Action automatic signOut
    public void signOut() {

        auth.signOut();

    }

    FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            FirebaseUser user = firebaseAuth.getCurrentUser();

            if (user == null) {

                startActivity(new Intent(ChangePassActivity.this, LoginActivity.class));

                finish();

            }

            else {

                setDataToView(user);

            }

        }

    };

    //Action Onclick using ID
    public void onClickView(View view) {

        switch (view.getId()){

            case R.id.btn_prev_change :

                Intent intent = new Intent(ChangePassActivity.this, FamilyActivity.class);

               startActivity(intent);

               break;

            case R.id.submit_pass :

                break;
        }

    }

    @Override
    public void onClick(View v) {

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
