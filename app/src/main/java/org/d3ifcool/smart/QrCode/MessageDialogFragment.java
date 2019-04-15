package org.d3ifcool.smart.QrCode;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.d3ifcool.smart.Home.MainActivity;
import org.d3ifcool.smart.Model.House;
import org.d3ifcool.smart.R;

public class MessageDialogFragment extends DialogFragment {

    public interface MessageDialogListener {

        public void onDialogPositiveClick(DialogFragment dialog);

    }

    private String mTitle;
    private String mMessage;
    private MessageDialogListener mListener;
    private DatabaseReference reference, reference0, reference1, reference2;
    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;
    MediaPlayer mediaPlayer;

    EditText inputHouse;



    public void onCreate(Bundle state) {
        super.onCreate(state);
        setRetainInstance(true);

        auth = FirebaseAuth.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    public static MessageDialogFragment newInstance(String title, String message, MessageDialogListener listener) {

        MessageDialogFragment fragment = new MessageDialogFragment();

        fragment.mTitle = title;

        fragment.mMessage = message;

        fragment.mListener = listener;

        return fragment;

    }

    public void play(){

        if (mediaPlayer == null){

            mediaPlayer = MediaPlayer.create(getContext(), R.raw.barcode_sound);

        }

        mediaPlayer.start();

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("Enter house name")

                .setTitle(mTitle)

                .setIcon(R.drawable.logo_lock);

        play();

        builder.setCancelable(false);

        inputHouse = new EditText(getContext());

//        inputHouse.setHint("House name required");

        inputHouse.setPadding(30, 0, 0, 30);

        builder.setView(inputHouse);

        builder.setPositiveButton("add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                final String txt = inputHouse.getText().toString();

                if (mListener != null) {

                    mListener.onDialogPositiveClick(MessageDialogFragment.this);

                }

                if (txt.length() <= 0){

//                    inputHouse.setError("House name required");

                    Toast.makeText(getContext(), "House name required", Toast.LENGTH_SHORT).show();

                    return;

                }

                final House house = new House(mMessage);

                try {
                    reference = FirebaseDatabase.getInstance().getReference().child("Devices").child(mMessage);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot deviceSnapshot) {

                            House getHouses = deviceSnapshot.getValue(House.class);

                            reference1 = FirebaseDatabase.getInstance().getReference().child("Devices").child("ListDevices").child(mMessage);

                            reference1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot listSnapshot) {

                                    reference0 = FirebaseDatabase.getInstance().getReference().child("Users").
                                            child(firebaseUser.getEmail().replace(".", ",")).child("Houses");

                                    reference2 = FirebaseDatabase.getInstance().getReference().child("Devices").child(mMessage);

                                    if (listSnapshot.exists() && deviceSnapshot.exists()){

                                        Toast.makeText(getContext(), "Device " + mMessage + " Already available", Toast.LENGTH_SHORT).show();

                                    }

                                    else if (deviceSnapshot.exists()) {

                                        String uploadId = reference.push().getKey();

                                        reference0.child(mMessage).setValue(mMessage);

                                        reference1.setValue(house);

                                        reference1.child("deviceCode").setValue(mMessage);

                                        reference1.child("name").setValue(txt);

                                        reference2.child("deviceCode").setValue(mMessage);

                                        reference2.child("name").setValue(txt);

                                        startActivity(new Intent(getContext(), MainActivity.class));

                                        Toast.makeText(getContext(), txt + " added", Toast.LENGTH_SHORT).show();

                                        return;

                                    } else {

                                        Toast.makeText(getContext(), "Device " + mMessage + " Not found", Toast.LENGTH_SHORT).show();

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }

                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });

                }catch (Exception e){

                    Toast.makeText(getContext(), "Device " + mMessage + " Not found", Toast.LENGTH_SHORT).show();

                }

            }

        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                startActivity(new Intent(getContext(), MainActivity.class));

            }

        });

        builder.setCancelable(false);

        return builder.create();

    }

}