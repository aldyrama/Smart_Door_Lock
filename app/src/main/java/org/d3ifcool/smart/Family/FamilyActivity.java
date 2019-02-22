package org.d3ifcool.smart.Family;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import org.d3ifcool.smart.AccountActivity.ChangePassActivity;
import org.d3ifcool.smart.BottomNavigation.BottomNavigationViewHelper;
import org.d3ifcool.smart.Home.FragmentUser;
import org.d3ifcool.smart.Home.Fragment_List_Door;
import org.d3ifcool.smart.Home.MainActivity;
import org.d3ifcool.smart.R;
import org.d3ifcool.smart.BottomNavigation.SectionsPageAdapter;
import org.d3ifcool.smart.Setting.SettingActivity;

public class FamilyActivity extends AppCompatActivity {
    private static final String TAG = "Family";
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    ImageView menu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family);
        setStatustBarColor(R.color.colorWhite);

        loadFragmentValue();
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
//        mViewPager = (ViewPager) findViewById(R.id.container);
//        mViewPager.setPageTransformer(true, new ZoomAnimation());
//        setupViewPager(mViewPager);
//
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(mViewPager);


//        tabLayout.getTabAt(0).setText("User");
//        tabLayout.getTabAt(1).setText("Profile");


        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        Intent intent1 = new Intent(FamilyActivity.this, MainActivity.class);
                        startActivity(intent1);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

//                    case R.id.nav_activity:
//                        Intent intent2 = new Intent(FamilyActivity.this, ActivityFeature.class);
//                        startActivity(intent2);
//                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                        break;

                    case R.id.nav_user:
                        break;

                    case R.id.nav_setting :
                        Intent intent3 = new Intent(FamilyActivity.this, SettingActivity.class);
                        startActivity(intent3);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                }

                return false;
            }
        });




    }

//    private void setupViewPager(ViewPager viewPager) {
//        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
////        adapter.addFragment(new FragmentUser());
//        adapter.addFragment(new FragmentProfile());
//        viewPager.setAdapter(adapter);
//
//
//    }


    private void loadFragmentValue() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frameContainer, new FragmentProfile())
                .commit();

        Fragment newFragment = new FragmentProfile();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameContainer, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }


    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.value_password:
                Intent intent = new Intent(FamilyActivity.this, ChangePassActivity.class);
                startActivity(intent);
                break;


        }
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

    @Override
    public void onBackPressed() {
        Intent out = new Intent(Intent.ACTION_MAIN);
        out.addCategory(Intent.CATEGORY_HOME);
        out.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(out);

    }
}
