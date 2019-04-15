package org.d3ifcool.smart.Home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import org.d3ifcool.smart.Activity.FragmentHistory;
import org.d3ifcool.smart.BottomNavigation.SectionsPageAdapter;
import org.d3ifcool.smart.Family.ZoomAnimation;
import org.d3ifcool.smart.R;

public class HousesDetail extends AppCompatActivity implements  View.OnClickListener {
    private static final String TAG = "HousesDetail";
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    private TextView houseName_detail, deviceCodetxt;

    private void initializeWidgets() {

        houseName_detail = findViewById(R.id.houseName_key);

        deviceCodetxt = findViewById(R.id.deviceCode_key);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_houses);

        getWindow().setFlags(

                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,

                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN

        );

        initializeWidgets();

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);

        mViewPager.setPageTransformer(true, new ZoomAnimation());

        setupViewPager(mViewPager);

        Intent i = this.getIntent();
        String name =i.getExtras().getString("NAME_KEY");
        String deviceCode =i.getExtras().getString("DEVICECODE_KEY");

        houseName_detail.setText(name);

        deviceCodetxt.setText(deviceCode);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsHouses);

        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setText("Door");

        tabLayout.getTabAt(1).setText("Member");

        tabLayout.getTabAt(2).setText("History");

    }

    @Override
    public void onClick(View v) {

    }

    public void onClickView(View view){

        switch (view.getId()){

        }

    }

    private void setupViewPager(ViewPager viewPager) {

        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());

        adapter.addFragment(new Fragment_List_Door());

        adapter.addFragment(new FragmentUser());

        adapter.addFragment(new FragmentHistory());

        viewPager.setAdapter(adapter);

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

        startActivity(new Intent(HousesDetail.this, MainActivity.class));

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

}
