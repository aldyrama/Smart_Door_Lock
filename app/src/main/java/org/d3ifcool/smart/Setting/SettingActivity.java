package org.d3ifcool.smart.Setting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.d3ifcool.smart.Adapter.SettingAdapter;
import org.d3ifcool.smart.BottomNavigation.BottomNavigationViewHelper;
import org.d3ifcool.smart.Family.FamilyActivity;
import org.d3ifcool.smart.Home.MainActivity;
import org.d3ifcool.smart.Model.Notif;
import org.d3ifcool.smart.Model.Setting;
import org.d3ifcool.smart.Model.User;
import org.d3ifcool.smart.R;
import org.d3ifcool.smart.WifiConfiguration.EsptouchDemoActivity;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class SettingActivity extends AppCompatActivity {
    private static final String TAG = "Setting";

    private ImageView doorNotif, guestNotif;
    private boolean statusDoor, statusGuest;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private DatabaseReference referenceNotif;
    private TextView configur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        getWindow().setFlags(

                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,

                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN

        );

        auth = FirebaseAuth.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        guestNotif = findViewById(R.id.guest_notif);

        doorNotif = findViewById(R.id.door_notif);

        configur = findViewById(R.id.conf);

        onOffNotifGuest();

        onOffNotifDoor();

        checkAccount();

        Toolbar toolbar = findViewById(R.id.toolsetting);

        setSupportActionBar(toolbar);

        referenceNotif = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getEmail().replace(".", ","))
                .child("Notifications");

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);

        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        Menu menu = bottomNavigationView.getMenu();

        MenuItem menuItem = menu.getItem(2);

        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.nav_home:

                        Intent intent1 = new Intent(SettingActivity.this, MainActivity.class);

                        startActivity(intent1);

                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                        break;

                    case R.id.nav_user:

                        Intent intent3 = new Intent(SettingActivity.this, FamilyActivity.class);

                        startActivity(intent3);

                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                        break;

                    case R.id.nav_setting:

                        break;

                }

                return false;

            }

        });

        configur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SettingActivity.this, EsptouchDemoActivity.class));

            }

        });


        referenceNotif.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {

                    Notif notif = dataSnapshot.getValue(Notif.class);

                    boolean Guest = notif.isGuest();

                    boolean Door = notif.isDoor();

                    if (Guest){

                        guestNotif.setImageResource(R.drawable.switch_on);

                    }

                    else {

                        guestNotif.setImageResource(R.drawable.switch_off);

                    }

                    if (Door){

                        doorNotif.setImageResource(R.drawable.switch_on);

                    }

                    else {

                        doorNotif.setImageResource(R.drawable.switch_off);

                    }

                }catch (Exception e){}

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

    //check account login
    private void checkAccount() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getEmail()
                .replace(".",","));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (getContext() == null){

                    return;

                }

                User user = dataSnapshot.getValue(User.class);

                String check = user.getTypeAccount();

                if (check.equals("Owner")) {

                    configur.setEnabled(true);

                }

                else {

                    configur.setEnabled(false);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

    public void onOffNotifGuest(){

        guestNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (statusGuest){

                    guestNotif.setImageResource(R.drawable.switch_on);

                    referenceNotif.child("guest").setValue(statusGuest);

                    Toast.makeText(SettingActivity.this, "guest notification is active", Toast.LENGTH_SHORT).show();

                    statusGuest = false;

                }

                else {

                    guestNotif.setImageResource(R.drawable.switch_off);

                    referenceNotif.child("guest").setValue(statusGuest);

                    Toast.makeText(SettingActivity.this, "guest notification is not active", Toast.LENGTH_SHORT).show();

                    statusGuest = true;

                }

            }

        });

    }

    public void onOffNotifDoor(){

        doorNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (statusDoor){

                    doorNotif.setImageResource(R.drawable.switch_on);

                    referenceNotif.child("door").setValue(statusDoor);

                    Toast.makeText(SettingActivity.this, "guest notification is active", Toast.LENGTH_SHORT).show();

                    statusDoor = false;

                }

                else {

                    doorNotif.setImageResource(R.drawable.switch_off);

                    referenceNotif.child("door").setValue(statusDoor);

                    Toast.makeText(SettingActivity.this, "guest notification is not active", Toast.LENGTH_SHORT).show();

                    statusDoor = true;

                }

            }

        });

    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(Intent.ACTION_MAIN);

        intent.addCategory(Intent.CATEGORY_HOME);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);

    }

    @SuppressLint("ResourceAsColor")
    private void setStatustBarColor(@ColorRes int statustBarColor) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

            int color = ContextCompat.getColor(this, statustBarColor);

            Window window = getWindow();

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(color);

            window.setTitleColor(R.color.black);

        }

    }

}
