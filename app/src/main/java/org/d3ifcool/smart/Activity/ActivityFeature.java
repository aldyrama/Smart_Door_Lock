package org.d3ifcool.smart.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import org.d3ifcool.smart.Adapter.RecyclerViewAdapterHistory;
import org.d3ifcool.smart.BottomNavigation.BottomNavigationHelperHouse;
import org.d3ifcool.smart.BottomNavigation.BottomNavigationViewHelper;
import org.d3ifcool.smart.Data;
import org.d3ifcool.smart.Family.FamilyActivity;
import org.d3ifcool.smart.Home.HouseDetail;
import org.d3ifcool.smart.Home.MainActivity;
import org.d3ifcool.smart.Model.History;
import org.d3ifcool.smart.Model.House;
import org.d3ifcool.smart.R;
import org.d3ifcool.smart.BottomNavigation.SectionsPageAdapter;
import org.d3ifcool.smart.Setting.SettingActivity;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActivityFeature extends AppCompatActivity implements View.OnClickListener, RecyclerViewAdapterHistory.OnItemClickListener {
    private static final String TAG = "Activity";
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;

    private RecyclerView mRecyclerViewHistory;
    private RecyclerViewAdapterHistory mAdapterHistory;
    private ProgressBar mProgressBarHistory;
    private FirebaseStorage mStorageHistory;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private List<History> mHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature);
        setStatustBarColor(R.color.colorWhite);
        viewWidget();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("History");

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        //set Custom bottomNavigationView
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar_house);
        BottomNavigationHelperHouse.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);


        //Item BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
//                    case R.id.nav_door:
//                        Intent intent2 = new Intent(ActivityFeature.this, HouseDetail.class);
//                        startActivity(intent2);
//                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                        break;

                    case R.id.nav_member:
                        Intent intent1 = new Intent(ActivityFeature.this, ActivityFeature.class);
                        startActivity(intent1);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    case R.id.nav_activity_house:
                        break;


                }

                return false;
            }

        });


        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mHistory.clear();

                for (DataSnapshot historySnapshot : dataSnapshot.getChildren()) {
                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
//                    Map<String,Object> map =  connectSnapshot.getValue(genericTypeIndicator);
                    Map<String, Object> map = (Map<String, Object>) historySnapshot.getValue();


                    History upload = new History();
                    upload.setUsernamse( (String) map.get("name"));
                    mHistory.add(upload);
                }
                mAdapterHistory.notifyDataSetChanged();
                mProgressBarHistory.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ActivityFeature.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressBarHistory.setVisibility(View.INVISIBLE);
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

    @Override
    public void onClick(View v) {

    }



    public void viewWidget(){
        mRecyclerViewHistory = findViewById(R.id.recycler_view_history);
        mRecyclerViewHistory.setHasFixedSize(true);
        mRecyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));

        mProgressBarHistory = findViewById(R.id.myDataLoaderProgressBarHistory);
        mProgressBarHistory.setVisibility(View.VISIBLE);

        mHistory = new ArrayList<>();
        mAdapterHistory = new RecyclerViewAdapterHistory (ActivityFeature.this, mHistory);
        mRecyclerViewHistory.setAdapter(mAdapterHistory);
        mAdapterHistory.setOnItemClickListener(ActivityFeature.this);
    }


    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onShowItemClick(int position) {

    }

    @Override
    public void onDeleteItemClick(int position) {

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
