package org.d3ifcool.smart.Home;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
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

import org.d3ifcool.smart.Data;
import org.d3ifcool.smart.Model.Door;
import org.d3ifcool.smart.Model.House;
import org.d3ifcool.smart.R;
import org.d3ifcool.smart.StartActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HouseDetail extends AppCompatActivity implements  View.OnClickListener, RecyclerAdapterDoor.OnItemClickListener {

    TextView houseName_detail, detal_date;
    ImageView cam, closePoupUpDoor, addDoor_btn, streaming_cam;
    Dialog dialog_Door;
    Button commitDoor;
    EditText addDoorEdtxt;

    FirebaseAuth auth;
    DatabaseReference reference;
    FirebaseUser firebaseUser;


    private RecyclerView mRecyclerViewDoor;
    private RecyclerAdapterDoor mAdapterDoor;
    private ProgressBar mProgressBarDoor;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private List<Door> mDoor;
    private ProgressDialog pd;


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

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/RemachineScript_Personal_Use.ttf");
        houseName_detail.setTypeface(typeface);

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
//        loadFragmentValue();

        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String keyHouse = new Door().getKey();

        dialog_Door = new Dialog(this);

        closePoupUpDoor = (ImageView) findViewById(R.id.close_popup_door);
        commitDoor = (Button) findViewById(R.id.add_door_btn);
        addDoorEdtxt = (EditText) findViewById(R.id.door_name_txt);
        addDoor_btn = (ImageView) findViewById(R.id.add_door_tool);
        initializeWidgets();
        getDoor();

        addDoor_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddDoor();
            }
        });

        Intent i = this.getIntent();
        String name =i.getExtras().getString("NAME_KEY");

        houseName_detail.setText(name);
        detal_date.setText(getDateToday());

        mRecyclerViewDoor = findViewById(R.id.mRecyclerView_detaill);
        mRecyclerViewDoor.setHasFixedSize(true);
        mRecyclerViewDoor.setLayoutManager(new LinearLayoutManager(this));

        mProgressBarDoor = findViewById(R.id.myDataLoaderProgressBarDoorr);
        mProgressBarDoor.setVisibility(View.VISIBLE);

        mDoor = new ArrayList<>();
        mAdapterDoor = new RecyclerAdapterDoor (this, mDoor);
        mRecyclerViewDoor.setAdapter(mAdapterDoor);
        mAdapterDoor.setOnItemClickListener(HouseDetail.this);

    }


    public void getDoor(){
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Device").child(firebaseUser.getUid())
                .child(Data.user).child("house_" +Data.housenameid);
        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mDoor.clear();
                dataSnapshot.child("doorName");
                for (DataSnapshot doorSnapshot : dataSnapshot.getChildren()) {
                    try{
                        GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                        Map<String,Object> map =  doorSnapshot.getValue(genericTypeIndicator);

                        Door upload = new Door();
                        upload.setDoorName( (String) map.get("doorName"));
                        //Toast.makeText(HouseDetail.this, (String) map.get("name"), Toast.LENGTH_SHORT).show();
//                    Door upload = houseSnapshot.getValue(Door.class);
//                    upload.setKey(houseSnapshot.getKey());
                        mDoor.add(upload);
                    }catch (Exception e){}
                }
                mAdapterDoor.notifyDataSetChanged();
                mProgressBarDoor.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HouseDetail.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressBarDoor.setVisibility(View.INVISIBLE);
            }
        });

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

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(selectedItem.getDoorName());
        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mDatabaseRef.child(selectedKey).removeValue();
                Toast.makeText(HouseDetail.this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showDialogAddDoor(){
        dialog_Door.setContentView(R.layout.add_door_popup);
        closePoupUpDoor = (ImageView) dialog_Door.findViewById(R.id.close_popup_door);
        commitDoor = (Button) dialog_Door.findViewById(R.id.add_door_btn);
        addDoorEdtxt = (EditText) dialog_Door.findViewById(R.id.door_name_txt);
        commitDoor.setOnClickListener(this);

        addDoorEdtxt.addTextChangedListener(textWatcheradd);
//        homeAddress.addTextChangedListener(textWatcheradd);


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

                reference = FirebaseDatabase.getInstance().getReference().child("Device").child(firebaseUser.getUid()).child(Data.user);

                Door door = new Door(addDoorEdtxt.getText().toString().trim());

                String str_house = addDoorEdtxt.getText().toString();

                if (TextUtils.isEmpty(str_house)){
                    Toast.makeText(HouseDetail.this, "Enter door name", Toast.LENGTH_SHORT).show();
                }

                else {
                    String uploadId = reference.push().getKey();
//                    reference.child("name").setValue(Data.housenameid);
                    reference.child("house_" +Data.housenameid).child("door_" + str_house).setValue(door);
                    Data.doorName = str_house;
//                    reference.child(uploadId).setValue(door);



                }

                pd.hide();
                dialog_Door.dismiss();
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
        }

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(HouseDetail.this, MainActivity.class));
    }
}
