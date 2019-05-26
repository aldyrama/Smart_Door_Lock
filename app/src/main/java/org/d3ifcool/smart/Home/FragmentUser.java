package org.d3ifcool.smart.Home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.d3ifcool.smart.Adapter.RecylerViewAdapterUserInvite;
import org.d3ifcool.smart.Data;
import org.d3ifcool.smart.Model.User;
import org.d3ifcool.smart.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import hari.bounceview.BounceView;

public class FragmentUser extends Fragment implements View.OnClickListener, RecylerViewAdapterUserInvite.OnItemClickListener{

    private static final String TAG = "User";
    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;
    private DatabaseReference reference,
            reference0, reference1,
            reference2, reference3,
            expired;
    private Dialog dialog;
    private Button sendInvite;
    private EditText phoneNumber;
    private ImageView closePopup, ic;
    private Button invite;
    private LinearLayout lr;
    private TextView expiredUser, empty_members;
    private CheckBox member;
    private String endTime;
    private RecyclerView mRecyclerViewInvite;
    private RecylerViewAdapterUserInvite mAdapterInvite;
    private ProgressBar mProgressBarInvite;
    private DatabaseReference mDatabaseRef;
    private List<User> mConnect;
    private Calendar myCalendar = Calendar.getInstance();
    private Activity mActivity;
    private SimpleDateFormat simpleDateFormat;
    private ProgressDialog pd;


    private void openDetailActivity(String[] data){

        Intent intent = new Intent(getActivity(), DetailUser.class);

        intent.putExtra("NAME_KEY",data[0]);

        intent.putExtra("EMAIL_KEY",data[1]);

        intent.putExtra("IMAGE_KEY",data[2]);

        intent.putExtra("TYPEACCOUNT_KEY",data[3]);

        intent.putExtra("STARTACCESS", data[4]);

        intent.putExtra("EXPIRED", data[5]);

        Intent i = getActivity().getIntent();
        final String deviceCode =i.getExtras().getString("DEVICECODE_KEY");

        String device = deviceCode;
        intent.putExtra("DEVICECODE_KEY", device);
        startActivity(intent);

    }


    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            Log.d("chiladd", "change" + dataSnapshot);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            mConnect.clear();

            getMember();

            mAdapterInvite.notifyDataSetChanged();

            Log.d("onChildChanged", "change" + dataSnapshot);

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            Log.d("onChildRemoved", "change" + dataSnapshot);

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            Log.d("onChildMoved", "change" + dataSnapshot);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }

    };


    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_fragment, container, false);

        auth = FirebaseAuth.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mActivity = getActivity();

        //Get method
        expireUsers();

        viewWidgrt(view);

        checkAccount();

        getMember();

        userInfo();

        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy h:mm a", Locale.US);

        pd = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);


        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialogPhoneNumber();

            }

        });

        return view;

    }


    public void checkMembers(){

        if (mAdapterInvite.getItemCount() != 0){

            empty_members.setVisibility(View.GONE);

            mRecyclerViewInvite.setVisibility(View.VISIBLE);

        }

        else {

            empty_members.setVisibility(View.VISIBLE);

            mRecyclerViewInvite.setVisibility(View.GONE);

        }

    }

    //check account login
    private void checkAccount() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getEmail()
                .replace(".",","));
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (getContext() == null) {

                    return;

                }

                User user = dataSnapshot.getValue(User.class);

                String check = user.getTypeAccount();

                if (check.equals("Owner")) {

                    invite.setVisibility(View.VISIBLE);

                    lr.setVisibility(View.VISIBLE);

                } else {

                    invite.setVisibility(View.GONE);

                    lr.setVisibility(View.GONE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

    public void showDatePickerEnd(){

         final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);

                myCalendar.set(Calendar.MONTH, monthOfYear);

                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                final TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),mTimeDataSet,
                        myCalendar.get(Calendar.HOUR_OF_DAY),myCalendar.get(Calendar.MINUTE)+5,false);

                timePickerDialog.show();

                updateLabelEnd();


            }

            final TimePickerDialog.OnTimeSetListener mTimeDataSet = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                    myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);

                    myCalendar.set(Calendar.MINUTE, minute);

                    updateLabelEnd();
                }

            };

        };


        final DatePickerDialog datePickerDialog = new DatePickerDialog(dialog.getContext(), R.style.DialogTheme, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));

        DatePicker datePicker = datePickerDialog.getDatePicker();

        myCalendar.add(Calendar.MONTH, + 1);

        datePicker.setMinDate(System.currentTimeMillis());

        datePickerDialog.show();

    }

    public void getMember(){

        try {

            Intent i = getActivity().getIntent();
            final String deviceCode =i.getExtras().getString("DEVICECODE_KEY");

            mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Devices").child(deviceCode).child("Member");
            mDatabaseRef.addChildEventListener(childEventListener);
            mDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange( DataSnapshot dataSnapshot) {

                    mConnect.clear();

                    for (DataSnapshot connectSnapshot : dataSnapshot.getChildren()) {

                        User upload = connectSnapshot.getValue(User.class);

                        endTime = upload.getExpired();

                        mConnect.add(upload);

                    }

                    mAdapterInvite.notifyDataSetChanged();

                    mProgressBarInvite.setVisibility(View.GONE);

                    checkMembers();

                }

                @Override
                public void onCancelled( DatabaseError databaseError) {

                    Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                    mProgressBarInvite.setVisibility(View.INVISIBLE);

                }

            });

        }catch (Exception e){}

    }

    private String getDateToday(){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy h:mm a");

        Log.d("typeString", "date" + dateFormat);

        Date date = new Date();

        String today= dateFormat.format(date);

        return today;

    }

    @Override
    public void onClick(View v) {

    }

    private void showDialogPhoneNumber() {

        dialog.setContentView(R.layout.invite_user_popup);

        dialog.setCanceledOnTouchOutside(false);

        phoneNumber = dialog.findViewById(R.id.email_member);

        member = dialog.findViewById(R.id.always_member);

        expiredUser = dialog.findViewById(R.id.exp);

        closePopup = dialog.findViewById(R.id.close_popup_phone);

        sendInvite = dialog.findViewById(R.id.send_invite);

        sendInvite.setOnClickListener(this);

        expiredUser.setOnClickListener(this);

        BounceView.addAnimTo(dialog);

        closePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }

        });


        member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (member.isChecked()){

                    expiredUser.setVisibility(View.GONE);

                    expiredUser.setText(" ");

                }

                else {

                    expiredUser.setVisibility(View.VISIBLE);

                    expiredUser.setText(getString(R.string.Expired));

                }

            }

        });

        expiredUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            showDatePickerEnd();

            }

        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        final String email = firebaseUser.getEmail().replace(".", ",");

        sendInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pd.setMessage("Please wait...");

                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                pd.setCanceledOnTouchOutside(false);

                pd.setCancelable(false);

                pd.show();

                final String str_invite = phoneNumber.getText().toString();

                final String str_exp = expiredUser.getText().toString();

                final String str_start = getDateToday();

               if (str_invite.isEmpty()){

                    phoneNumber.setError(getString(R.string.Email_required));

                    pd.hide();

                    return;

                }

                else if (str_exp.isEmpty()){

                    expiredUser.setError(getString(R.string.Expired_required));

                    pd.hide();

                    return;

                }


                reference = FirebaseDatabase.getInstance().getReference().child("Users").child(str_invite.replace(".", ","));
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot userSnapshot) {

                        Intent i = getActivity().getIntent();
                        final String deviceCode = i.getExtras().getString("DEVICECODE_KEY");

                        reference0 = FirebaseDatabase.getInstance().getReference().child("Devices").child(deviceCode).child("Member");
                        reference0.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot memberSnapshot) {

                                reference1 = FirebaseDatabase.getInstance().getReference().child("Users").child(str_invite.replace(".", ","))
                                            .child("Houses");

                                    reference2 = FirebaseDatabase.getInstance().getReference("Devices").child(deviceCode).child("Member");

                                    reference3 = FirebaseDatabase.getInstance().getReference("Devices").child(deviceCode).child("Member");

                                if (memberSnapshot.exists()) {

                                    phoneNumber.setError("Member " + str_invite + " Already available");

                                    pd.hide();

                                }

                                else if (userSnapshot.exists()) {

                                        User getUser = userSnapshot.getValue(User.class);

                                        String uploadId = getUser.getEmail().replace(".", ",");

                                        reference1.child(deviceCode).setValue(deviceCode);

                                        reference2.child(uploadId).setValue(getUser);

                                        reference3.child(uploadId).child("start_access").setValue(str_start);

                                        reference3.child(uploadId).child("expired").setValue(str_exp);

                                        Toast.makeText(getActivity(), str_invite + "added", Toast.LENGTH_SHORT).show();

                                        dialog.dismiss();

                                        pd.hide();

                                    }

                                else if (str_invite.replace(".", ",").equals(email)){

                                        phoneNumber.setError(str_invite + " Can not");

                                        pd.hide();

                                    }

                                else{

                                        phoneNumber.setError(str_invite + " Not found");

                                        pd.hide();


                                    }

                                }


                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                            }

                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                });


//                reference0 = FirebaseDatabase.getInstance().getReference().child("Users").child(str_invite.replace(".",","));
//                    reference0.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull final DataSnapshot userSnapshot) {
//                            Intent i = getActivity().getIntent();
//                            final String deviceCode = i.getExtras().getString("DEVICECODE_KEY");
//
//                            reference = FirebaseDatabase.getInstance().getReference().child("Devices").child(deviceCode).child("Member");
//                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot memberSnapshot) {
//
//                                    reference1 = FirebaseDatabase.getInstance().getReference().child("Users").child(str_invite.replace(".", ","))
//                                            .child("Houses");
//
//                                    reference2 = FirebaseDatabase.getInstance().getReference("Devices").child(deviceCode).child("Member");
//
//                                    reference3 = FirebaseDatabase.getInstance().getReference("Devices").child(deviceCode).child("Member");
//
//                                    Log.d("email", "message " + email);
//
//                                    Log.d("user ", " : " + userSnapshot);
//                                    Log.d("user ", " : " + memberSnapshot);
//
//                                   if (!memberSnapshot.exists() && userSnapshot.exists()){
//
//                                        User getUser = userSnapshot.getValue(User.class);
//
//                                        String uploadId = getUser.getEmail().replace(".", ",");
//
//                                        reference1.child(deviceCode).setValue(deviceCode);
//
//                                        reference2.child(uploadId).setValue(getUser);
//
//                                        reference3.child(uploadId).child("start_access").setValue(str_start);
//
//                                        reference3.child(uploadId).child("expired").setValue(str_exp);
//
//                                        Toast.makeText(getActivity(), str_invite + "added", Toast.LENGTH_SHORT).show();
//
//                                        dialog.dismiss();
//
//                                        pd.hide();
//
//                                    }
//
//                                    else if (userSnapshot.exists() && memberSnapshot.exists()){
//
//                                        phoneNumber.setError("Member " + str_invite + " Already available");
//
//                                        pd.hide();
//
//                                    }
//
//                                    else if (str_invite.replace(".", ",").equals(email)){
//
//                                        phoneNumber.setError(str_invite + " Can not");
//
//                                        pd.hide();
//
//                                    }
//
//                                    else {
//
//                                        pd.hide();
//
//                                        phoneNumber.setError(str_invite + " Not found");
//
//                                    }
//
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                }
//
//                            });
//
//
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            pd.hide();
//
//                            phoneNumber.setError(str_invite + " Not found");
//
//                            Toast.makeText(getActivity(), str_invite + " Not found ", Toast.LENGTH_SHORT).show();
//
//                        }
//
//                    });
//
                }
//
        });

    }


    private void updateLabelEnd() {

        String myFormat = "dd/MM/yyyy h:mm a";

        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        sdf.setLenient(false);

        expiredUser.setText(sdf.format(myCalendar.getTime()));

    }

    private void userInfo(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getEmail().replace(".", ","));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (getContext() == null){

                    return;

                }

                User user = dataSnapshot.getValue(User.class);

                Data.usernameConnect = user.getFullname();

            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

            }

        });

    }


    public void viewWidgrt(View view){

        dialog = new Dialog(getActivity());

        phoneNumber = view.findViewById(R.id.email_member);

        invite = view.findViewById(R.id.invite_btn_user);

        invite.setStateListAnimator(null);

        sendInvite = view.findViewById(R.id.send_invite);

        closePopup = view.findViewById(R.id.close_popup_phone);

        ic = view.findViewById(R.id.ic_exp);

        expiredUser = view.findViewById(R.id.exp);

        member = view.findViewById(R.id.always_member);

        lr = view.findViewById(R.id.lr_invite);

        empty_members = view.findViewById(R.id.empty_member);

        mConnect = new ArrayList<>();

        mRecyclerViewInvite = view.findViewById(R.id.recycler_view_invite);

        mRecyclerViewInvite.setHasFixedSize(true);

        mRecyclerViewInvite.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapterInvite = new RecylerViewAdapterUserInvite (getActivity(), mConnect);

        mAdapterInvite.setOnItemClickListener(this);

        mRecyclerViewInvite.setAdapter(mAdapterInvite);

        mProgressBarInvite = view.findViewById(R.id.myDataUserLoaderProgressBar);

        mProgressBarInvite.setVisibility(View.VISIBLE);

    }

    @Override
    public void onItemClick(int position) {

        User clickedUser= mConnect.get(position);

        String[] userData={clickedUser.getFullname(), clickedUser.getEmail(), clickedUser.getImageurl(),
                clickedUser.getTypeAccount(), clickedUser.getStart_access(), clickedUser.getExpired()};

        openDetailActivity(userData);

    }

    @Override
    public void onShowItemClick(int position) {

        User clickedUser= mConnect.get(position);

        String[] userData={clickedUser.getFullname(), clickedUser.getEmail(), clickedUser.getImageurl(),
                clickedUser.getTypeAccount(), clickedUser.getStart_access(), clickedUser.getExpired()};

        openDetailActivity(userData);

    }

    @Override
    public void onDeleteItemClick(int position) {
        User selectedItem = mConnect.get(position);
        final String selectedKey = selectedItem.getEmail().replace(".", ",");

        Intent i = getActivity().getIntent();
        final String deviceCode = i.getExtras().getString("DEVICECODE_KEY");

        reference = FirebaseDatabase.getInstance().getReference().child("Devices").child(deviceCode).child("Member");

        reference0 = FirebaseDatabase.getInstance().getReference().child("Users").child(selectedKey).child("Houses");
        reference0.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                for(DataSnapshot data : dataSnapshot.getChildren()) {

                    if(data.getValue(String.class).equals(deviceCode)){

                        reference0.child(data.getKey()).removeValue();

                    }

                }

                reference.child(selectedKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity(), "Item deleted", Toast.LENGTH_SHORT).show();

                    }

                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

    public void expireUsers(){

        Intent i = getActivity().getIntent();
        final String deviceCode =i.getExtras().getString("DEVICECODE_KEY");

        expired = FirebaseDatabase.getInstance().getReference();
        expired.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                for (DataSnapshot expiredSnapshot : dataSnapshot.child("Devices").child(deviceCode).child("Member").getChildren()) {

                    final String email = expiredSnapshot.getKey();

                    String exp = expiredSnapshot.child("expired").getValue(String.class);

                    Log.d("exp", "onDataChange: " + exp);


                    try {

                        String now = getDateToday();

                        @SuppressLint("SimpleDateFormat")
                        Date dateNow=new SimpleDateFormat(getString(R.string.formatDate)).parse(now);

                        @SuppressLint("SimpleDateFormat")
                        Date dateExp=new SimpleDateFormat(getString(R.string.formatDate)).parse(exp);

                        long millisNow = dateNow.getTime();

                        long millisExp = dateExp.getTime();

                        Log.d("datatime", "test" + millisExp);

                        if (millisExp <= millisNow) {

                            expiredSnapshot.getRef().removeValue();

                            dataSnapshot.child("Users").child(email).child("Houses").child(deviceCode).getRef().removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            Log.d("dataMember", "data" + dataSnapshot);

                                        }

                                    });

                        }

                    } catch (ParseException e) {

                        e.printStackTrace();

                    }

                }

            }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

}


