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

import org.d3ifcool.smart.Activity.ActivityFeature;
import org.d3ifcool.smart.Adapter.SettingAdapter;
import org.d3ifcool.smart.BottomNavigation.BottomNavigationViewHelper;
import org.d3ifcool.smart.Family.FamilyActivity;
import org.d3ifcool.smart.Home.MainActivity;
import org.d3ifcool.smart.Model.Setting;
import org.d3ifcool.smart.R;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {
    private static final String TAG = "Setting";

    int preSelectedIndex = -1;
    private ImageView doorNotif, guestNotif;
    int statusDoor, statusGuest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setStatustBarColor(R.color.colorWhite);

        doorNotif = findViewById(R.id.doo_notif);
        guestNotif = findViewById(R.id.guest_notif);

        onOffNotifDoor();
        onOffNotifGuest();

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

//                    case R.id.nav_activity:
//                        Intent intent2 = new Intent(SettingActivity.this, ActivityFeature.class);
//                        startActivity(intent2);
//                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                        break;

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

    public void onOffNotifDoor(){
        doorNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (statusDoor == 0){
                    doorNotif.setImageResource(R.drawable.switch_off);
                    statusDoor = 1;
                }
                else {
                    doorNotif.setImageResource(R.drawable.switch_on);
                    statusDoor = 0;
                }

            }
        });

    }

    public void onOffNotifGuest(){
        guestNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (statusGuest == 0){
                    guestNotif.setImageResource(R.drawable.switch_off);
                    statusGuest = 1;
                }

                else {
                    guestNotif.setImageResource(R.drawable.switch_on);
                    statusGuest = 0;
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
