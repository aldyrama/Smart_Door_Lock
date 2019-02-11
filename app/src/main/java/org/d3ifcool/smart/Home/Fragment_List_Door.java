package org.d3ifcool.smart.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import org.d3ifcool.smart.Model.Door;
import org.d3ifcool.smart.R;

import java.util.ArrayList;
import java.util.List;

public class Fragment_List_Door extends Fragment implements View.OnClickListener, RecyclerAdapterDoor.OnItemClickListener {

    FirebaseAuth auth;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    private RecyclerView mRecyclerView;
    private RecyclerAdapterDoor mAdapter;
    private ProgressBar mProgressBar;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private List<Door> mDoor;

    public Fragment_List_Door() {

    }
//
//    private void openDetailActivity(String[] data){
//        Intent intent = new Intent(this, DetailsActivity.class);
//        intent.putExtra("NAME_KEY",data[0]);
//        intent.putExtra("DESCRIPTION_KEY",data[1]);
//        intent.putExtra("IMAGE_KEY",data[2]);
//        startActivity(intent);
    //    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_door_fragment,container,false);

        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Door").child(firebaseUser.getUid());

        mRecyclerView = view.findViewById(R.id.mRecyclerView_detail);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mProgressBar = view.findViewById(R.id.myDataLoaderProgressBarDoor);
        mProgressBar.setVisibility(View.VISIBLE);

        mDoor = new ArrayList<>();
        mAdapter = new RecyclerAdapterDoor (getActivity(), mDoor);
        mRecyclerView.setAdapter(mAdapter);
//        mAdapter.setOnItemClickListener(getActivity());

        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mDoor.clear();

                for (DataSnapshot teacherSnapshot : dataSnapshot.getChildren()) {
                    Door upload = teacherSnapshot.getValue(Door.class);
                    upload.setKey(teacherSnapshot.getKey());
                    mDoor.add(upload);
                }
                mAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });

        return view;


    }
    public void onItemClick(int position) {
//        Door clickedTeacher= mDoor.get(position);
//        String[] teacherData={clickedTeacher.getDoorName()};
//        openDetailActivity(teacherData);
    }

    @Override
    public void onShowItemClick(int position) {
//        Door clickedTeacher= mDoor.get(position);
//        String[] teacherData={clickedTeacher.getDoorName()};
//        openDetailActivity(teacherData);
    }

    @Override
    public void onDeleteItemClick(int position) {
        Door selectedItem = mDoor.get(position);
        final String selectedKey = selectedItem.getKey();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(selectedItem.getDoorName());
        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mDatabaseRef.child(selectedKey).removeValue();
                Toast.makeText(getActivity(), "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }

    @Override
    public void onClick(View v) {

    }
}
