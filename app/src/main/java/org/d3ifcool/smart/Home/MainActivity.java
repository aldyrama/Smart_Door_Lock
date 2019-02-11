package org.d3ifcool.smart.Home;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

import org.d3ifcool.smart.Activity.ActivityFeature;
import org.d3ifcool.smart.BottomNavigation.BottomNavigationViewHelper;
import org.d3ifcool.smart.BottomNavigation.SectionsPageAdapter;
import org.d3ifcool.smart.Data;
import org.d3ifcool.smart.Family.FamilyActivity;
import org.d3ifcool.smart.Model.Door;
import org.d3ifcool.smart.Model.House;
import org.d3ifcool.smart.Model.User;
import org.d3ifcool.smart.Notification.MyFirebaseMessagingService;
import org.d3ifcool.smart.R;
import org.d3ifcool.smart.Setting.SettingActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.markushi.ui.CircleButton;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RecyclerAdapterHouse.OnItemClickListener {

    private static final String TAG = "MainActivity";
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    private FirebaseDatabase database;
    private FirebaseStorage mStorage;
    private StorageReference Storage;
    DatabaseReference Ref;
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



    String profileid;
    ProgressDialog pd;
    Context context;
    CardView addHome;
    RecordView recordView;
    RecordButton recordButton;
    Button commitHome, commitInvitation, editHouseName;
    Dialog dialog, dialogJoin, dialogHouseName;
    ImageView closePoupUp, closePoupUpJoin, toolAddHome, lock, closeEditHouseName, penEditTxt, action_stream;
    CircleButton streaming;
    EditText invitation, homeAddress, housnameEdittxt, houseNameEditTxt;
    TextView joinHome, house_Name;
    Context mContext;
    int status;

    private static String mFileName = null;
    private static final String LOG_TAG = "AudioRecordTest";
    private int REQUEST_ID_MULTIPLE_PERMISSIONS;

    private void openDetailActivity(String[] data) {
        Intent intent = new Intent(this, HouseDetail.class);
        intent.putExtra("NAME_KEY", data[0]);
//        intent.putExtra("DESCRIPTION_KEY", data[1]);
//        intent.putExtra("IMAGE_KEY", data[2]);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getUsername();

        startService(new Intent(this, MyFirebaseMessagingService.class));

        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs = getSharedPreferences("PREFS", MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");

        database = FirebaseDatabase.getInstance();
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        Storage = FirebaseStorage.getInstance().getReference();
//        String username = Data.user.getUsername();


        mDatabaseDoor = FirebaseDatabase.getInstance().getReference("Door").child(firebaseUser.getUid());


        dialog = new Dialog(this);
        dialogJoin = new Dialog(this);
        dialogHouseName = new Dialog(this);

        //find object with id
        //ImageView
        closePoupUpJoin = (ImageView) findViewById(R.id.close_popup_join);
        toolAddHome = (ImageView) findViewById(R.id.tool_add_home);
//        penEditTxt = (ImageView) findViewById(R.id.pen_edittxt_house);
//        lock = (ImageView) findViewById(R.id.lock_door);
        action_stream = (ImageView) findViewById(R.id.stream_action);
//        streaming = (CircleButton) findViewById(R.id.stream_cam);
        closePoupUp = (ImageView) findViewById(R.id.close_popup_home);
        //CardView
//        addHome = (CardView) findViewById(R.id.card_add_home);
        //TextView
//        house_Name = (TextView) findViewById(R.id.myhouse);
        //Button
        commitHome = (Button) findViewById(R.id.add_home_code);
        editHouseName = (Button) findViewById(R.id.btn_edit);
        commitInvitation = (Button) findViewById(R.id.add_invitation_code);
        //EditText
        invitation = (EditText) findViewById(R.id.invitation_code);
        housnameEdittxt = (EditText) findViewById(R.id.edit_house_nametxt);

        addHome = (CardView) findViewById(R.id.cards_add_home);


        recyclerView = findViewById(R.id.mRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mProgressBar = findViewById(R.id.myDataLoaderProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        mHouses = new ArrayList<> ();
        mAdapter = new RecyclerAdapterHouse (MainActivity.this, mHouses);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(MainActivity.this);

        mStorage = FirebaseStorage.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

//        mDatabaseRef = FirebaseDatabase.getInstance().getReference("houses");


        //get method
//        checkAccount();
//        checkHouse();
        houseInfo();
        getHouse();

        //set custom toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolhome);
        setSupportActionBar(toolbar);

        //set Custom bottomNavigationView
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        //Item BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        break;

                    case R.id.nav_activity:
                        Intent intent1 = new Intent(MainActivity.this, ActivityFeature.class);
                        startActivity(intent1);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
//            addHome.setVisibility(View.GONE);

        } else {
//            addHome.setVisibility(View.VISIBLE);

        }



    }

    public void getHouse(){
//        String showEmail = firebaseUser.getEmail();
//        Data.user = showEmail;
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Device").child(firebaseUser.getUid()).child(Data.user);
        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mHouses.clear();

                for (DataSnapshot houseSnapshot : dataSnapshot.getChildren()) {
                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                    Map<String,Object> map =  houseSnapshot.getValue(genericTypeIndicator);

                    House upload = new House();
                    upload.setName( (String) map.get("name"));
                    mHouses.add(upload);
                }
                mAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });

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


    //Textwatcher addhome
    private TextWatcher textWatcheradd = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String houseNameInput = house_Name.getText().toString().trim();

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

    //Show dialog addhome
    private void showDialogAddHome() {
        dialog.setContentView(R.layout.add_home_popup);
        closePoupUp = (ImageView) dialog.findViewById(R.id.close_popup_home);
        commitHome = (Button) dialog.findViewById(R.id.add_home_code);
        house_Name = (EditText) dialog.findViewById(R.id.house_name);
        commitHome.setOnClickListener(this);

        house_Name.addTextChangedListener(textWatcheradd);
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
                String str_house = house_Name.getText().toString();
                reference = FirebaseDatabase.getInstance().getReference().child("Device").child(firebaseUser.getUid()).child(Data.user).child("house_" + str_house);
                        House house = new House(house_Name.getText().toString().trim());



                        if (TextUtils.isEmpty(str_house)){
                            Toast.makeText(MainActivity.this, "Enter house name", Toast.LENGTH_SHORT).show();
                        }

                        else {

                            String uploadId = reference.push().getKey();
                            reference.setValue(house);
                            Data.keyhouse = uploadId;
                            Data.housenameid = str_house;
                            Toast.makeText(MainActivity.this, str_house + " added", Toast.LENGTH_SHORT).show();


                        }

                pd.hide();
                dialog.dismiss();
            }
        });


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


    //Show dialog house name
    public void showDialogHouseName(){
        dialogHouseName.setContentView(R.layout.popup_edit_house_name);
        closeEditHouseName = dialogHouseName.findViewById(R.id.close_popup_house);
        housnameEdittxt = dialogHouseName.findViewById(R.id.edit_house_nametxt);
        editHouseName = dialogHouseName.findViewById(R.id.btn_edit);
        editHouseName.setOnClickListener(this);

//        housnameEdittxt.addTextChangedListener(houseNameWatcher);

        closeEditHouseName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogHouseName.dismiss();
            }
        });

        dialogHouseName.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogHouseName.show();


        editHouseName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProgressBar.setVisibility(View.VISIBLE);
                uploadProgressBar.setIndeterminate(true);

                String inst_houseName = housnameEdittxt.getText().toString();

                if (TextUtils.isEmpty(inst_houseName)){
                    Toast.makeText(MainActivity.this, "fields are required!", Toast.LENGTH_SHORT).show();


                }

                else {
                    insertHouseName(inst_houseName);
                    dialogHouseName.dismiss();

                }
                uploadProgressBar.setVisibility(View.INVISIBLE);
                pd.hide();
            }
        });

    }


    //Show dialog join home
    private void showDialogJoinHome(){
        dialogJoin.setContentView(R.layout.join_home_popup);
        dialog.setContentView(R.layout.add_home_popup);
        closePoupUpJoin = (ImageView) dialogJoin.findViewById(R.id.close_popup_join);
        invitation = (EditText) dialogJoin.findViewById(R.id.invitation_code);
        commitInvitation = (Button) dialogJoin.findViewById(R.id.add_invitation_code);
        commitInvitation.setOnClickListener(this);

        invitation.addTextChangedListener(textWatcherjoin);

        closePoupUpJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToaAddHome();

            }
        });

        dialogJoin.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogJoin.show();
        dialog.dismiss();

    }


//    //Load fragment comunication
//    private void loadFragmentValue() {
////        getSupportFragmentManager().beginTransaction()
////                .add(R.id.frame_layout_main, new HomeValue())
////                .commit();
//
//        Fragment newFragment = new HomeValue();
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.frame_layout_main, newFragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
//
//    }

    //Edit house name
    public void insertHouseName(String houseName){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());
        HashMap<String, Object> map = new HashMap<>();
        map.put("house_Name", houseName);

        reference.updateChildren(map);

        Toast.makeText(MainActivity.this, "Successfully updated!", Toast.LENGTH_SHORT).show();
        pd.hide();

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

    //all onclick
    public void onClickView(View view) {
        switch (view.getId()) {

            case R.id.tool_add_home :
                showDialogAddHome();
                break;

            case R.id.stream_action :
                startActivity(new Intent(MainActivity.this, StreamingActivity.class));
                break;

            case R.id.card_add_home :
                showDialogAddHome();
                break;


        }
    }

    //check account login
    private void checkAccount() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getContext() == null){
                    return;
                }
                User user = dataSnapshot.getValue(User.class);

                String check = user.getTypeAccount();

                if (check.equals("Owner")) {
//                    penEditTxt.setVisibility(View.VISIBLE);
                    toolAddHome.setVisibility(View.VISIBLE);

                }

                else {

//                    penEditTxt.setVisibility(View.GONE);
                    toolAddHome.setVisibility(View.GONE);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    public void checkHouse(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Device").child(firebaseUser.getUid());

        if (reference != null){
            addHome.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        else {
            addHome.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }


    public void backToaAddHome(){
        showDialogAddHome();
        dialogJoin.dismiss();

    }


    @Override
    public void onClick(View v) {

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
        String[] houseData={clickedHouse.getName()};
        openDetailActivity(houseData);

    }

    @Override
    public void onShowItemClick(int position) {
        House clickedHouse = mHouses.get(position);
        String[] houseData={clickedHouse.getName()};
        openDetailActivity(houseData);

    }

    @Override
    public void onDeleteItemClick(int position) {

        House selectedItem = mHouses.get(position);
        final String selectedKey = selectedItem.getName();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(selectedItem.getName());
        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mDatabaseRef.child(selectedKey).removeValue();
                Toast.makeText(MainActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }
}
