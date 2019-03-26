package org.d3ifcool.smart.Home;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rbrooks.indefinitepagerindicator.IndefinitePagerIndicator;
import com.wang.avi.AVLoadingIndicatorView;

import org.d3ifcool.smart.Adapter.RecyclerAdapterHouse;
import org.d3ifcool.smart.BottomNavigation.BottomNavigationViewHelper;
import org.d3ifcool.smart.BottomNavigation.SectionsPageAdapter;
import org.d3ifcool.smart.Data;
import org.d3ifcool.smart.Family.FamilyActivity;
import org.d3ifcool.smart.Model.House;
import org.d3ifcool.smart.Model.User;
import org.d3ifcool.smart.Notification.MyFirebaseMessagingService;
import org.d3ifcool.smart.R;
import org.d3ifcool.smart.Setting.SettingActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hari.bounceview.BounceView;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RecyclerAdapterHouse.OnItemClickListener {

    private static final String TAG = "MainActivity";
    private SectionsPageAdapter mSectionsPageAdapter;
    private FirebaseDatabase database;
    private FirebaseStorage mStorage;
    private StorageReference Storage;
    private DatabaseReference mDatabaseRef;
    private RecyclerView recyclerView;
    private RecyclerAdapterHouse mAdapter;
    private ValueEventListener mDBListener;
    private List<House> mHouses;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    DatabaseReference reference, reference0, reference1;
    private AVLoadingIndicatorView mProgressBar;
    FrameLayout fader;

    String profileid;
    ProgressDialog pd;
    CardView addHome, loadning;
    Button commitHome;
    Dialog dialog, dialogJoin, dialogHouseName;
    ImageView closePoupUp, toolAddHome, allLock, action_stream;
    EditText  deviceCode, housnameEdittxt, houseNameEditTxt;
    TextView  emptyInMemer;
    IndefinitePagerIndicator indicator;


    public void openDetailActivity(String[] data) {
        Intent intent = new Intent(this, HousesDetail.class);
        intent.putExtra("NAME_KEY", data[0]);
        intent.putExtra("DEVICECODE_KEY", data[1]);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setStatustBarColor(R.color.colorWhite);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        );

        startService(new Intent(this, MyFirebaseMessagingService.class));
        SharedPreferences prefs = getSharedPreferences("PREFS", MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");

        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        database = FirebaseDatabase.getInstance();
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        Storage = FirebaseStorage.getInstance().getReference();

        //get method
        viewWidget();
        getHouse();

        Data.madeDateHouse = getDateToday();

        //set custom toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolhome);
        setSupportActionBar(toolbar);


        //set Custom bottomNavigationView
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);


        //Item BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        break;

                    case R.id.nav_user:
                        Intent intent2 = new Intent(MainActivity.this, FamilyActivity.class);
                        startActivity(intent2);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    case R.id.nav_setting:
                        Intent intent3 = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(intent3);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                }

                return false;
            }

        });


        //Checking internet
        if (!haveNetwork()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setIcon(R.drawable.ic_action_warning_yellow);
            dialog.setTitle("Internet Connection Alert");
            dialog.setMessage("Please Check Your Connection Internet");
            dialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    onBackPressed();
                }
            })
                    .show();
            addHome.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);

        } else {
            addHome.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);

        }

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

                    emptyInMemer.setVisibility(View.GONE);
                    toolAddHome.setVisibility(View.VISIBLE);

                }

                else {

                    emptyInMemer.setVisibility(View.VISIBLE);
                    addHome.setVisibility(View.GONE);
                    toolAddHome.setVisibility(View.GONE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }


    public void checkHouse() {

        if (mAdapter.getItemCount() !=  0){
            addHome.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            emptyInMemer.setVisibility(View.GONE);
            toolAddHome.setVisibility(View.VISIBLE);
            indicator.setVisibility(View.VISIBLE);

        }

        else {

            addHome.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            toolAddHome.setVisibility(View.GONE);
            indicator.setVisibility(View.GONE);
            checkAccount();

        }

    }


    public void getHouse(){
        mDatabaseRef = database.getInstance().getReference();
        mDatabaseRef.keepSynced(true);
        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mDatabaseRef == null) {
                    database = FirebaseDatabase.getInstance();
                    database.setPersistenceEnabled(true);
                    mDatabaseRef = database.getReference();
                }
                mHouses.clear();
                try {

                    for (DataSnapshot kodedeviceSnapshot : dataSnapshot.child("Users").child(firebaseUser.getEmail()
                            .replace(".", ",")).child("Houses").getChildren()) {
                        String kode_device = kodedeviceSnapshot.getValue(String.class);
                        Log.d(TAG, "onDataChange: " + kode_device);
                        House house = dataSnapshot.child("Devices").child("ListDevices").child(kode_device).getValue(House.class);
                        Log.d(TAG, "onDataChange: " + house.getName());
                        mHouses.add(house);

                    }
                }catch (Exception e){}

//                for (DataSnapshot houseSnapshot : dataSnapshot.getChildren()) {
//                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
//                    Map<String,Object> map =  houseSnapshot.getValue(genericTypeIndicator);
//
//                    House upload = new House();
//                    upload.setName( (String) map.get("name"));
//                    mHouses.add(upload);
//                    Data.checkRecyler = upload;

                mAdapter.notifyDataSetChanged();
//                loadning.setVisibility(View.INVISIBLE);
                stopLoadingAnimation();

                checkHouse();
                checkAccount();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                stopLoadingAnimation();            }

        });

    }


    private String getDateToday(){
        DateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String today= dateFormat.format(date);
        return today;
    }



    public boolean haveNetwork() {
        boolean have_WIFI = false;
        boolean have_MOBILE = false;

        ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();

        for (NetworkInfo info:networkInfos){
            if (info.getTypeName().equalsIgnoreCase("WIFI"));
            if (info.isConnected())
                have_WIFI = true;
            if (info.getTypeName().equalsIgnoreCase("MOBILE"));
            if (info.isConnected())
                have_MOBILE = true;

        }

        return have_WIFI || have_MOBILE;

    }


    @Override
    public void onClick(View v) {

    }


    //all onclick
    public void onClickView(View view) {
        switch (view.getId()) {

            case R.id.action_add_home :
                showDialogAddHome();
                break;

            case R.id.stream_action :
                startActivity(new Intent(MainActivity.this, StreamingActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;

            case R.id.cards_add_home :
                showDialogAddHome();
                break;

        }

    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }


    @Override
    public void onItemClick(int position) {
        House clickedHouse= mHouses.get(position);
        String[] houseData={clickedHouse.getName(), clickedHouse.getDeviceCode()};
        openDetailActivity(houseData);

    }


    @Override
    public void onShowItemClick(int position) {
        House clickedHouse = mHouses.get(position);
        String[] houseData={clickedHouse.getName(), clickedHouse.getDeviceCode()};
        openDetailActivity(houseData);

    }


    @Override
    public void onDeleteItemClick(int position) {
        House selectedItem = mHouses.get(position);
        final String selectedKey = selectedItem.getDeviceCode();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("DELETE DEVICE");
        builder.setMessage("are you sure to delete this device ?");
        builder.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        reference = FirebaseDatabase.getInstance().getReference().child("Devices").child("ListDevices");
                        reference0 = FirebaseDatabase.getInstance().getReference().child("Devices").child(selectedKey);
                        reference1 = FirebaseDatabase.getInstance().getReference().child("Devices").child(selectedKey);
                        reference.child(selectedKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                reference0.child("Member").removeValue();
                                reference1.child("ListDoor").removeValue();
                                Toast.makeText(MainActivity.this, "Item deleted " + selectedKey, Toast.LENGTH_SHORT).show();
                            }

                        });

                    }
                });

        builder.show();

    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    @SuppressLint("ResourceAsColor")
    private void setStatustBarColor(@ColorRes int statustBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            int color = ContextCompat.getColor(this, statustBarColor);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
            window.setTitleColor(R.color.black);
        }
    }


    //Show dialog addhome
    private void showDialogAddHome() {
        dialog.setContentView(R.layout.add_home_popup);
        dialog.setCanceledOnTouchOutside(false);
        closePoupUp = (ImageView) dialog.findViewById(R.id.close_popup_home);
        commitHome = (Button) dialog.findViewById(R.id.add_home_code);
        houseNameEditTxt = (EditText) dialog.findViewById(R.id.house_name);
        deviceCode = (EditText) dialog.findViewById(R.id.code_device);
        commitHome.setOnClickListener(this);
        BounceView.addAnimTo(dialog);


        closePoupUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        commitHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(MainActivity.this);
                pd.setMessage("Please wait...");
                pd.show();

                final String str_house = houseNameEditTxt.getText().toString();
                final String str_device = deviceCode.getText().toString();

                String made_date = Data.madeDateHouse;
                final House house = new House(str_house, str_device);

                if (TextUtils.isEmpty(str_house)){
                    houseNameEditTxt.setError("House name required");
                }

                else if (TextUtils.isEmpty(str_device)){
                    deviceCode.setError("Device code required");

                }

                reference = FirebaseDatabase.getInstance().getReference().child("Devices").child(str_device);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        reference0 = FirebaseDatabase.getInstance().getReference().child("Users").
                                child(firebaseUser.getEmail().replace(".", ",")).child("Houses");
                        reference1 = FirebaseDatabase.getInstance().getReference().child("Devices").child("ListDevices").child(str_device);

                        if (dataSnapshot.exists()) {
                            House getHouses = dataSnapshot.getValue(House.class);

                            String uploadId = reference.push().getKey();
//                            reference.setValue(house);
                            reference0.child(uploadId).setValue(str_device);
                            reference1.setValue(house);

                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, str_house + " added", Toast.LENGTH_SHORT).show();

                            pd.hide();
                            return;

                        }

                        else {

                            pd.hide();
                            deviceCode.setError(str_device + " Not found");

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

//
//                    reference.child("madeDate").setValue(made_date);
//
//                    dialog.dismiss();
//                pd.hide();

            }
        });

    }


    public void viewWidget(){
        //find object with id
        toolAddHome =  findViewById(R.id.action_add_home);
        allLock =  findViewById(R.id.all_lockHouse);
        action_stream =  findViewById(R.id.stream_action);
        closePoupUp =  findViewById(R.id.close_popup_home);
        addHome =  findViewById(R.id.cards_add_home);
        emptyInMemer =  findViewById(R.id.empty_device);
        commitHome =  findViewById(R.id.add_home_code);
        housnameEdittxt = findViewById(R.id.house_name);
        deviceCode = findViewById(R.id.code_device);
        mProgressBar = findViewById(R.id.myDataLoaderProgressBar);
        fader = findViewById(R.id.fader);
        indicator = findViewById(R.id.recyclerview_indicator);
//        loadning = findViewById(R.id.card_loading);
//        loadning.setVisibility(View.VISIBLE);
        setLoadingAnimation();



        dialog = new Dialog(this);
        dialogJoin = new Dialog(this);
        dialogHouseName = new Dialog(this);

        recyclerView = findViewById(R.id.mRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        SnapHelper snapHelper = new GravitySnapHelper(Gravity.START);
        new LinearSnapHelper().attachToRecyclerView(recyclerView);
//        snapHelper.attachToRecyclerView(recyclerView);
        indicator.attachToRecyclerView(recyclerView);
        mHouses = new ArrayList<> ();
        mAdapter = new RecyclerAdapterHouse (MainActivity.this, mHouses);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(MainActivity.this);

    }

    private void setLoadingAnimation() {
        fader.setVisibility(View.VISIBLE);
        mProgressBar.show();
    }

    private void stopLoadingAnimation(){
        fader.setVisibility(View.GONE);
        mProgressBar.hide();
    }


    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }

}
