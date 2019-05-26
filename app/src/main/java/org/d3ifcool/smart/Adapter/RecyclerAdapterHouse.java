package org.d3ifcool.smart.Adapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.d3ifcool.smart.Home.HousesDetail;
import org.d3ifcool.smart.Model.House;
import org.d3ifcool.smart.Model.User;
import org.d3ifcool.smart.Onvif.MainCamera;
import org.d3ifcool.smart.Onvif.OnvifInput;
import org.d3ifcool.smart.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static java.security.AccessController.getContext;

public  class RecyclerAdapterHouse extends RecyclerView.Adapter<RecyclerAdapterHouse.RecyclerViewHolder> implements View.OnClickListener {
    private Context mContext;
    private List<House> houses;
    private OnItemClickListener mListener;
    private boolean status = false;
    DoorInterfaces doorInterfaces;
    private Activity activity;
    private ObjectAnimator textBlinking;
    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;


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

    }

    private void openDetailCamera(String[] data) {

        Intent intent = new Intent(mContext, MainCamera.class);

        intent.putExtra("NAME_KEY", data[0]);

        intent.putExtra("DEVICECODE_KEY", data[1]);

        mContext.startActivity(intent);

    }

    public RecyclerAdapterHouse (Context context, List<House> uploads) {

        mContext = context;

        houses = uploads;

    }

    @Override
    public RecyclerViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.row_model_house, parent, false);
        return new RecyclerViewHolder(v);

    }

    @Override
    public void onBindViewHolder( final RecyclerViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        final House currentHouse = houses.get(position);

        holder.name_house.setText(currentHouse.getName());

        currentHouse.getDeviceCode();

        holder.allLock.setOnClickListener(this);

        holder.mProgressbar.setVisibility(View.VISIBLE);

        holder.mProgressbar.setSecondaryProgress(50000);

        holder.vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

        activity = (Activity) mContext;

        holder.cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                House clickedHouse = houses.get(position);

                String[] houseData={clickedHouse.getName(), clickedHouse.getDeviceCode()};

                openDetailCamera(houseData);

                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Devices").child(currentHouse.getDeviceCode());
        ref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                House house = dataSnapshot.getValue(House.class);

                try {

                    int totalDevices = house.getTotalDevices();

                    if (totalDevices > 0) {

                        holder.Cdevice.setImageResource(R.drawable.connected);

                        holder.txtcConnect.setText(String.valueOf(totalDevices) + " Device");

                    } else {

                        holder.Cdevice.setImageResource(R.drawable.disconnected);

                        holder.txtcConnect.setText(0 + " Device");

                    }

                }catch (Exception e) {}

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

                    boolean thief = house.isThief();

                    boolean guestDetect = house.isGuest();

                    boolean status = house.isConnect();

                    if (!guestDetect) {

                        holder.isGuest.setImageResource(R.drawable.bell_no_guest);

                        holder.guest.setText(R.string.no_guest);

                    } else {

                        holder.isGuest.setImageResource(R.drawable.bell_guest);

                        holder.guest.setText(R.string.guest);

                    }

                    if (!thief || !status){

                        holder.warning.setVisibility(View.GONE);

                    }

                    else {

                       holder.warning.setVisibility(View.VISIBLE);

                        textBlinking = ObjectAnimator.ofInt(holder.warning, "textColor", Color.RED, Color.TRANSPARENT);

                        textBlinking.setDuration(600);

                        textBlinking.setEvaluator(new ArgbEvaluator());

                        textBlinking.setRepeatCount(ValueAnimator.INFINITE);

                        textBlinking.setRepeatMode(ValueAnimator.REVERSE);

                        textBlinking.start();

                    }


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

        @SuppressLint("SimpleDateFormat")
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

        private TextView name_house, guest, locktxt, txtcConnect, warning;
        private ImageView Cdevice, allLock, isGuest, door, cam;
        ProgressBar mProgressbar;
        Vibrator vibrator;

        private RecyclerViewHolder(View itemView) {
            super(itemView);

            name_house = itemView.findViewById ( R.id.houseName );

            Cdevice = itemView.findViewById(R.id.deviceStatus);

            warning = itemView.findViewById(R.id.warning_house);

            guest = itemView.findViewById(R.id.txt_guest);

            txtcConnect = itemView.findViewById(R.id.txt_device);

            allLock = itemView.findViewById(R.id.all_lockHouse);

            cam = itemView.findViewById(R.id.stream_action);

            isGuest = itemView.findViewById(R.id.guest);

            locktxt = itemView.findViewById(R.id.allock);

            door = itemView.findViewById(R.id.doorDetail);

            mProgressbar = itemView.findViewById(R.id.progress_lock);

            mProgressbar.setSecondaryProgress(50000);

            vibrator = (Vibrator) itemView.getContext().getSystemService(Context.VIBRATOR_SERVICE);

            Typeface typeface = Typeface.createFromAsset(itemView.getContext().getAssets(), "font/Aaargh.ttf");

            name_house.setTypeface(typeface);

            name_house.setTypeface(null, Typeface.BOLD);

            auth = FirebaseAuth.getInstance();

            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

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
        public boolean onMenuItemClick(final MenuItem item) {

            if (mListener != null) {

                final int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {

                    switch (item.getItemId()) {

                        case 1:

                            mListener.onShowItemClick(position);

                            return true;

                        case 2:

                            DatabaseReference checkUser = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getEmail()
                                    .replace(".", ","));
                            checkUser.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (getContext() == null){

                                        return;

                                    }

                                    User user = dataSnapshot.getValue(User.class);

                                    String acount = user.getTypeAccount();

                                    if (acount.equals("Owner")){

                                        mListener.onDeleteItemClick(position);

                                    }

                                    else {

                                        Toast.makeText(mContext, "only owner", Toast.LENGTH_SHORT).show();

                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

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