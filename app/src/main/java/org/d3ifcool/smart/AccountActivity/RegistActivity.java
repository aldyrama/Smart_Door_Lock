package org.d3ifcool.smart.AccountActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.futuremind.recyclerviewfastscroll.Utils;
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
import com.google.firebase.database.core.Constants;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.d3ifcool.smart.Account.Account;
import org.d3ifcool.smart.Data;
import org.d3ifcool.smart.FirebaseDatabase.DatabaseAdapter;
import org.d3ifcool.smart.FirebaseDatabase.DatabaseFirebase;
import org.d3ifcool.smart.Home.MainActivity;
import org.d3ifcool.smart.Internet.CheckConnection;
import org.d3ifcool.smart.Model.House;
import org.d3ifcool.smart.Model.User;
import org.d3ifcool.smart.R;

import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.internal.Util;

public class RegistActivity extends AppCompatActivity {

    //Widget
    Context context;
    private RadioGroup radioTypeGroup;
    private RadioButton radioTypeButton;
    public static Activity mActivity;
    EditText username, fullname, email, password, houseName;
    Button register;
    TextView txt_login, txt_regist;
    ProgressDialog pd;
    RadioGroup radioGroup;
    //Firebase
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    public static final String mypreference = "mypref";
    public static final String Username = "usernameKey";

    FirebaseStorage firebaseStorage;
    Uri resultUri;
    private boolean isRegister;
    private String mEmail;
    private FirebaseAuth mAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        setStatustBarColor(R.color.colorWhite);
        view();
        mActivity = this;


        radioGroup = findViewById(R.id.type_account);
        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRegister = true;
                pd = new ProgressDialog(RegistActivity.this);
                pd.setMessage("Please wait...");
                pd.setCancelable(false);
                pd.setCanceledOnTouchOutside(false);
                pd.show();

                String str_username = username.getText().toString();
                String str_fullname = fullname.getText().toString();
                String str_email = email.getText().toString();
                String str_password = password.getText().toString();

                if (str_username.isEmpty()){
                    pd.hide();
                    username.setError("username required!");
                }

                else if (str_fullname.isEmpty()){
                    pd.hide();
                    fullname.setError("fullname required!");
                }

                else if (str_email.isEmpty()){
                    email.setError("email required!");
                    pd.hide();

                }


                else if(str_password.isEmpty()){
                    pd.hide();
                    password.setError("password required!");
                }

                else if (str_password.length() < 6){
                    pd.hide();
                    password.setError("Password must have 6 characters!");

                } else {
                    register(str_username, str_fullname, str_email, str_password);



                }
            }
        });

    }


    public static boolean isEmailValid(String email){
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    private void view(){
        username = findViewById(R.id.username_edittxt);
        email = findViewById(R.id.email_edittxt);
        fullname = findViewById(R.id.fullname_edittxt);
        password = findViewById(R.id.pass_edittxt);
        register = findViewById(R.id.register);
        txt_login = findViewById(R.id.txt_login);
        txt_regist = findViewById(R.id.text_regist);


        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/Fontspring_DEMO_microsquare_bold.ttf");
        txt_regist.setTypeface(typeface);

    }


    public void register(final String username, final String fullname, final String email, String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegistActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userID = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(email.replace(".",","));
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("email", email);
                            map.put("username", username.toLowerCase());
                            map.put("fullname", fullname);
                            map.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/smartdoor-7d0e6.appspot.com/o/userphoto.png?alt=media&token=b9ed95a7-56ef-4ee6-9dcf-6b5669fccb8c");
                            map.put("typeAccount", accountType());


                            reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
//                                        sendEmailVerification();
                                        SharedPreferences sharedpreferences = getSharedPreferences(mypreference,
                                                Context.MODE_PRIVATE);

                                        SharedPreferences sp = getSharedPreferences("your_shared_pref_name", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sp.edit();
                                        editor.putString("username", username);
                                        editor.apply();

                                        Data.user = username;
                                        pd.dismiss();
//                                        auth.signOut();
                                        Intent intent = new Intent(RegistActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                                    }
                                }
                            });
                        } else {

                            pd.dismiss();

                            Toast.makeText(RegistActivity.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public String accountType(){

        radioTypeGroup = (RadioGroup) findViewById(R.id.type_account);

        int selectedId = radioTypeGroup.getCheckedRadioButtonId();

        radioTypeButton = (RadioButton) findViewById(selectedId);

        if(radioTypeButton.getText().equals("Owner")) {
            return "Owner";
        }

        if(radioTypeButton.getText().equals("Member")) {
            return "Member";
        }

        return "";

    }


    private void sendEmailVerification() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(RegistActivity.this,"Check your Email for verification",Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                    }
                }
            });
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onBackPressed() {
       startActivity(new Intent(RegistActivity.this, LoginActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

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
