package org.d3ifcool.smart.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.d3ifcool.smart.Model.Camera;
import org.d3ifcool.smart.R;

import java.util.List;

import static java.security.AccessController.getContext;


public class RecyclerViewAdapterCam extends RecyclerView.Adapter<RecyclerViewAdapterCam.MyViewHolder> {
    private Context mContext;
    private List<Camera> mData;
    private OnItemClickListener mListener;
    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;

    public RecyclerViewAdapterCam(Context mainCamera, List<Camera> mCamera) {

        this.mContext = mainCamera;

        this.mData = mCamera;
    }

    @Override
    public int getItemViewType(int position) {

        final int size = mData.size() - 1;

        if (size == 0)

            return ItemType.ATOM;

        else if (position == 0)

            return ItemType.START;

        else if (position == size)

            return ItemType.END;

        else return ItemType.NORMAL;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_model_camera, parent, false), i);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Camera currentCam = mData.get(position);

        holder.name.setText(currentCam.getName());

        holder.ip.setText(currentCam.getIpAddress());


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

        private TextView name, ip;
        private ImageView photo, status;

        public MyViewHolder(@NonNull View itemView, int type) {
            super(itemView);

            name = itemView.findViewById(R.id.cameName);

            ip = itemView.findViewById(R.id.cameIP);

            auth = FirebaseAuth.getInstance();

            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            itemView.setOnClickListener(this);

            itemView.setOnCreateContextMenuListener(this);

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

