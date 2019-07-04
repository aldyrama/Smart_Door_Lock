package org.d3ifcool.smart.Home;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.d3ifcool.smart.Adapter.RecyclerAdapterMember;
import org.d3ifcool.smart.Data;
import org.d3ifcool.smart.Model.User;
import org.d3ifcool.smart.R;

import java.util.ArrayList;
import java.util.List;

public class MemberDoor extends AppCompatActivity implements RecyclerAdapterMember.OnItemClickListener {

    private List<User> mConnect;
    private RecyclerAdapterMember mAdapterInvite;
    private FirebaseAuth auth;
    private DatabaseReference mDatabaseRef, reference;
    private RecyclerView mRecyclerView;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_door);

        auth = FirebaseAuth.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        widget();

        getMember();
    }

    private void getMember() {

            try {

                Intent i = this.getIntent();
                final String deviceCode =i.getExtras().getString("DEVICE_CODE");
                final String pin = i.getExtras().getString("PIN");

                mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Devices").child(deviceCode);
                mDatabaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange( DataSnapshot dataSnapshot) {

                        mConnect.clear();

                        for (DataSnapshot connectSnapshot : dataSnapshot.child("Member").getChildren()) {

                            User upload = connectSnapshot.getValue(User.class);

                            String email = connectSnapshot.getKey();

                            try {
                                DataSnapshot userSnapshot = dataSnapshot.child("Doors").child(pin).child("Member");

                                User user = userSnapshot.getValue(User.class);


                                Log.d("member", "User" + userSnapshot.child(email).exists());

                                if (userSnapshot.child(email).exists()) {

                                    upload.setChecked(true);

                                } else {

                                    upload.setChecked(false);

                                }

                            } catch (Exception e) {
                            }

                            mConnect.add(upload);

                        }

                        mAdapterInvite.notifyDataSetChanged();

                        checkMembers();

                    }

                    @Override
                    public void onCancelled( DatabaseError databaseError) {

                        Toast.makeText(MemberDoor.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

//                        mProgressBarInvite.setVisibility(View.INVISIBLE);

                    }

                });

            }catch (Exception e){}

    }

    private void checkMembers() {

    }


    private void widget() {

        mConnect = new ArrayList<>();

        mRecyclerView = findViewById(R.id.recycler_member);

        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapterInvite = new RecyclerAdapterMember (this, mConnect);

        mAdapterInvite.setOnItemClickListener(this);

        mRecyclerView.setAdapter(mAdapterInvite);

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
}
