package org.d3ifcool.smart.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.d3ifcool.smart.Data;
import org.d3ifcool.smart.Model.Door;
import org.d3ifcool.smart.Model.House;
import org.d3ifcool.smart.Model.User;
import org.d3ifcool.smart.Notification.MyFirebaseMessagingService;
import org.d3ifcool.smart.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import hari.bounceview.BounceView;


public class RecyclerAdapterDoor extends RecyclerView.Adapter<RecyclerAdapterDoor.RecyclerViewHolder>
        implements View.OnClickListener, RecyclerAdapterHouse.doorInterfaces {
    private Context mContext;
    private List<Door> mDoor;
    private OnItemClickListener mListener;
    FirebaseUser firebaseUser;
    FirebaseAuth auth;
    private Uri mImageUri;
    StorageReference storageRef;
    DatabaseReference referencee;
    ProgressDialog pd;
    int status;


    public RecyclerAdapterDoor (FragmentActivity activity, List<Door> uploads) {
        mContext = activity;
        mDoor = uploads;
    }

    @Override
    public RecyclerAdapterDoor.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.row_model_door, parent, false);
        return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, int position) {
        final Door currentDoor = mDoor.get(position);
        holder.name_door.setText(currentDoor.getDoorName());
        holder.lockImageView.setOnClickListener(this);
        holder.mProgressbar.setVisibility(View.VISIBLE);
        final String time = holder.getTimeToday();
        final String date = holder.getDateToday();

        final String url = "https://firebasestorage.googleapis.com/v0/b/smartdoor-7d0e6.appspot.com/o/lock_door.png?alt=media&token=2a903126-fc6e-4f87-b62c-9ccb7e9f5383";
        final String url1 = "https://firebasestorage.googleapis.com/v0/b/smartdoor-7d0e6.appspot.com/o/unlock_door.png?alt=media&token=15c98219-2c31-49db-9338-968e35cced71";
        final String urlSystem = "https://firebasestorage.googleapis.com/v0/b/smartdoor-7d0e6.appspot.com/o/system.png?alt=media&token=4073718b-f3a5-4bd8-afd0-61b8e0829fc5";

        Intent i = ((Activity) mContext).getIntent();
        final String name =i.getExtras().getString("NAME_KEY");
        final String deviceCode =i.getExtras().getString("DEVICECODE_KEY");
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Devices").child(deviceCode).child("Doors")
                .child(currentDoor.getDoorPin());
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Door door = dataSnapshot.getValue(Door.class);
                Door doorr = new Door();

                String statustDoor = door.getStatus();
                holder.statusdoor.setText(door.getStatus());
                try {

                int lock = door.getDoorLock();
                status = lock;
                String system = door.getStatus();
                String nameDoor = door.getDoorName();

//                if (system.equals("Open")){
//                        holder.lockImageView.setEnabled(false);
//
//                    }

                if (lock == 0){
                    Picasso.with(mContext)
                            .load(url)
                            .into(holder.lockImageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    holder.mProgressbar.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onError() {

                                }
                            });

                    holder.status.setText("Locked");


                }

                else {
                    Picasso.with(mContext)
                            .load(url1)
                            .into(holder.lockImageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    holder.mProgressbar.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onError() {

                                }
                            });

                    holder.status.setText("Unlocked");

                    }


                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        BounceView.addAnimTo(holder.lockImageView)
                .setScaleForPopOutAnim(0f, 0f);
        holder.lockImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String url = "https://firebasestorage.googleapis.com/v0/b/smartdoor-7d0e6.appspot.com/o/lock_door.png?alt=media&token=2a903126-fc6e-4f87-b62c-9ccb7e9f5383";
                final String url1 = "https://firebasestorage.googleapis.com/v0/b/smartdoor-7d0e6.appspot.com/o/unlock_door.png?alt=media&token=15c98219-2c31-49db-9338-968e35cced71";
                final DatabaseReference reference0 = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getEmail().replace(".",","));

                reference0.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final Intent i = ((Activity) mContext).getIntent();
                        String name = i.getExtras().getString("NAME_KEY");
                        String deviceCode = i.getExtras().getString("DEVICECODE_KEY");
                        final User getUser = dataSnapshot.getValue(User.class);

                        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Devices").child(deviceCode).child("Doors")
                                .child(currentDoor.getDoorPin());
                        final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Devices").child(deviceCode).child("History");
                        dataSnapshot.child("Devices").child(deviceCode).child("Doors").child(currentDoor.getDoorPin());

                        Door door = dataSnapshot.getValue(Door.class);

                        final String lock;
                        String uploadId = reference1.push().getKey();
                        String system = door.getStatus();
                        String nameDoor = door.getDoorName();


                        if (!isInternetOn()){
                            Toast.makeText(v.getContext(), "no internet connection!", Toast.LENGTH_SHORT).show();
                        }

                        else if (status == 0) {
                            holder.mProgressbar.setVisibility(View.VISIBLE);
                            status = 1;
                            lock = "Unlock by app";
                            reference.child("doorLock").setValue(status);
                            reference1.child(uploadId).setValue(getUser);
                            reference1.child(uploadId).child("lock").setValue(lock);
                            reference1.child(uploadId).child("lockImage").setValue(url1);
                            reference1.child(uploadId).child("door").setValue(currentDoor.getDoorName());
                            reference1.child(uploadId).child("time").setValue(time);
                            reference1.child(uploadId).child("date").setValue(date);


                        } else {
                            holder.mProgressbar.setVisibility(View.VISIBLE);
                            status = 0;
                            lock = "Lock by app";
                            reference.child("doorLock").setValue(status);
                            reference1.child(uploadId).setValue(getUser);
                            reference1.child(uploadId).child("lock").setValue(lock);
                            reference1.child(uploadId).child("lockImage").setValue(url);
                            reference1.child(uploadId).child("door").setValue(currentDoor.getDoorName());
                            reference1.child(uploadId).child("time").setValue(time);
                            reference1.child(uploadId).child("date").setValue(date);




                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public final boolean isInternetOn()
    {
        ConnectivityManager connec = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        // ARE WE CONNECTED TO THE NET
        if ( connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED )
        {
            // MESSAGE TO SCREEN FOR TESTING (IF REQ)
            //Toast.makeText(this, connectionType + ” connected”, Toast.LENGTH_SHORT).show();
            return true;
        }
        else if ( connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED
                ||  connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED  )
        {
            return false;
        }

        return false;
    }

    @Override
    public int getItemCount() {
        return mDoor.size();
    }

    @Override
    public void getDoor() {

    }


    public interface OnItemClickListener {
        void onItemClick(int position);
        void onShowItemClick(int position);
        void onDeleteItemClick(int position);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;

    }


    @Override
    public void onClick(View v) {

    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        public TextView name_door, statusdoor, dateTextView, status;
        public ImageView lockImageView;
        public AnimationDrawable imagesAnimation;
        ProgressBar mProgressbar;


        public RecyclerViewHolder(View itemView) {
            super(itemView);
            name_door =itemView.findViewById ( R.id.doorName );
            statusdoor = itemView.findViewById(R.id.statustDoor);
            lockImageView = itemView.findViewById(R.id.lockDoor);
            mProgressbar = itemView.findViewById(R.id.prog_lock);
            status = itemView.findViewById(R.id.status_lock);
            imagesAnimation = (AnimationDrawable) lockImageView.getBackground();
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }


    private String getDateToday(){
        DateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
        Date date=new Date();
        String today= dateFormat.format(date);
        return today;
    }


    private String getTimeToday(){
            DateFormat dateFormat=new SimpleDateFormat("hh:mm:ss a");
            Date date=new Date();
            String today= dateFormat.format(date);
            return today;
        }


        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem deleteItem = menu.add(Menu.NONE, 1, 1, "Delete");

            deleteItem.setOnMenuItemClickListener(this);
        }


        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {

                    switch (item.getItemId()) {
                        case 2:
                            mListener.onShowItemClick(position);
                            return true;
                        case 1:
                            mListener.onDeleteItemClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }
}
