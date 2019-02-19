package org.d3ifcool.smart.AccountActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
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

        //Initial Widget
        findPass = findViewById(R.id.find_pass);
        email = findViewById(R.id.email);
        //Initial firebase
        auth = FirebaseAuth.getInstance();

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

    private TextWatcher findPassword = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {


        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

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
}
