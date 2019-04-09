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
import org.d3ifcool.smart.Model.Setting;
import org.d3ifcool.smart.Model.User;
import org.d3ifcool.smart.R;
import org.d3ifcool.smart.WifiConfiguration.EsptouchDemoActivity;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class SettingActivity extends AppCompatActivity {
    private static final String TAG = "Setting";

    int preSelectedIndex = -1;
    private ImageView doorNotif, guestNotif;
    private String statusDoor, statusGuest;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private TextView configur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
//        setStatustBarColor(R.color.colorWhite);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        );

        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        guestNotif = findViewById(R.id.guest_notif);
        configur = findViewById(R.id.conf);

        onOffNotifGuest();
        checkAccount();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolsetting);
        setSupportActionBar(toolbar);

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

                    case R.id.nav_setting :

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


//        final List<Setting> setting = new ArrayList<>();
//        setting.add(new Setting("Door status notification",false));
//        setting.add(new Setting("Guest notification",false));
//        setting.add(new Setting("",false));
//
//        final SettingAdapter adapter = new SettingAdapter(this, setting);
//        listView.setAdapter(adapter);
//
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                Setting model = setting.get(i); //changed it to model because viewers will confused about it
//
//                model.setCheck(true);
//
//                setting.set(i, model);
//
//                if (preSelectedIndex > 1){
//
//                    Setting preRecord = setting.get(preSelectedIndex);
//                    preRecord.setCheck(true);
//
//                    setting.set(preSelectedIndex, preRecord);
//
//                }
//                else {
//
//                    preSelectedIndex = i;
//
//                    //now update adapter so we are going to make a update method in adapter
//                    //now declare adapter final to access in inner method
//
//                    adapter.updateRecords(setting);
//                }
//            }
//        });

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
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getEmail().replace(".", ","))
                .child("Notifications");
        guestNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (statusGuest == "enable"){
                    guestNotif.setImageResource(R.drawable.switch_on);
                    reference.child("guest").setValue(statusGuest);

                    statusGuest = "disable";

                }

                else {
                    guestNotif.setImageResource(R.drawable.switch_off);
                    reference.child("guest").setValue(statusGuest);
                    statusGuest = "enable";

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
