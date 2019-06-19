package org.d3ifcool.smart.Home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import org.d3ifcool.smart.QrCode.Qr;
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

    private static final String TAG = "OuutputCamera";
    private SectionsPageAdapter mSectionsPageAdapter;
    private FirebaseDatabase database;
    private FirebaseStorage mStorage;
    private StorageReference Storage;
    private DatabaseReference mDatabaseRef;
    private RecyclerView recyclerView;
    private RecyclerAdapterHouse mAdapter;
    private ValueEventListener mDBListener;
    private List<House> mHouses;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference, reference0, reference1, reference2;
    private AVLoadingIndicatorView mProgressBar;
    private View fader;
    private int counterInput = 0;
    private int countDown = 30;
    private String profileid;
    private ProgressDialog pd;
    private LinearLayout emptyDevice;
    private FloatingActionButton addHome;
    private Button commitHome, sure, cancel;
    private Dialog dialog, dialogAdd;
    private ImageView closePoupUp, toolAddHome,
            closePouUpOption, qrOption, inputOption;
    private EditText  deviceCode, houseNameEditTxt;
    private TextView  emptyInMemer, count, title, textEmpty, textConnection, retry;
    private IndefinitePagerIndicator indicator;
    private RelativeLayout rl;


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

//        getWindow().setFlags(
//
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//
//                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
//
//        );

        startService(new Intent(this, MyFirebaseMessagingService.class));

        SharedPreferences prefs = getSharedPreferences("PREFS", MODE_PRIVATE);

        profileid = prefs.getString("profileid", "none");

        auth = FirebaseAuth.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mStorage = FirebaseStorage.getInstance();

        database = FirebaseDatabase.getInstance();

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        Storage = FirebaseStorage.getInstance().getReference();

        pd = new ProgressDialog(this, R.style.MyAlertDialogStyle);


        //get method
        viewWidget();

        getHouse();

//        checkAccount();

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
        if (!isOnline()) {

            rl.setVisibility(View.VISIBLE);

            textConnection.setVisibility(View.VISIBLE);


        }

        else {

            rl.setVisibility(View.GONE);

            textConnection.setVisibility(View.GONE);

        }

//
//        Intent intent = new Intent();
//
//        String manufacturer = android.os.Build.MANUFACTURER;
//
//        switch (manufacturer) {
//
//            case "xiaomi":
//                intent.setComponent(new ComponentName("com.miui.securitycenter",
//                        "com.miui.permcenter.autostart.AutoStartManagementActivity"));
//                break;
//            case "oppo":
//                intent.setComponent(new ComponentName("com.coloros.safecenter",
//                        "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
//
//                break;
//            case "vivo":
//                intent.setComponent(new ComponentName("com.vivo.permissionmanager",
//                        "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
//                break;
//        }
//
//        List<ResolveInfo> arrayList =  getPackageManager().queryIntentActivities(intent,
//                PackageManager.MATCH_DEFAULT_ONLY);
//
//        if (arrayList.size() > 0) {
//            startActivity(intent);
//        }

    }

    //check account login
    private void checkAccount() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getEmail()
                .replace(".",","));
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (getContext() == null){

                    return;

                }

                User user = dataSnapshot.getValue(User.class);

                String check = user.getTypeAccount();

                if (check.equals("Owner")) {

                    addHome.setEnabled(true);

                    addHome.setVisibility(View.VISIBLE);

                    textEmpty.setText("ADD DEVICE");

                    toolAddHome.setVisibility(View.VISIBLE);

                    recyclerView.setEnabled(true);

                }

                else {

                    addHome.setEnabled(false);

                    addHome.setVisibility(View.GONE);

                    textEmpty.setText("NO DEVICE");

                    toolAddHome.setVisibility(View.GONE);

                    recyclerView.setEnabled(false);

                }

                checkHouse();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

    @SuppressLint("RestrictedApi")
    public void checkHouse() {

        if (mAdapter.getItemCount() !=  0){

            recyclerView.setVisibility(View.VISIBLE);

            toolAddHome.setVisibility(View.VISIBLE);

            emptyDevice.setVisibility(View.GONE);

        }

        else {

            emptyDevice.setVisibility(View.VISIBLE);

            recyclerView.setVisibility(View.GONE);

            toolAddHome.setVisibility(View.GONE);


        }

    }

    public void getHouse(){
        setLoadingAnimation();
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

                mAdapter.notifyDataSetChanged();

                fader.setVisibility(View.GONE);

                stopLoadingAnimation();

//                checkHouse();
//
                checkAccount();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                stopLoadingAnimation();

            }

        });

    }

    private String getDateToday(){

        DateFormat dateFormat=new SimpleDateFormat("dd/M/yyyy h:mm");

        Date date = new Date();

        String today= dateFormat.format(date);

        return today;

    }

    private boolean isOnline() {

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();

    }

    @Override
    public void onClick(View v) {

    }


    //all onclick
    public void onClickView(View view) {

        switch (view.getId()) {

            case R.id.action_add_home :

                showDialodAdd();

                break;

            case R.id.doorDetail :

                break;

            case R.id.stream_action :

                break;

            case R.id.cards_add_home :

                showDialodAdd();

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

        final String selectEmail = firebaseUser.getEmail().replace(".", ",");

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        final View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.allert_dialog_delete, null);

        title = view.findViewById(R.id.txt_alert_delete);

        title.setText("Are you sure to delete this device?");

        sure = view.findViewById(R.id.btn_sure_delete);

        cancel = view.findViewById(R.id.btn_cancel_delete);

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(selectEmail).child("Houses");
//                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                                for(DataSnapshot data : dataSnapshot.getChildren()) {
//
//                                    if(data.getValue(String.class).equals(deviceCode)){
//
//                                        ref.child(data.getKey()).removeValue();
//
//                                        Log.d("key", "data " + data);
//
//                                    }
//
//                                }
                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(selectEmail).child("Houses");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                        for(DataSnapshot data : dataSnapshot.getChildren()) {

                            if(data.getValue(String.class).equals(deviceCode)){

                                ref.child(data.getKey()).removeValue();

                            }

                        }

                                reference = FirebaseDatabase.getInstance().getReference().child("Devices").child("ListDevices");

                                reference0 = FirebaseDatabase.getInstance().getReference().child("Devices").child(selectedKey);

                                reference1 = FirebaseDatabase.getInstance().getReference().child("Devices").child(selectedKey);

                                reference.child(selectedKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        reference0.child("Member").getRef().removeValue();

                                        reference1.child("ListDoor").getRef().removeValue();

                                        Toast.makeText(MainActivity.this, "Item deleted " + selectedKey, Toast.LENGTH_SHORT).show();

                                        dialog.dismiss();

                                        startActivity(new Intent(MainActivity.this, MainActivity.class));

                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


                                    }

                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }

                        });

            }


        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


            }
        });
        builder.setView(view);
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

    private void showDialodAdd() {

        dialogAdd.setContentView(R.layout.dialog_add);

        dialogAdd.setCanceledOnTouchOutside(false);

        closePouUpOption = dialogAdd.findViewById(R.id.close_popup_add);

        qrOption = dialogAdd.findViewById(R.id.qr);

        inputOption = dialogAdd.findViewById(R.id.input);

        BounceView.addAnimTo(dialogAdd);

        closePouUpOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogAdd.dismiss();

            }

        });

        qrOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogAdd.dismiss();

                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                startActivity(new Intent(MainActivity.this, Qr.class));

            }

        });

        inputOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialogAddHome();

                dialogAdd.dismiss();

                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }

        });

        dialogAdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogAdd.show();

    }

    //Show dialog addhome
    private void showDialogAddHome() {

        dialog.setContentView(R.layout.add_home_popup);

        dialog.setCanceledOnTouchOutside(false);

        closePoupUp = dialog.findViewById(R.id.close_popup_home);

        commitHome = dialog.findViewById(R.id.add_home_code);

        houseNameEditTxt = dialog.findViewById(R.id.house_name);

        count = dialog.findViewById(R.id.countDownTime);

        deviceCode = dialog.findViewById(R.id.code_device);

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

                pd.setMessage("Please wait...");

                pd.show();

                final String str_house = houseNameEditTxt.getText().toString();

                final String str_device = deviceCode.getText().toString();

                final House house = new House(str_house, str_device);

                if (TextUtils.isEmpty(str_house)) {

                    counterInput = 0;

                    houseNameEditTxt.setError("House name required");

                    pd.hide();

                }

                if (TextUtils.isEmpty(str_device)) {

                    counterInput = 0;

                    deviceCode.setError("Device code required");

                    pd.hide();

                } else {

                    reference = FirebaseDatabase.getInstance().getReference().child("Devices").child(str_device);

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot deviceSnapshot) {

                            House getHouses = deviceSnapshot.getValue(House.class);

                            reference1 = FirebaseDatabase.getInstance().getReference().child("Devices").child("ListDevices").child(str_device);
                            reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot listSnapshot) {

                                    reference0 = FirebaseDatabase.getInstance().getReference().child("Users").
                                            child(firebaseUser.getEmail().replace(".", ",")).child("Houses");

                                    reference2 = FirebaseDatabase.getInstance().getReference().child("Devices").child(str_device);

                                    if (listSnapshot.exists() && deviceSnapshot.exists()){

                                        counterInput = 0;

                                        deviceCode.setError("Device " + str_device + " Already available");

                                        pd.hide();

                                    }

                                     else if (!listSnapshot.exists() && deviceSnapshot.exists()) {

                                        counterInput = 0;

                                        String uploadId = reference.push().getKey();

                                        reference0.child(str_device).setValue(str_device);

                                        reference1.setValue(house);

                                        reference2.child("name").setValue(str_house);

                                        dialog.dismiss();

                                        dialogAdd.dismiss();

                                        Toast.makeText(MainActivity.this, str_house + " added", Toast.LENGTH_SHORT).show();

                                        pd.hide();

                                    }

                                    else {

                                        counterInput++;

                                        Log.d("jumlah", "input" + counterInput);

                                        deviceCode.setError("Device " + str_device + " Not found");

                                        pd.hide();

                                        if (counterInput == 7) {

                                            new CountDownTimer(30000, 1000) {

                                                @Override
                                                public void onTick(long millisUntilFinished) {

                                                    commitHome.setEnabled(false);

                                                    closePoupUp.setEnabled(false);

                                                    houseNameEditTxt.setEnabled(false);

                                                    deviceCode.setEnabled(false);

                                                    count.setVisibility(View.VISIBLE);

                                                    count.setText("Try again in " + countDown + " seconds");

                                                    countDown--;

                                                }

                                                @Override
                                                public void onFinish() {

                                                    countDown = 30;

                                                    counterInput = 0;

                                                    closePoupUp.setEnabled(true);

                                                    commitHome.setEnabled(true);

                                                    houseNameEditTxt.setEnabled(true);

                                                    deviceCode.setEnabled(true);

                                                    count.setVisibility(View.GONE);

                                                }

                                            }.start();

                                        }

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }

                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });

                }

            }

        });

    }

    public void viewWidget(){

        //find object with id
        toolAddHome =  findViewById(R.id.action_add_home);

        closePoupUp =  findViewById(R.id.close_popup_home);

        addHome =  findViewById(R.id.cards_add_home);

        emptyDevice = findViewById(R.id.empty);

        textEmpty = findViewById(R.id.txt_empty);

        count = findViewById(R.id.countDownTime);

        textConnection = findViewById(R.id.text_status);

        commitHome =  findViewById(R.id.add_home_code);

        deviceCode = findViewById(R.id.code_device);

        mProgressBar = findViewById(R.id.myDataLoaderProgressBar);

        fader = findViewById(R.id.fader);

        fader.setVisibility(View.VISIBLE);

        rl = findViewById(R.id.internet_status);

        retry = findViewById(R.id.txt_retry);

        setLoadingAnimation();

        dialog = new Dialog(this);

        dialogAdd = new Dialog(this);

        recyclerView = findViewById(R.id.mRecyclerView);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        new LinearSnapHelper().attachToRecyclerView(recyclerView);

//        indicator.attachToRecyclerView(recyclerView);

        mHouses = new ArrayList<> ();

        mAdapter = new RecyclerAdapterHouse (MainActivity.this, mHouses);

        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(MainActivity.this);

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });

    }

    @SuppressLint("RestrictedApi")
    private void setLoadingAnimation() {

        mProgressBar.show();

        addHome.setVisibility(View.GONE);

        addHome.setEnabled(false);

    }

    @SuppressLint("RestrictedApi")
    private void stopLoadingAnimation(){

        mProgressBar.hide();

        addHome.setVisibility(View.VISIBLE);

        addHome.setEnabled(true);

    }


    protected void onDestroy() {

        super.onDestroy();

        mDatabaseRef.removeEventListener(mDBListener);

    }

}
