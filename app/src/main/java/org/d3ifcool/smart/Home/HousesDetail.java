package org.d3ifcool.smart.Home;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.d3ifcool.smart.BottomNavigation.SectionsPageAdapter;
import org.d3ifcool.smart.Family.ZoomAnimation;
import org.d3ifcool.smart.Model.House;
import org.d3ifcool.smart.R;

public class HousesDetail extends AppCompatActivity implements  View.OnClickListener {
    private static final String TAG = "HousesDetail";
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    DatabaseReference reference;

    ImageView lock;
    TextView houseName_detail, deviceCodetxt;
    boolean doorLock;

    private void initializeWidgets() {
        houseName_detail = findViewById(R.id.houseName_key);
        deviceCodetxt = findViewById(R.id.deviceCode_key);
        lock = (ImageView) findViewById(R.id.lockDoor);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_houses);

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
}
