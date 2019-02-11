package org.d3ifcool.smart.Family;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import org.d3ifcool.smart.Internet.CheckConnection;
import org.d3ifcool.smart.Model.User;
import org.d3ifcool.smart.R;

import at.markushi.ui.CircleButton;

public class FragmentUser extends Fragment implements View.OnClickListener {

    private static final String TAG = "User";

    User mUser;
    DatabaseReference reference;
    FirebaseUser firebaseUser;


    Dialog dialog;
    Button sendInvite;
    EditText phoneNumber;
    CountryCodePicker phoneCode;
    ImageView closePopup;
    CircleButton invite;
    private CheckConnection checkConnection;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_fragment,container,false);

        dialog = new Dialog(getActivity());
        phoneNumber =  (EditText) view.findViewById(R.id.username_member);
        invite = (CircleButton) view.findViewById(R.id.invite);
        sendInvite = (Button) view.findViewById(R.id.send_invite);
        closePopup = (ImageView) view.findViewById(R.id.close_popup_phone);



//        reference = FirebaseDatabase.getInstance().getReference().child("Connected").child("Connect_"+mUser.getUsername());


        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialogPhoneNumber();

            }
        });

        return view;
    }

    private TextWatcher textWatcherPhoneNumber = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String phoneNumberInput = phoneNumber.getText().toString().trim();

            sendInvite.setEnabled(!phoneNumberInput.isEmpty());

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    public void messageDialog(String text) {

        final android.app.AlertDialog.Builder message = new android.app.AlertDialog.Builder(getActivity());
        message.setTitle("Info");
        message.setMessage(text);
        message.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        message.create().show();
    }


    private void showDialogPhoneNumber(){
        dialog.setContentView(R.layout.invite_user_popup);
        phoneNumber = (EditText) dialog.findViewById(R.id.username_member);
        closePopup = (ImageView) dialog.findViewById(R.id.close_popup_phone);
        sendInvite = (Button) dialog.findViewById(R.id.send_invite);
        sendInvite.setOnClickListener(this);

        phoneNumber.addTextChangedListener(textWatcherPhoneNumber);

        closePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    @Override
    public void onClick(View v) {
        }

    }


