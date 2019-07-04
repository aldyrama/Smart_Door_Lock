package org.d3ifcool.smart.Adapter;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.dinuscxj.progressbar.CircleProgressBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.d3ifcool.smart.Home.MemberDoor;
import org.d3ifcool.smart.Model.Door;
import org.d3ifcool.smart.Model.House;
import org.d3ifcool.smart.Model.User;
import org.d3ifcool.smart.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import hari.bounceview.BounceView;

import static java.security.AccessController.getContext;


public class RecyclerAdapterDoor extends RecyclerView.Adapter<RecyclerAdapterDoor.RecyclerViewHolder>
        implements View.OnClickListener, RecyclerAdapterHouse.DoorInterfaces, CircleProgressBar.ProgressFormatter {
    private Context mContext;
    private List<Door> mDoor;
    private List<House> mHouse;
    private OnItemClickListener mListener;
    private FirebaseUser firebaseUser;
    private ObjectAnimator textBlinking;
    int status;
    private boolean statusDevice;
    private FirebaseAuth auth;
    private static final String DEFAULT_PATTERN = "%d%%";

    private void openDetailActivity(String[] data) {

        Intent i = ((Activity) mContext).getIntent();
        final String deviceCode =i.getExtras().getString("DEVICECODE_KEY");

        i = new Intent(mContext, MemberDoor.class);

        i.putExtra("PIN", data[0]);

        i.putExtra("DEVICE_CODE",deviceCode);

        mContext.startActivity(i);

    }

    public RecyclerAdapterDoor (FragmentActivity activity, List<Door> uploads) {

        mContext = activity;

        mDoor = uploads;

    }

    @Override
    public RecyclerAdapterDoor.RecyclerViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.row_model_door, parent, false);

        return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int position) {

        final Door currentDoor = mDoor.get(position);

        holder.name_door.setText(currentDoor.getDoorName());

        holder.lockImageView.setOnClickListener(this);

        holder.mProgressbar.setVisibility(View.VISIBLE);

        holder.circleProgressBar.setProgressFormatter(null);

        final String time = holder.getTimeToday();

        final String date = getToday();

        final Activity activity = (Activity) mContext;

        long millis = 1119193190;

        SimpleDateFormat sdf =  new SimpleDateFormat("dd/MM/yyyy h:mm a");

        Date resultdate = new Date(millis);

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

                try {

                    holder.statusdoor.setText(door.getStatus());

                    int lock = door.getDoorLock();

                    status = lock;

                    int oVoltage = door.getVoltage();

                    String nameDoor = door.getDoorName();

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

                    if (oVoltage >= 689000 ){

                        holder.devicePower.setImageResource(R.drawable.b100);

                        holder.textIndicator.setText("100%");

                        holder.low.setVisibility(View.GONE);

                    }

                    else if (oVoltage > 559000 && oVoltage < 689000){

                        holder.devicePower.setImageResource(R.drawable.b75);

                        holder.textIndicator.setText("75%");

                        holder.low.setVisibility(View.GONE);

                    }

                    else if (oVoltage > 420000 && oVoltage < 559000){

                        holder.devicePower.setImageResource(R.drawable.b50);

                        holder.textIndicator.setText("50%");

                        holder.low.setVisibility(View.GONE);

                    }

                    else if (oVoltage > 190000 && oVoltage < 420000){

                        holder.devicePower.setImageResource(R.drawable.b25);

                        holder.textIndicator.setText("25%");

                        holder.low.setVisibility(View.VISIBLE);

                        textBlinking = ObjectAnimator.ofInt(holder.low, "textColor", Color.RED, Color.TRANSPARENT);

                        textBlinking.setDuration(600);

                        textBlinking.setEvaluator(new ArgbEvaluator());

                        textBlinking.setRepeatCount(ValueAnimator.INFINITE);

                        textBlinking.setRepeatMode(ValueAnimator.REVERSE);

                        textBlinking.start();


                    }

                    else {

                        holder.devicePower.setImageResource(R.drawable.b0);

                        holder.textIndicator.setText("0%");

                        holder.low.setText("No Battery");

                        holder.low.setVisibility(View.VISIBLE);

                        textBlinking = ObjectAnimator.ofInt(holder.low, "textColor", Color.RED, Color.TRANSPARENT);

                        textBlinking.setDuration(600);

                        textBlinking.setEvaluator(new ArgbEvaluator());

                        textBlinking.setRepeatCount(ValueAnimator.INFINITE);

                        textBlinking.setRepeatMode(ValueAnimator.REVERSE);

                        textBlinking.start();

                    }

                }catch (Exception e){

                    e.printStackTrace();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        holder.member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Door clickedDoor = mDoor.get(position);

                String[] houseData={clickedDoor.getDoorPin()};

                openDetailActivity(houseData);

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
                        try {
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

                        String now = getDateTodayHistory();

                        Date dateNow = new SimpleDateFormat("dd/M/yyyy HH:mm:ss").parse(now);

                        long millisNow = dateNow.getTime();

//                        String uploadId = reference1.push().getKey();

                        String system = door.getStatus();

                        String nameDoor = door.getDoorName();

                        if (!isInternetOn()){

                            holder.textConnection.setVisibility(View.VISIBLE);

                            Toast.makeText(v.getContext(), "no internet connection!", Toast.LENGTH_SHORT).show();

                        }

                        else if (status == 0 && isInternetOn()) {

                            holder.textConnection.setVisibility(View.GONE);

                            holder.mProgressbar.setVisibility(View.VISIBLE);

                            status = 1;

                            lock = "Unlock by app";

                            reference.child("doorLock").setValue(status);

                            reference1.child(String.valueOf(millisNow)).setValue(getUser);

                            reference1.child(String.valueOf(millisNow)).child("lock").setValue(lock);

                            reference1.child(String.valueOf(millisNow)).child("lockImage").setValue(url1);

                            reference1.child(String.valueOf(millisNow)).child("door").setValue(currentDoor.getDoorName());

                            reference1.child(String.valueOf(millisNow)).child("time").setValue(time);

                            reference1.child(String.valueOf(millisNow)).child("date").setValue(date);


                        } else {

                            holder.mProgressbar.setVisibility(View.VISIBLE);

                            status = 0;

                            lock = "Lock by app";

                            reference.child("doorLock").setValue(status);

                            reference1.child(String.valueOf(millisNow)).setValue(getUser);

                            reference1.child(String.valueOf(millisNow)).child("lock").setValue(lock);

                            reference1.child(String.valueOf(millisNow)).child("lockImage").setValue(url);

                            reference1.child(String.valueOf(millisNow)).child("door").setValue(currentDoor.getDoorName());

                            reference1.child(String.valueOf(millisNow)).child("time").setValue(time);

                            reference1.child(String.valueOf(millisNow)).child("date").setValue(date);

                        }

                    }catch (ParseException e) {
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });

            }

        });

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Devices").child(deviceCode).child("Doors")
                .child(currentDoor.getDoorPin());
        ref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Door door = dataSnapshot.getValue(Door.class);

                try {

                    String now = getDateToday();

                    Log.d("total", "now" + now);

                    String statusDevice = door.getConnect();

                    Log.d("total", "updateDate" + statusDevice);

                    @SuppressLint("SimpleDateFormat")
                    Date dateNow = new SimpleDateFormat("dd/M/yyyy HH:mm").parse(now);

                    @SuppressLint("SimpleDateFormat")
                    Date dateCont = new SimpleDateFormat("dd/M/yyyy HH:mm").parse(statusDevice);

                    long millisNow = dateNow.getTime();

                    long millisCont = dateCont.getTime();

                    long totalMillis = (millisNow - millisCont);

                    Log.d("total", "millis" + totalMillis);

                    if (totalMillis >= 24000) {

                        holder.device.setImageResource(R.drawable.ic_not_connect);

                        holder.lockImageView.setEnabled(false);


                    }

                    else {

                        holder.device.setImageResource(R.drawable.ic_connect);

                        holder.lockImageView.setEnabled(true);

                    }

                } catch (

                        ParseException e) {

                    e.printStackTrace();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        if (!isInternetOn()){

            holder.textConnection.setVisibility(View.VISIBLE);

        }

        else {

            holder.textConnection.setVisibility(View.GONE);

        }

    }

    private String getToday(){

        DateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy" );

        Date date = new Date();

        String today= dateFormat.format(date);

        return today;

    }


    private String getDateToday() {

        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat=new SimpleDateFormat("dd/M/yyyy HH:mm");

        Date date = new Date();

        String today= dateFormat.format(date);

        return today;

    }

    private String getDateTodayHistory() {

        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat=new SimpleDateFormat("dd/M/yyyy HH:mm:ss");

        Date date = new Date();

        String today= dateFormat.format(date);

        return today;

    }

    public final boolean isInternetOn()
    {

        ConnectivityManager connec = (ConnectivityManager)

                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        // ARE WE CONNECTED TO THE NET
        if ( connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||

                connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED )

        {

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

    @SuppressLint("DefaultLocale")
    @Override
    public CharSequence format(int progress, int max) {
        return String.format(DEFAULT_PATTERN, (int) ((float) progress / (float) max * 100));

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

        private TextView name_door, statusdoor, textConnection, status, textIndicator, low;
        private ImageView lockImageView, device, devicePower, member;
        private AnimationDrawable imagesAnimation;
        private CircleProgressBar circleProgressBar;
        private ProgressBar mProgressbar;
        private String replaceEmail;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            name_door =itemView.findViewById ( R.id.doorName );

            statusdoor = itemView.findViewById(R.id.statustDoor);

            textIndicator = itemView.findViewById(R.id.txt_power);

            textConnection = itemView.findViewById(R.id.text_status);

            lockImageView = itemView.findViewById(R.id.lockDoor);

            mProgressbar = itemView.findViewById(R.id.prog_lock);

            status = itemView.findViewById(R.id.status_lock);

            member = itemView.findViewById(R.id.door_member);

            imagesAnimation = (AnimationDrawable) lockImageView.getBackground();

            auth = FirebaseAuth.getInstance();

            devicePower = itemView.findViewById(R.id.battery_indicator);

            device = itemView.findViewById(R.id.connect_device);

            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            circleProgressBar = itemView.findViewById(R.id.progress_autolock);

            replaceEmail = firebaseUser.getEmail().replace(".", ",");

            low = itemView.findViewById(R.id.battery);

            itemView.setOnClickListener(this);

            itemView.setOnCreateContextMenuListener(this);

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

            Intent i = ((Activity) mContext).getIntent();
            final String name =i.getExtras().getString("NAME_KEY");
            final String deviceCode =i.getExtras().getString("DEVICECODE_KEY");

            if (mListener != null) {

                final int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {

                    switch (item.getItemId()) {

                        case 2:

                            mListener.onShowItemClick(position);

                            return true;

                        case 1:

                            DatabaseReference checkUser = FirebaseDatabase.getInstance().getReference().child("Devices").child(deviceCode).
                                    child("Owner");
                            checkUser.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (getContext() == null){

                                        return;

                                    }

//                                    User user = dataSnapshot.getValue(User.class);
//
//                                    String account = user.getTypeAccount();

                                    String email = (String) dataSnapshot.getValue();
                                    Log.d("email", "data" + email);
                                    if (firebaseUser.getEmail().replace(".", ",").equals(email)) {

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

    @Override
    public void refresh() {

    }

    @Override
    public void getDevice(House connect) {

        statusDevice = connect.isConnect();

    }

}
