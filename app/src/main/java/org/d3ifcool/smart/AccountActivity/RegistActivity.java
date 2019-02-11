package org.d3ifcool.smart.AccountActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
        view();
        mActivity = this;


        radioGroup = findViewById(R.id.type_account);
        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistActivity.this, LoginActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRegister = true;
                pd = new ProgressDialog(RegistActivity.this);
                pd.setMessage("Please wait...");
                pd.show();

                String str_username = username.getText().toString();
                String str_fullname = fullname.getText().toString();
                String str_email = email.getText().toString();
                String str_password = password.getText().toString();

                if (TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullname) || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)){
                    Toast.makeText(RegistActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                } else if(str_password.length() < 6){
                    Toast.makeText(RegistActivity.this, "Password must have 6 characters!", Toast.LENGTH_SHORT).show();
                } else {
                    register(str_username, str_fullname, str_email, str_password);



                }
            }
        });



    }

//    public void createAccount(View view) {
//        isRegister = true;
//        pd = new ProgressDialog(RegistActivity.this);
//        pd.setMessage("Please wait...");
//        pd.show();
//
//        final DatabaseAdapter databaseAdapter = new DatabaseAdapter(this);
//
//        try {
//            Account account = databaseAdapter.getAccount();
//            if (account.getmUsername() != null) {
//                Intent gotoMain = new Intent(this, MainActivity.class);
//                startActivity(gotoMain);
//            } else {
//
//            }
//        } catch (Exception e) {
//            final EditText username = (EditText) findViewById(R.id.username_edittxt);
//            final EditText fullname = (EditText) findViewById(R.id.fullname_edittxt);
//            final EditText email = (EditText) findViewById(R.id.email_edittxt);
//            final EditText password = (EditText) findViewById(R.id.pass_edittxt);
//
//            try {
//                final DatabaseFirebase databaseFirebase = new DatabaseFirebase(this);
//
//                CheckConnection checkConnection = new CheckConnection(this);
//
//                if (checkConnection.checkInternetConnection()) {
//
//                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
//                    mDatabase.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            for (DataSnapshot children : dataSnapshot.getChildren()) {
//                                if (children.getKey().equals("usr_" + username.getText().toString())) {
//                                    messageDialog("the account name has been used, try another");
//                                    pd.hide();
//                                    return;
//
//                                }
//                            }
//
//                            String str_username = username.getText().toString();
//                            String str_fullname = fullname.getText().toString();
//                            String str_email = email.getText().toString();
//                            String str_password = password.getText().toString();
//
//                            if (TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullname) || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)) {
//                                Toast.makeText(RegistActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
//                            } else if (str_password.length() < 6) {
//                                Toast.makeText(RegistActivity.this, "Password must have 6 characters!", Toast.LENGTH_SHORT).show();
//
//                            } else {
//                                databaseFirebase.insertUser(
//                                        new User(username.getText().toString(),
//                                                password.getText().toString(),
//                                                accountType()
//
//
//                                                )
//                                );
//                            }
//                            pd.dismiss();
//
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
//
//                }
//                else {
//                    messageDialog("Failed to Register, Try Your Internet Check");
//                    pd.dismiss();
//                }
//
//            } catch (IllegalArgumentException err) {
//                messageDialog("Nama User Tidak Boleh Kosong");
//                pd.dismiss();
//                return;
//            }
//        }
//    }

    private void view(){
        username = findViewById(R.id.username_edittxt);
        email = findViewById(R.id.email_edittxt);
        fullname = findViewById(R.id.fullname_edittxt);
        password = findViewById(R.id.pass_edittxt);
        register = findViewById(R.id.register);
        txt_login = findViewById(R.id.txt_login);
        txt_regist = findViewById(R.id.text_regist);


        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/RemachineScript_Personal_Use.ttf");
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

                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child(username);
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
                                        SharedPreferences sharedpreferences = getSharedPreferences(mypreference,
                                                Context.MODE_PRIVATE);

                                        SharedPreferences sp = getSharedPreferences("your_shared_pref_name", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sp.edit();
                                        editor.putString("username", username);
                                        editor.apply();

                                        Data.user = username;
                                        pd.dismiss();
                                        Intent intent = new Intent(RegistActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
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

    private void messageDialog(String text) {

        final android.app.AlertDialog.Builder message = new android.app.AlertDialog.Builder(this);
        message.setTitle("Info");
        message.setMessage(text);
        message.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        message.create().show();


    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onBackPressed() {
       startActivity(new Intent(RegistActivity.this, LoginActivity.class));

    }


}
