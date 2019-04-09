package org.d3ifcool.smart.Adapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
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

//import com.squareup.picasso.Picasso;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.d3ifcool.smart.Data;
import org.d3ifcool.smart.Model.Door;
import org.d3ifcool.smart.Model.House;
import org.d3ifcool.smart.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public  class RecyclerAdapterHouse extends RecyclerView.Adapter<RecyclerAdapterHouse.RecyclerViewHolder> implements View.OnClickListener {
    private Context mContext;
    private List<House> houses;
    private OnItemClickListener mListener;
    private boolean status = false;

    public interface doorInterfaces{

        void getDoor();

    }

    public RecyclerAdapterHouse (Context context, List<House> uploads) {
        mContext = context;
        houses = uploads;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.row_model_house, parent, false);
        return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        final House currentHouse = houses.get(position);
        holder.name_house.setText(currentHouse.getName());
        currentHouse.getDeviceCode();
        holder.allLock.setOnClickListener(this);
        holder.mProgressbar.setVisibility(View.VISIBLE);
        holder.mProgressbar.setSecondaryProgress(50000);
        holder.vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

        Log.d("houseName", "ondatachange" + currentHouse.getName());
        final String url = "https://firebasestorage.googleapis.com/v0/b/smartdoor-7d0e6.appspot.com/o/lock_door.png?alt=media&token=2a903126-fc6e-4f87-b62c-9ccb7e9f5383";
        final String url1 = "https://firebasestorage.googleapis.com/v0/b/smartdoor-7d0e6.appspot.com/o/unlock_door.png?alt=media&token=15c98219-2c31-49db-9338-968e35cced71";


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Devices").child(currentHouse.getDeviceCode());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                House house = dataSnapshot.getValue(House.class);
//                Door door = new Door();
//                DatabaseReference reference0 = FirebaseDatabase.getInstance().getReference("Devices").child(currentHouse.getDeviceCode());
//                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Devices").child(currentHouse.getDeviceCode()).child("Doors")
//                        .child(door.getDoorPin());

                try {

                    boolean lock = house.isHouse_lock();
                    boolean guestDetect = house.isGuest();

                    if (lock == false) {
//                        reference1.setValue(false);
                        Picasso.with(mContext)
                                .load(url)
                                .into(holder.allLock, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                        holder.mProgressbar.setVisibility(View.INVISIBLE);


                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });
                    } else {
//                        reference1.setValue(true);
                        Picasso.with(mContext)
                                .load(url1)
                                .into(holder.allLock, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                        holder.mProgressbar.setVisibility(View.INVISIBLE);

                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });

                    }

                    if (guestDetect == false) {

                        holder.isGuest.setImageResource(R.drawable.bell_no_guest);
                        holder.guest.setText("no guests");

                    } else {

                        holder.isGuest.setImageResource(R.drawable.bell_guest);
                        holder.guest.setText("there is guest");
//                        holder.vibrator.vibrate(800);


                    }
                }catch (Exception e){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.allLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Devices").child(currentHouse.getDeviceCode());

                if (status == false){
                    status = true;
                    reference.child("house_lock").setValue(status);
                    holder.locktxt.setText("House is not locked");
                    Toast.makeText(mContext, "House unlocked", Toast.LENGTH_SHORT).show();


                }

                else {
                    status = false;
                    reference.child("house_lock").setValue(status);
                    holder.locktxt.setText("House locked");
                    Toast.makeText(mContext, "House locked", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return houses.size();
    }

    @Override
    public void onClick(View v) {

    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        public TextView name_house, dateTextView, guest, locktxt;
        public ImageView doorView, allLock, isGuest;
        ProgressBar mProgressbar;
        Vibrator vibrator;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            name_house = itemView.findViewById ( R.id.houseName );
//            doorView = itemView.findViewById(R.id.doo_status);
            guest = itemView.findViewById(R.id.txt_guest);
            allLock = itemView.findViewById(R.id.all_lockHouse);
            isGuest = itemView.findViewById(R.id.guest);
            locktxt = itemView.findViewById(R.id.allock);
            mProgressbar = itemView.findViewById(R.id.progress_lock);
            mProgressbar.setSecondaryProgress(50000);
            vibrator = (Vibrator) itemView.getContext().getSystemService(Context.VIBRATOR_SERVICE);

            Typeface typeface = Typeface.createFromAsset(itemView.getContext().getAssets(), "font/Aaargh.ttf");
            name_house.setTypeface(typeface);
            name_house.setTypeface(null, Typeface.BOLD);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
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
            MenuItem showItem = menu.add( Menu.NONE, 1, 1, "Show");
            MenuItem deleteItem = menu.add(Menu.NONE, 2, 2, "Delete");

            showItem.setOnMenuItemClickListener(this);
            deleteItem.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {

                    switch (item.getItemId()) {
                        case 1:
                            mListener.onShowItemClick(position);
                            return true;
                        case 2:
                            mListener.onDeleteItemClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }


    public interface OnItemClickListener {
        void onItemClick(int position);
        void onShowItemClick(int position);
        void onDeleteItemClick(int position);

    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    private String getDateToday(){
        DateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String today= dateFormat.format(date);
        return today;
    }


}