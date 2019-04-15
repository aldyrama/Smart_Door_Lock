package org.d3ifcool.smart;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeviceStatus extends AppCompatActivity {

    int counter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Devices").child(deviceCode);

        new CountDownTimer(30000, 1000){
            public void onTick(long millisUntilFinished){
                counter++;
            }
            public  void onFinish(){

            }

        }.start();

    }
}
