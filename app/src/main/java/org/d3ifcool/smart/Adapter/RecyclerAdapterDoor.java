package org.d3ifcool.smart.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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

import org.d3ifcool.smart.Model.Door;
import org.d3ifcool.smart.Model.House;
import org.d3ifcool.smart.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import xyz.schwaab.avvylib.Animation;
import xyz.schwaab.avvylib.AvatarView;

public class RecyclerAdapterDoor extends RecyclerView.Adapter<RecyclerAdapterDoor.RecyclerViewHolder> implements View.OnClickListener {
    private Context mContext;
    private List<Door> door;
    private OnItemClickListener mListener;
    FirebaseUser firebaseUser;
    FirebaseAuth auth;
    private Uri mImageUri;
    StorageReference storageRef;
    ProgressDialog pd;
    int status = 0;


    public RecyclerAdapterDoor (Context context, List<Door> uploads) {
        mContext = context;
        door = uploads;
    }

    @Override
    public RecyclerAdapterDoor.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.row_model_door, parent, false);
        return new RecyclerViewHolder(v);
    }



    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, int position) {
        final Door currentDoor = door.get(position);
        holder.name_door.setText(currentDoor.getDoorName());
//        holder.dateTextView.setText(getDateToday());
        holder.lockImageView.setOnClickListener(this);
        holder.mProgressbar.setVisibility(View.VISIBLE);


        final String url = "https://firebasestorage.googleapis.com/v0/b/smartdoor-7d0e6.appspot.com/o/lock_door.png?alt=media&token=2a903126-fc6e-4f87-b62c-9ccb7e9f5383";
        final String url1 = "https://firebasestorage.googleapis.com/v0/b/smartdoor-7d0e6.appspot.com/o/unlock_door.png?alt=media&token=15c98219-2c31-49db-9338-968e35cced71";

        Intent i = ((Activity) mContext).getIntent();
        final String name =i.getExtras().getString("NAME_KEY");
        final String deviceCode =i.getExtras().getString("DEVICECODE_KEY");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Devices").child(deviceCode).child("Doors")
                .child(currentDoor.getDoorName());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Door door = dataSnapshot.getValue(Door.class);
                int lock = door.getDoorLock();

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
                    holder.status.setText("Locket");

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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        holder.lockImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = ((Activity) mContext).getIntent();
                String name =i.getExtras().getString("NAME_KEY");
                String deviceCode =i.getExtras().getString("DEVICECODE_KEY");
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Devices").child(deviceCode).child("Doors")
                        .child(currentDoor.getDoorName());

                if (status == 0){
                    status = 1;

                    reference.child("doorLock").setValue(status);

//                    holder.lockImageView.setAnimating(true);
//                    holder.lockImageView.setBorderThickness(18); //Currently px
//                    holder.lockImageView.setHighlightBorderColor(Color.GREEN);
//                    holder.lockImageView.setHighlightBorderColorEnd(Color.CYAN);
//                    holder.lockImageView.setNumberOfArches(0);
//                    holder.lockImageView.setImageResource(R.drawable.lock_door);
//                    holder.lockImageView.setTotalArchesDegreeArea(80);
//                    mProgressbar.setVisibility(View.INVISIBLE);


                }

                else {
                    status = 0;
                    reference.child("doorLock").setValue(status);

//                    mProgressbar.setVisibility(View.INVISIBLE);

//                    holder.lockImageView.setAnimating(true);
//                    holder.lockImageView.setBorderThickness(18); //Currently px
//                    holder.lockImageView.setHighlightBorderColor(Color.GREEN);
//                    holder.lockImageView.setHighlightBorderColorEnd(Color.CYAN);
//                    holder.lockImageView.setNumberOfArches(0);
//                    holder.lockImageView.setImageResource(R.drawable.unlock_door);
//                    holder.lockImageView.setTotalArchesDegreeArea(80);

                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return door.size();
    }

    @Override
    public void onClick(View v) {

    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        public TextView name_door,doorView, dateTextView, status;
        public ImageView lockImageView;
        public AnimationDrawable imagesAnimation;
        ProgressBar mProgressbar;


        public RecyclerViewHolder(View itemView) {
            super(itemView);
            name_door =itemView.findViewById ( R.id.doorName );
//            dateTextView = itemView.findViewById(R.id.date_door);
            lockImageView = itemView.findViewById(R.id.lockDoor);
            mProgressbar = itemView.findViewById(R.id.prog_lock);
            status = itemView.findViewById(R.id.status_lock);
            imagesAnimation = (AnimationDrawable) lockImageView.getBackground();

//
//            Typeface typeface = Typeface.createFromAsset(itemView.getContext().getAssets(), "font/Fontspring_DEMO_microsquare_bold.ttf");
//            name_door.setTypeface(typeface);

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

//    public void setOnItemClickListener(FragmentActivity activity) {
//        mListener = (OnItemClickListener) activity;
//    }

    private String getDateToday(){
        DateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd");
        Date date=new Date();
        String today= dateFormat.format(date);
        return today;
    }
}
