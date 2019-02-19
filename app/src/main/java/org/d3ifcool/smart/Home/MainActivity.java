package org.d3ifcool.smart.Home;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.markushi.ui.CircleButton;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RecyclerAdapterHouse.OnItemClickListener {

    private static final String TAG = "MainActivity";
    private SectionsPageAdapter mSectionsPageAdapter;
    private FirebaseDatabase database;
    private FirebaseStorage mStorage;
    private StorageReference Storage;
    private DatabaseReference mDatabaseRef, mDatabaseDoor;
    private RecyclerView recyclerView;
    private RecyclerAdapterHouse mAdapter;
    private ValueEventListener mDBListener;
    private List<House> mHouses;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    private ProgressBar uploadProgressBar;
    private View view;
    private MediaRecorder mRecorder;
    private ProgressBar mProgressBar;
    private StorageTask mUploadTask;
    private List<User> mUser;
    private boolean isLock;

    String profileid;
    ProgressDialog pd;
    Context context;
    CardView addHome;
    RecordView recordView;
    RecordButton recordButton;
    Button commitHome, commitInvitation, editHouseName;
    Dialog dialog, dialogJoin, dialogHouseName, dialogUser;
    ImageView closePoupUp, closePoupUpJoin, toolAddHome, allLock, closeEditHouseName, action_stream;
    CircleButton streaming;
    EditText invitation, deviceCode, housnameEdittxt, houseNameEditTxt;
    TextView  emptyInMemer;
    Context mContext;
    int status;
    String[] data;

    private static String mFileName = null;
    private static final String LOG_TAG = "AudioRecordTest";
    private int REQUEST_ID_MULTIPLE_PERMISSIONS;

    public void openDetailActivity(String[] data) {
        Intent intent = new Intent(this, HousesDetail.class);
        intent.putExtra("NAME_KEY", data[0]);
        intent.putExtra("DEVICECODE_KEY", data[1]);
//        intent.putExtra("IMAGE_KEY", data[2]);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getUsername();

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

        mDatabaseDoor = FirebaseDatabase.getInstance().getReference("Door").child(firebaseUser.getUid());

        //get method
        viewWidget();
        getHouse();
        houseInfo();


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


    public void allUnlock(){
        reference = FirebaseDatabase.getInstance().getReference().child("Devices");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot houseSnapshot : dataSnapshot.getChildren()) {
                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {
                    };
                    Map<String, Object> map = houseSnapshot.getValue(genericTypeIndicator);

                    House Lock = houseSnapshot.getValue(House.class);
                    boolean checkLock = Lock.isHouse_lock();

                    if (checkLock == true){

//                      allLock.setImageResource(R.drawable.lock);
                        Toast.makeText(MainActivity.this, "Lock" + checkLock, Toast.LENGTH_SHORT).show();

                    }

                    else {

//                      allLock.setImageResource(R.drawable.unlock);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    //check account login
    private void checkAccount() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).
                child("Email_" + firebaseUser.getEmail().replace(".",","));
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

        }

        else {
            addHome.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

        }

    }


    public void getHouse(){
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mHouses.clear();
                for(DataSnapshot kodedeviceSnapshot : dataSnapshot.child("Users").child(firebaseUser.getUid()).child("Email_"+firebaseUser.getEmail().replace(".", ","))
                        .child("Houses").getChildren()){
                    String kode_device = kodedeviceSnapshot.getValue(String.class);
                    Log.d(TAG, "onDataChange: " + kode_device);
                    House house = dataSnapshot.child("Devices").child(kode_device).getValue(House.class);
                    Log.d(TAG, "onDataChange: " + house.getName());
                    mHouses.add(house);


//                for (DataSnapshot houseSnapshot : dataSnapshot.getChildren()) {
//                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
//                    Map<String,Object> map =  houseSnapshot.getValue(genericTypeIndicator);
//
//                    House upload = new House();
//                    upload.setName( (String) map.get("name"));
//                    mHouses.add(upload);
//                    Data.checkRecyler = upload;

                }

                mAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.GONE);

                checkAccount();
                checkHouse();
                allUnlock();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.INVISIBLE);
            }

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


    //house information
    private void houseInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getContext() == null) {
                    return;
                }
                User user = dataSnapshot.getValue(User.class);

//                house_Name.setText(user.getHouseName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }


    //Edit house name
    public void insertHouseName(String houseName){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());
        HashMap<String, Object> map = new HashMap<>();
        map.put("house_Name", houseName);

        reference.updateChildren(map);

        Toast.makeText(MainActivity.this, "Successfully updated!", Toast.LENGTH_SHORT).show();
        pd.hide();

    }


    public void obserVationHouse(){
        reference = FirebaseDatabase.getInstance().getReference().child("Device").child(firebaseUser.getUid()).child(Data.user).
                child("house_" + Data.user);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()) {
                    if (data.child("house" + Data.housenameid).exists()) {
                        //do ur stuff
                    } else {
                        //do something if not exists
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onClick(View v) {

    }


    //all onclick
    public void onClickView(View view) {
        switch (view.getId()) {

            case R.id.tool_add_home :
                showDialogAddHome();
                break;

            case R.id.stream_action :
                startActivity(new Intent(MainActivity.this, StreamingActivity.class));
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
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Devices").child(firebaseUser.getUid()).child(Data.user);
        House selectedItem = mHouses.get(position);
        final String selectedKey = selectedItem.getName();
        Data.housenameid = selectedKey;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("house_" + Data.housenameid);
        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mDatabaseRef.child("house_" + Data.housenameid).removeValue();
                Toast.makeText(MainActivity.this, "Item deleted" + selectedKey, Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    //Show dialog addhome
    private void showDialogAddHome() {
        dialog.setContentView(R.layout.add_home_popup);
        closePoupUp = (ImageView) dialog.findViewById(R.id.close_popup_home);
        commitHome = (Button) dialog.findViewById(R.id.add_home_code);
        houseNameEditTxt = (EditText) dialog.findViewById(R.id.house_name);
        deviceCode = (EditText) dialog.findViewById(R.id.code_device);
        commitHome.setOnClickListener(this);

//        houseNameEditTxt.addTextChangedListener(textWatcheradd);
//        deviceCode.addTextChangedListener(textWatcheradd);
//        homeAddress.addTextChangedListener(textWatcheradd);


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
                String str_house = houseNameEditTxt.getText().toString();
                String str_device = deviceCode.getText().toString();
                reference = FirebaseDatabase.getInstance().getReference().child("Devices").child(str_device);
                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).
                        child("Email_" + firebaseUser.getEmail().replace(".", ",")).child("Houses");

                String made_date = Data.madeDateHouse;
//                House house1 = new House(houseNameEditTxt, deviceCode.getText().toString().trim());
                House house = new House(str_house, str_device);

                if (TextUtils.isEmpty(str_house)){
                    houseNameEditTxt.setError("House name required");
                }

                else if (TextUtils.isEmpty(str_device)){
                    deviceCode.setError("Device code required");

                }

                else {

                    String uploadId = reference.push().getKey();
                    reference.setValue(house);
                    reference1.child(uploadId).setValue(str_device);
                    reference.child("madeDate").setValue(made_date);
                    Data.keyhouse = uploadId;
                    Data.housenameid = str_house;
                    Data.deviceCode = str_device;
                    Toast.makeText(MainActivity.this, str_house + " added", Toast.LENGTH_SHORT).show();

                    dialog.dismiss();

                }

                pd.hide();

            }
        });

    }


    //Textwatcher addhome
    private TextWatcher textWatcheradd = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String houseNameInput = housnameEdittxt.getText().toString().trim();

            commitHome.setEnabled(!houseNameInput.isEmpty());

        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    };


    //Textwatcher invite user
    private TextWatcher textWatcherjoin = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String invitationInput = invitation.getText().toString().trim();

            commitInvitation.setEnabled(!invitationInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    };


    public void viewWidget(){
        //find object with id
        toolAddHome = (ImageView) findViewById(R.id.tool_add_home);
        allLock = (ImageView) findViewById(R.id.all_lockHouse);
        action_stream = (ImageView) findViewById(R.id.stream_action);
        closePoupUp = (ImageView) findViewById(R.id.close_popup_home);
        addHome = (CardView) findViewById(R.id.cards_add_home);
        emptyInMemer = (TextView) findViewById(R.id.empty_device);
        commitHome = (Button) findViewById(R.id.add_home_code);
        housnameEdittxt = (EditText) findViewById(R.id.house_name);
        deviceCode = (EditText) findViewById(R.id.code_device);
        mProgressBar = findViewById(R.id.myDataLoaderProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        dialog = new Dialog(this);
        dialogJoin = new Dialog(this);
        dialogHouseName = new Dialog(this);

        recyclerView = findViewById(R.id.mRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mHouses = new ArrayList<> ();
        mAdapter = new RecyclerAdapterHouse (MainActivity.this, mHouses);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(MainActivity.this);

    }


    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }

}
