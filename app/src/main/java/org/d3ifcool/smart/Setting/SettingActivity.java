package org.d3ifcool.smart.Setting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
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
import org.d3ifcool.smart.Notification.MyFirebaseMessagingService;
import org.d3ifcool.smart.R;
import org.d3ifcool.smart.WifiConfiguration.EsptouchDemoActivity;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class SettingActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private TextView configur;
    private Switch guest, door, thief;
    private boolean onOffGuest, onOffDoor, onOffThief;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private static final String TAG = "Setting";
    public static final String GLOBAL_SHARED_PREFS = "org.d3ifcool.smart";
    public static final String SWITCH1 = "guest";
    public static final String SWITCH2 = "door";
    public static final String SWITCH3 = "thief";


    public interface SettingInterface{

        void refresh();

        void triggerHouses();

        void triggerDoors();

    }

    @SuppressLint("CommitPrefEdits")
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

        configur = findViewById(R.id.conf);

        guest = findViewById(R.id.switch_guest);

        door = findViewById(R.id.switch_door);

        thief = findViewById(R.id.switch_thief);

        checkAccount();

        switchOnOff();

        prefs = getSharedPreferences(GLOBAL_SHARED_PREFS, MODE_PRIVATE);
        editor = prefs.edit();

        guest.setChecked(prefs.getBoolean(SWITCH1, true));

        door.setChecked(prefs.getBoolean(SWITCH2, true));

        thief.setChecked(prefs.getBoolean(SWITCH3, true));

        Toolbar toolbar = findViewById(R.id.toolsetting);

        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView_Bar);

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

                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

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

    public void switchOnOff(){
        guest.setChecked(true);

        guest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){

                    Log.d("guest : ","true");

                    onOffGuest = true;

                    guest.setChecked(true);

                    editor.putBoolean(SWITCH1, true);

                    editor.apply();

                    editor.commit();

//                    Toast.makeText(SettingActivity.this, "Guest notification On", Toast.LENGTH_SHORT).show();

                }

                else {

                    Log.d("guest : ","false");

                    onOffGuest = false;

                    guest.setChecked(false);

                    editor.putBoolean(SWITCH1, false);

                    editor.apply();

                    editor.commit();

//                    Toast.makeText(SettingActivity.this, "Guest notification Off", Toast.LENGTH_SHORT).show();

                }

            }

        });

        door.setChecked(true);

        door.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){

                    Log.d("door : ","true");

                    onOffDoor = true;

                    door.setChecked(true);

                    editor.putBoolean(SWITCH2, door.isChecked());

                    editor.apply();

                    editor.commit();

//                    Toast.makeText(SettingActivity.this, "Door notification On", Toast.LENGTH_SHORT).show();

                }

                else {

                    Log.d("door : ","false");

                    onOffDoor = false;

                    door.setChecked(false);

                    editor.putBoolean(SWITCH2, false);

                    editor.apply();

                    editor.commit();

//                    Toast.makeText(SettingActivity.this, "Door notification Off", Toast.LENGTH_SHORT).show();

                }
            }

        });

        thief.setChecked(true);

        thief.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){

                    Log.d("thief : ","true");

                    onOffThief = true;

                    thief.setChecked(true);

                    editor.putBoolean(SWITCH3, true);

                    editor.apply();

                    editor.commit();

//                    Toast.makeText(SettingActivity.this, "Thief notification On", Toast.LENGTH_SHORT).show();

                }

                else {

                    Log.d("thief : ","false");

                    onOffThief = false;

                    thief.setChecked(false);

                    editor.putBoolean(SWITCH3, false);

                    editor.commit();

//                    Toast.makeText(SettingActivity.this, "Thief notification On", Toast.LENGTH_SHORT).show();

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

    public void refresh(){

    }

}
