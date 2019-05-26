package org.d3ifcool.smart.Onvif;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.d3ifcool.smart.Model.Camera;
import org.d3ifcool.smart.Model.House;
import org.d3ifcool.smart.R;
import org.videolan.libvlc.Dialog;

public class OnvifInput extends AppCompatActivity {

    private TextView ip, login, password;
    private Button connect;
    private ObjectAnimator textBlinking;
    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onvif_config);

        ip = findViewById(R.id.ipAddress);

        login = findViewById(R.id.login);

        password = findViewById(R.id.password);

        connect = findViewById(R.id.button);

        pd = new ProgressDialog(this, R.style.MyAlertDialogStyle);

        setCamera();

        Intent i = getIntent();
        final String deviceCode =i.getExtras().getString("DEVICECODE_KEY");

        Log.d("kode", " : " + deviceCode);

    }

    public void setCamera(){

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pd.setMessage("Please wait...");

                pd.show();

                final String str_ip = ip.getText().toString();

                final String str_login = login.getText().toString();

                final String str_password = password.getText().toString();

//                final Camera camera = new Camera(str_ip, str_login, str_password);

                if (TextUtils.isEmpty(str_ip)) {

                    ip.setError("Ip address required");

                    pd.hide();

                }

                if (TextUtils.isEmpty(str_login)) {

                    login.setError("Login required");

                    pd.hide();

                }

                if (TextUtils.isEmpty(str_password)){

                    password.setError("Password required");

                    pd.hide();

                }

                else {

                    Intent i = getIntent();
                    final String deviceCode =i.getExtras().getString("DEVICECODE_KEY");

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Devices").child(deviceCode).child("Camera");

//                    ref.setValue(camera);

                    pd.hide();

                }

            }
        });

    }
}
