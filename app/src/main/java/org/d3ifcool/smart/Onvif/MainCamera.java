package org.d3ifcool.smart.Onvif;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.d3ifcool.smart.Adapter.RecyclerAdapterHouse;
import org.d3ifcool.smart.Adapter.RecyclerViewAdapterCamera;
import org.d3ifcool.smart.Adapter.RecyclerViewAdapterHistory;
import org.d3ifcool.smart.Home.HousesDetail;
import org.d3ifcool.smart.Home.MainActivity;
import org.d3ifcool.smart.Model.Camera;
import org.d3ifcool.smart.Model.Door;
import org.d3ifcool.smart.Model.House;
import org.d3ifcool.smart.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hari.bounceview.BounceView;

public class MainCamera extends AppCompatActivity implements View.OnClickListener, RecyclerViewAdapterCamera.OnItemClickListener{

    private FloatingActionButton add;
    private Dialog dialog;
    private EditText name, ip, login, pass;
    private ImageView closePouUpOption;
    private Button addCam;
    private TextView txtEmty;
    private ProgressDialog pd;
    private RecyclerView recyclerView;
    private ValueEventListener mDBListener;
    private List<Camera> mCamera;
    private RecyclerViewAdapterCamera mAdapter;
    private DatabaseReference mDatabaseRef;
    private String deviceCode;

    public void openDetailCamera(String[] data) {

        Intent intent = new Intent(this, Cam.class);

        intent.putExtra("NAME_CAME", data[0]);

        intent.putExtra("IP_ADDRESS", data[1]);

        startActivity(intent);

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_camera);

        Intent i = getIntent();
        deviceCode =i.getExtras().getString("DEVICECODE_KEY");

        widget();

        getCam();

    }

    public void getCam() {

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Devices").child(deviceCode).child("Camera");
        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mCamera.clear();

                try {

                    for (DataSnapshot camSnapshot : dataSnapshot.getChildren()) {

                        Camera upload = camSnapshot.getValue(Camera.class);

                        String test = upload.getIpAddress();

                        Log.d("cam", " : " + dataSnapshot);

                        mCamera.add(upload);


                    }

                    mAdapter.notifyDataSetChanged();

                    checkCamera();



                }catch (Exception e){}

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(MainCamera.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }

        });

    }

    public void dialogAddCamera(){

        dialog.setContentView(R.layout.add_camera_popup);

        dialog.setCanceledOnTouchOutside(false);

        name = dialog.findViewById(R.id.camera_name_txt);

        ip = dialog.findViewById(R.id.ipAddress);

        ip.setText("rtsp://");

        login = dialog.findViewById(R.id.login);

        pass = dialog.findViewById(R.id.password);

        addCam = dialog.findViewById(R.id.add_camera_btn);

        closePouUpOption = dialog.findViewById(R.id.close_popup_camera);

        BounceView.addAnimTo(dialog);

        closePouUpOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 dialog.dismiss();

            }

        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        addCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pd.setMessage("Please wait...");

                pd.show();

                final String str_name = name.getText().toString();

                final String str_ip = ip.getText().toString();

                final Camera camera = new Camera(str_name, str_ip);

                if (TextUtils.isEmpty(str_name)) {

                    name.setError("Camera name required");

                    pd.hide();

                }

                if (TextUtils.isEmpty(str_ip)) {

                    ip.setError("Ip address required");

                    pd.hide();

                }

                else {

                    DatabaseReference ref0 = FirebaseDatabase.getInstance().getReference().child("Devices").child(deviceCode).child("Camera");

                    ref0.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()){

                                ip.setError( str_ip + " Already available");

                                pd.hide();

                            }

                            else {

                                Intent i = getIntent();
                                final String deviceCode = i.getExtras().getString("DEVICECODE_KEY");

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Devices").child(deviceCode).child("Camera");

                                ref.child(str_ip.replace(".", ",").replace("/", ",")).setValue(camera);

                                pd.hide();

                                dialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        });
    }

    public void checkCamera(){
        if (mAdapter.getItemCount() != 0){

            txtEmty.setVisibility(View.GONE);

        }

        else {

            txtEmty.setVisibility(View.VISIBLE);

        }
    }
    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(int position) {

        Camera clickedCamera = mCamera.get(position);

        String[] cameraData={clickedCamera.getName(), clickedCamera.getIpAddress()};

        openDetailCamera(cameraData);

    }

    @Override
    public void onShowItemClick(int position) {

        Camera clickedCamera = mCamera.get(position);

        String[] cameraData={clickedCamera.getName(), clickedCamera.getIpAddress()};

        openDetailCamera(cameraData);

    }

    @Override
    public void onDeleteItemClick(int position) {

        Camera selectedItem = mCamera.get(position);

        final String selectedKey = selectedItem.getIpAddress().replace(".", ",").replace( "/", ",");

        new iOSDialogBuilder(this)

                .setCancelable(false)

                .setTitle("DELETE Camera")

                .setSubtitle("are you sure to delete this camera ?")

                .setNegativeListener("Cancel",  new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {

                        dialog.dismiss();
                    }
                })

                .setPositiveListener("YES", new iOSDialogClickListener() {
                    @Override
                    public void onClick(final iOSDialog dialog) {

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Devices").child(deviceCode);

                        ref.child("Camera").child(selectedKey).getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Toast.makeText(MainCamera.this, "Item deleted", Toast.LENGTH_SHORT).show();

                                dialog.dismiss();

                            }

                        });

                    }

                }).build().show();

    }

    private void widget(){

        add = findViewById(R.id.action_add_camera);

        name = findViewById(R.id.camera_name_txt);

        ip = findViewById(R.id.ipAddress);

        login = findViewById(R.id.login);

        pass = findViewById(R.id.password);

        txtEmty = findViewById(R.id.empty_camera);

        addCam = findViewById(R.id.add_camera_btn);

        recyclerView = findViewById(R.id.mRecyclerViewCam);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new LinearSnapHelper().attachToRecyclerView(recyclerView);

        mCamera = new ArrayList<>();

        mAdapter = new RecyclerViewAdapterCamera(MainCamera.this, mCamera);

        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(this);

        closePouUpOption = findViewById(R.id.close_popup_camera);

        dialog = new Dialog(this);

        pd = new ProgressDialog(MainCamera.this);

        pd = new ProgressDialog(this, R.style.MyAlertDialogStyle);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogAddCamera();

            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MainCamera.this, MainActivity.class));

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        super.onBackPressed();
    }
}
