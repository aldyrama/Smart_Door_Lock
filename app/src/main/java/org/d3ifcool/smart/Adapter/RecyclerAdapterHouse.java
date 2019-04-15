package org.d3ifcool.smart.Adapter;
import android.annotation.SuppressLint;
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

import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.d3ifcool.smart.Data;
import org.d3ifcool.smart.Home.HousesDetail;
import org.d3ifcool.smart.Home.MainActivity;
import org.d3ifcool.smart.Model.Door;
import org.d3ifcool.smart.Model.House;
import org.d3ifcool.smart.R;
import org.d3ifcool.smart.WifiConfiguration.EsptouchDemoActivity;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public  class RecyclerAdapterHouse extends RecyclerView.Adapter<RecyclerAdapterHouse.RecyclerViewHolder> implements View.OnClickListener {
    private Context mContext;
    private List<House> houses;
    private OnItemClickListener mListener;
    private boolean status = false;
    DoorInterfaces doorInterfaces;
    Activity activity;

    public RecyclerAdapterHouse(DoorInterfaces doorInterfaces) {

        this.doorInterfaces = (DoorInterfaces) doorInterfaces;
    }

    public interface DoorInterfaces{

        void refresh();

        void getDevice(House connect);

    }

    private void openDetailActivity(String[] data) {

        Intent intent = new Intent(mContext, HousesDetail.class);

        intent.putExtra("NAME_KEY", data[0]);

        intent.putExtra("DEVICECODE_KEY", data[1]);

        mContext.startActivity(intent);
//        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    public RecyclerAdapterHouse (Context context, List<House> uploads) {

        mContext = context;

        houses = uploads;

    }

    @NotNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.row_model_house, parent, false);
        return new RecyclerViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NotNull final RecyclerViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        final House currentHouse = houses.get(position);

        holder.name_house.setText(currentHouse.getName());

        currentHouse.getDeviceCode();

        holder.allLock.setOnClickListener(this);

        holder.mProgressbar.setVisibility(View.VISIBLE);

        holder.mProgressbar.setSecondaryProgress(50000);

        holder.vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

        activity = (Activity) mContext;

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Devices").child(currentHouse.getDeviceCode());
        ref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                House house = dataSnapshot.getValue(House.class);

                try {

                    boolean connect = house.isConnect();

                    String now = getDateToday();

                    Log.d("total", "now" + now);

                    String statusDevice = house.getUpdate();

                    Log.d("total", "updateDate" + statusDevice);

                    Date dateNow = new SimpleDateFormat("dd/M/yyyy HH:mm").parse(now);

                    Date dateCont = new SimpleDateFormat("dd/M/yyyy HH:mm").parse(statusDevice);

                    long millisNow = dateNow.getTime();

                    long millisCont = dateCont.getTime();

                    long totalMillis = (millisNow - millisCont);

                    Log.d("total", "millis" + totalMillis);

                    if (totalMillis >= 12000) {

                        ref.child("connect").setValue(false);

                        holder.Cdevice.setImageResource(R.drawable.ic_not_connect);

                        holder.txtcConnect.setText(R.string.disconnect);

                    }

                    else {

                        holder.Cdevice.setImageResource(R.drawable.ic_connect);

                        holder.txtcConnect.setText(R.string.connect);

                    }

//                    if (connect == false){
//
//                        holder.Cdevice.setImageResource(R.drawable.ic_not_connect);
//                        holder.txtcConnect.setText(R.string.disconnect);
//
//                    }
//
//                    else {
//
//                        holder.Cdevice.setImageResource(R.drawable.ic_connect);
//                        holder.txtcConnect.setText(R.string.connect);
//
//                    }


                } catch (

                        ParseException e) {

                    e.printStackTrace();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Devices").child(currentHouse.getDeviceCode());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                House house = dataSnapshot.getValue(House.class);

                try {

                    boolean lock = house.isHouse_lock();

                    boolean guestDetect = house.isGuest();

                    boolean connect = house.isConnect();

                    if (guestDetect == false) {

                        holder.isGuest.setImageResource(R.drawable.bell_no_guest);

                        holder.guest.setText(R.string.no_guest);

                    } else {

                        holder.isGuest.setImageResource(R.drawable.bell_guest);

                        holder.guest.setText(R.string.guest);

                    }

//                    if (connect == false){
//
//                        holder.Cdevice.setImageResource(R.drawable.ic_not_connect);
//                        holder.txtcConnect.setText(R.string.disconnect);
//
//                    }
//
//                    else {
//
//                        holder.Cdevice.setImageResource(R.drawable.ic_connect);
//                        holder.txtcConnect.setText(R.string.connect);
//
//                    }


                }catch (Exception e){

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        holder.door.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                House clickedHouse = houses.get(position);

                String[] houseData={clickedHouse.getName(), clickedHouse.getDeviceCode()};

                openDetailActivity(houseData);

                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }

        });

        holder.allLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Devices").child(currentHouse.getDeviceCode());

                if (!status){

                    status = true;

                    reference.child("house_lock").setValue(status);

                    holder.locktxt.setText(R.string.house_not_locket);

                    Toast.makeText(mContext, "House unlocked", Toast.LENGTH_SHORT).show();

                }

                else {

                    status = false;

                    reference.child("house_lock").setValue(status);

                    holder.locktxt.setText(R.string.house_locket);

                    Toast.makeText(mContext, "House locked", Toast.LENGTH_SHORT).show();

                }

            }

        });

    }

    private String getDateToday(){

        DateFormat dateFormat=new SimpleDateFormat("dd/M/yyyy HH:mm");

        Date date = new Date();

        String today= dateFormat.format(date);

        return today;

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

        private TextView name_house, guest, locktxt, txtcConnect;
        private ImageView Cdevice, allLock, isGuest, door;
        ProgressBar mProgressbar;
        Vibrator vibrator;

        private RecyclerViewHolder(View itemView) {
            super(itemView);

            name_house = itemView.findViewById ( R.id.houseName );

            Cdevice = itemView.findViewById(R.id.deviceStatus);

            guest = itemView.findViewById(R.id.txt_guest);

            txtcConnect = itemView.findViewById(R.id.txt_device);

            allLock = itemView.findViewById(R.id.all_lockHouse);

            isGuest = itemView.findViewById(R.id.guest);

            locktxt = itemView.findViewById(R.id.allock);

            door = itemView.findViewById(R.id.doorDetail);

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

}