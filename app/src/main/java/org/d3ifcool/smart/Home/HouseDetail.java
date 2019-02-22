package org.d3ifcool.smart.Home;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import org.d3ifcool.smart.Activity.ActivityFeature;
import org.d3ifcool.smart.Adapter.RecyclerAdapterDoor;
import org.d3ifcool.smart.BottomNavigation.BottomNavigationHelperHouse;
import org.d3ifcool.smart.BottomNavigation.BottomNavigationViewHelper;
import org.d3ifcool.smart.Data;
import org.d3ifcool.smart.Family.FamilyActivity;
import org.d3ifcool.smart.Model.Door;
import org.d3ifcool.smart.Model.House;
import org.d3ifcool.smart.R;
import org.d3ifcool.smart.Setting.SettingActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.security.AccessController.getContext;

public class HouseDetail extends AppCompatActivity implements  View.OnClickListener, RecyclerAdapterDoor.OnItemClickListener {

    private static final String TAG = "HouseDetail";
    TextView houseName_detail, detal_date;
    ImageView cam, closePoupUpDoor, addDoor_btn, streaming_cam, lock;
    Dialog dialog_Door;
    Button commitDoor;
    EditText addDoorEdtxt, doorPin;
    CardView addDoor;

    FirebaseAuth auth;
    DatabaseReference reference;
    FirebaseUser firebaseUser;

    private RecyclerView mRecyclerViewDoor;
    private RecyclerAdapterDoor mAdapterDoor;
    private ProgressBar mProgressBarDoor;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private List<Door>  mDoor;
    private ProgressDialog pd;
    int doorlock;


//    private void openDetailActivity(String[] data){
//        Intent intent = new Intent(this, HouseDetail.class);
//        intent.putExtra("NAME_KEY",data[0]);
////        intent.putExtra("DESCRIPTION_KEY",data[1]);
////        intent.putExtra("IMAGE_KEY",data[2]);
//        startActivity(intent);
//    }


    private void initializeWidgets(){
        houseName_detail = findViewById(R.id.myhouse_detail);
        detal_date = findViewById(R.id.date_detail);
        streaming_cam = findViewById(R.id.stream_cam);
        dialog_Door = new Dialog(this);

        closePoupUpDoor = (ImageView) findViewById(R.id.close_popup_door);
        lock = (ImageView) findViewById(R.id.lockDoor);
        commitDoor = (Button) findViewById(R.id.add_door_btn);
        addDoorEdtxt = (EditText) findViewById(R.id.door_name_txt);
        addDoor_btn = (ImageView) findViewById(R.id.add_door_tool);
        addDoor = (CardView) findViewById(R.id.cardAddDoor);
        doorPin = (EditText) findViewById(R.id.door_pin);

        mRecyclerViewDoor = findViewById(R.id.mRecyclerView_detaill);
        mRecyclerViewDoor.setHasFixedSize(true);
        mRecyclerViewDoor.setLayoutManager(new LinearLayoutManager(this));

        mProgressBarDoor = findViewById(R.id.myDataLoaderProgressBarDoorr);
        mProgressBarDoor.setVisibility(View.VISIBLE);

        mDoor = new ArrayList<>();
        mAdapterDoor = new RecyclerAdapterDoor (this, mDoor);
        mRecyclerViewDoor.setAdapter(mAdapterDoor);
        mAdapterDoor.setOnItemClickListener(HouseDetail.this);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/Aaargh.ttf");
        houseName_detail.setTypeface(typeface);
        houseName_detail.setTypeface(null, Typeface.BOLD);

    }

    private String getDateToday(){
        DateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd");
        Date date=new Date();
        String today= dateFormat.format(date);
        return today;

    }
//
//    private String getRandomCategory(){
//        String[] categories={"ZEN","BUDHIST","YOGA"};
//        Random random=new Random();
//        int index=random.nextInt(categories.length-1);
//        return categories[index];
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_house);
        setStatustBarColor(R.color.colorWhite);

        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String keyHouse = new Door().getKey();

        //Get method
        initializeWidgets();
        getDoor();

        Intent i = this.getIntent();
        String name =i.getExtras().getString("NAME_KEY");
        String deviceCode =i.getExtras().getString("DEVICECODE_KEY");
        houseName_detail.setText(name);
        detal_date.setText(getDateToday());


        addDoor_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddDoor();

            }

        });

//        lock.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {

//                if (doorlock == 0){
//                    lock.setImageResource(R.drawable.unlock);
//                    doorlock = 1;
//                }
//
//                else {
//
//                    lock.setImageResource(R.drawable.lock);
//                    doorlock = 0;
//                }
//            }
//        });


        //set Custom bottomNavigationView
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar_house);
        BottomNavigationHelperHouse.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);


        //Item BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
//                    case R.id.nav_door:
//                        break;

                    case R.id.nav_member:
                        Intent intent1 = new Intent(HouseDetail.this, ActivityFeature.class);
                        startActivity(intent1);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    case R.id.nav_activity_house:
                        Intent intent2 = new Intent(HouseDetail.this, ActivityFeature.class);
                        Data.nameKey = String.valueOf(intent2);
                        startActivity(intent2);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;


                }

                return false;
            }

        });

    }


    public void checkDoor(){

        if (mAdapterDoor.getItemCount() != 0){

            addDoor.setVisibility(View.GONE);
            mRecyclerViewDoor.setVisibility(View.VISIBLE);

        }

        else {

            addDoor.setVisibility(View.VISIBLE);
            mRecyclerViewDoor.setVisibility(View.GONE);
        }

    }


    public void getDoor(){
        Intent i = getIntent();
        String deviceCode =i.getExtras().getString("DEVICECODE_KEY");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Devices").child(deviceCode).child("Doors");
        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mDoor.clear();

                for (DataSnapshot doorSnapshot : dataSnapshot.getChildren()) {
                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                        Map<String,Object> map =  doorSnapshot.getValue(genericTypeIndicator);

                        Door uploadDoor = new Door();
                        uploadDoor.setDoorName( (String) map.get("doorName"));

                    Door door = doorSnapshot.getValue(Door.class);
                    Log.d(TAG, "onDataChange: " + door.getDoorName());
                    mDoor.add(uploadDoor);
//                for (DataSnapshot doorSnapshot : dataSnapshot.getChildren()) {
//                    try{
//                        GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
//                        Map<String,Object> map =  doorSnapshot.getValue(genericTypeIndicator);
//
//                        Door uploadDoor = new Door();
//                        uploadDoor.setDoorName( (String) map.get("doorName"));
//                        //Toast.makeText(HouseDetail.this, (String) map.get("name"), Toast.LENGTH_SHORT).show();
////                    Door upload = houseSnapshot.getValue(Door.class);
////                    upload.setKey(houseSnapshot.getKey());
//                        mDoor.add(uploadDoor);
//                        Data.checkRecyclerDoor = uploadDoor;


                }

                mAdapterDoor.notifyDataSetChanged();
                mProgressBarDoor.setVisibility(View.GONE);

                checkDoor();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HouseDetail.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressBarDoor.setVisibility(View.INVISIBLE);

            }

        });

    }


    private void loadFragmentValue() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.door_fragment, new Fragment_List_Door())
                .commit();

        Fragment newFragment = new Fragment_List_Door();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.door_fragment, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }




    @Override
    public void onItemClick(int position) {
//        Door clickedDoor = mDoor.get(position);
//        String[] teacherDoor={clickedDoor.getDoorName()};
//        openDetailActivity(teacherDoor);

    }


    @Override
    public void onShowItemClick(int position) {
//        Door clickedDoor = mDoor.get(position);
//        String[] door = {clickedDoor.getDoorName()};
//        openDetailActivity(door);

    }


    @Override
    public void onDeleteItemClick(int position) {
        Door selectedItem = mDoor.get(position);
        final String selectedKey = selectedItem.getDoorName();
        Data.doorName = selectedKey;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("house_" + Data.housenameid).
                child("door_" + Data.doorName);
        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mDatabaseRef.child("door_" + Data.doorName).removeValue();
                Toast.makeText(HouseDetail.this, "Item deleted", Toast.LENGTH_SHORT).show();

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.stream_cam :
                startActivity(new Intent(HouseDetail.this, StreamingActivity.class));
                break;

            case R.id.cardAddDoor :
                showDialogAddDoor();
                break;
        }

    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(HouseDetail.this, MainActivity.class));

    }


    public void showDialogAddDoor(){
        dialog_Door.setContentView(R.layout.add_door_popup);
        closePoupUpDoor = (ImageView) dialog_Door.findViewById(R.id.close_popup_door);
        commitDoor = (Button) dialog_Door.findViewById(R.id.add_door_btn);
        addDoorEdtxt = (EditText) dialog_Door.findViewById(R.id.door_name_txt);
        doorPin = (EditText) dialog_Door.findViewById(R.id.door_pin);
        commitDoor.setOnClickListener(this);


        closePoupUpDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_Door.dismiss();

            }

        });

        dialog_Door.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_Door.show();

        commitDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(HouseDetail.this);
                pd.setMessage("Please wait...");
                pd.show();

                Intent i = getIntent();
                String deviceCode =i.getExtras().getString("DEVICECODE_KEY");

                String str_door = addDoorEdtxt.getText().toString();
                String str_pin = doorPin.getText().toString();
                reference = FirebaseDatabase.getInstance().getReference().child("Devices").child(deviceCode).child("Doors").child(str_door);

                Door door = new Door(str_door, str_pin);

                if (TextUtils.isEmpty(str_door)){
                    addDoorEdtxt.setError("Door name required");
                }

                else if (TextUtils.isEmpty(str_pin)){
                    doorPin.setError("Pin door required");

                }

                else {
//                    String uploadId = reference.push().getKey();
//                    reference.child("name").setValue(Data.housenameid);
                    reference.setValue(door);
                    Data.doorName = str_door;
//                    reference.child(uploadId).setValue(door);

                }

                pd.hide();
                dialog_Door.dismiss();

            }
        });

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


    private TextWatcher textWatcheradd = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String doorNameInput = addDoorEdtxt.getText().toString().trim();

            commitDoor.setEnabled(!doorNameInput.isEmpty());

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

}
