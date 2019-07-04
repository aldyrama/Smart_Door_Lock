package org.d3ifcool.smart.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
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
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.d3ifcool.smart.Home.MemberDoor;
import org.d3ifcool.smart.Model.Camera;
import org.d3ifcool.smart.Model.User;
import org.d3ifcool.smart.R;
import org.d3ifcool.smart.Setting.SettingActivity;

import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static java.security.AccessController.getContext;
import static org.d3ifcool.smart.Data.deviceCode;


public class RecyclerAdapterMember extends RecyclerView.Adapter<RecyclerAdapterMember.MyViewHolder> {
    private Context mContext;
    private List<User> mData;
    private OnItemClickListener mListener;
    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;
    private SharedPreferences prefs;
    private Boolean check;
    private SharedPreferences.Editor editor;
    public static final String GLOBAL_SHARED_PREFS = "org.d3ifcool.smart";
    public static final String CHEXBOX = "member";


    public RecyclerAdapterMember(Activity memberDoor, List<User> mConnect) {

        mContext = memberDoor;

        mData = mConnect;

    }


//    @Override
//    public int getItemViewType(int position) {
//
//        final int size = mData.size() - 1;
//
//        if (size == 0)
//
//            return ItemType.ATOM;
//
//        else if (position == 0)
//
//            return ItemType.START;
//
//        else if (position == size)
//
//            return ItemType.END;
//
//        else return ItemType.NORMAL;
//    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_model_member_door, parent, false), i);


    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final User user = mData.get(position);

        holder.name.setText(user.getFullname());

        holder.email.setText(user.getEmail());

        holder.addMember.setChecked(user.isChecked());

        Picasso.with(mContext)
                .load(user.getImageurl())
                .placeholder(R.drawable.user_fix)
                .fit()
                .centerCrop()
                .into(holder.photo);

        final Intent i = ((Activity) mContext).getIntent();
        final String deviceCode =i.getExtras().getString("DEVICE_CODE");
        final String pin = i.getExtras().getString("PIN");

        holder.addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.addMember.isChecked()){

                    Intent i = ((Activity) mContext).getIntent();
                    final String deviceCode =i.getExtras().getString("DEVICE_CODE");
                    final String pin = i.getExtras().getString("PIN");

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                            .child("Devices")
                            .child(deviceCode)
                            .child("Doors")
                            .child(pin)
                            .child("Member");

                    User clickedUser = mData.get(position);

//                    user.setChecked(true);

                    String email = clickedUser.getEmail().replace(".", ",");

                    ref.child(email).setValue(clickedUser);

                }

                else {

                    Intent i = ((Activity) mContext).getIntent();
                    final String deviceCode =i.getExtras().getString("DEVICE_CODE");
                    final String pin = i.getExtras().getString("PIN");

                    User clickedUser = mData.get(position);

//                    user.setChecked(false);

                    final String email = clickedUser.getEmail().replace(".", ",");

                    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                            .child("Devices")
                            .child(deviceCode)
                            .child("Doors")
                            .child(pin)
                            .child("Member");

                            ref.child(email).getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

//                                    Toast.makeText(v.getContext(), "", Toast.LENGTH_SHORT).show();

                                }
                            });

                }
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getEmail()
                .replace(".",","));
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                String account = user.getTypeAccount();
                if (account.equals("Owner")){

                    holder.addMember.setEnabled(true);

                }

                else {

                    holder.addMember.setEnabled(false);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {

        return mData.size();

    }

    public class ItemType {

        public final static int NORMAL = 0;

        public final static int HEADER = 1;

        public final static int FOOTER = 2;

        public final static int START = 4;

        public final static int END = 8;

        public final static int ATOM = 16;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {

        mListener = listener;
    }

    public interface OnItemClickListener {

        void onItemClick(int position);

        void onShowItemClick(int position);

        void onDeleteItemClick(int position);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        private TextView name, email;
        private ImageView photo;
        private CheckBox addMember;
        public static final String GLOBAL_SHARED_PREFS = "org.d3ifcool.smart";

        public MyViewHolder(@NonNull View itemView, int type) {
            super(itemView);

            name = itemView.findViewById(R.id.username);

            photo = itemView.findViewById(R.id.imageMember);

            email = itemView.findViewById(R.id.email_member);

            addMember = itemView.findViewById(R.id.add_member_door);

            auth = FirebaseAuth.getInstance();

            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            prefs = mContext.getSharedPreferences(RecyclerAdapterMember.GLOBAL_SHARED_PREFS, MODE_PRIVATE);

            editor = prefs.edit();

//            addMember.setChecked(prefs.getBoolean(CHEXBOX, true));
//
//            addMember.setChecked(true);

            itemView.setOnClickListener(this);

            itemView.setOnCreateContextMenuListener(this);


        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            Intent i = ((Activity) mContext).getIntent();
            final String deviceCode =i.getExtras().getString("DEVICECODE_KEY");
            if (mListener != null) {

                final int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {

                    switch (item.getItemId()) {

                        case 1:

                            mListener.onShowItemClick(position);

                            return true;

                        case 2:

                            DatabaseReference checkUser = FirebaseDatabase.getInstance().getReference().child("Devices").child(deviceCode).
                                    child("Owner");
                            checkUser.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (getContext() == null) {

                                        return;

                                    }

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

    }

}

