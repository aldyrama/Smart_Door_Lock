package org.d3ifcool.smart.Home;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.hbb20.CountryCodePicker;

import org.d3ifcool.smart.Adapter.RecylerViewAdapterUserInvite;
import org.d3ifcool.smart.Data;
import org.d3ifcool.smart.Internet.CheckConnection;
import org.d3ifcool.smart.Model.Connect;
import org.d3ifcool.smart.Model.House;
import org.d3ifcool.smart.Model.User;
import org.d3ifcool.smart.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import hari.bounceview.BounceView;

public class FragmentUser extends Fragment implements View.OnClickListener, RecylerViewAdapterUserInvite.OnItemClickListener{

    private static final String TAG = "User";

    User mUser;
    View v;
    DatabaseReference mDatabase;
    FirebaseUser firebaseUser;
    FirebaseAuth auth;
    DatabaseReference reference, reference0, reference1, reference2, reference3, reference4;

    Dialog dialog;
    Button sendInvite;
    EditText phoneNumber;
    CountryCodePicker phoneCode;
    ImageView closePopup;
    Button invite;
    ProgressDialog pd;
    LinearLayout lr;
    TextView expiredUser, empty_members, start;
    CheckBox member;
    private RecyclerView mRecyclerViewInvite;
    private RecylerViewAdapterUserInvite mAdapterInvite;
    private ProgressBar mProgressBarInvite;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private List<User> mConnect;
    private List<House> mDevice;
    private ArrayList<Connect> mData;
    private boolean isAddUser;
    private CheckConnection checkConnection;
    Calendar myCalendar = Calendar.getInstance();
    int age_member;
    private long timestamp;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_fragment, container, false);

        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        //Get method
        viewWidgrt(view);
        checkAccount();
        getMember();
        userInfo();
//        handleExpired();

        Intent i = getActivity().getIntent();
        final String deviceCode =i.getExtras().getString("DEVICECODE_KEY");

//
//        mRecyclerViewInvite.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//
//                if (dy < 0){
//                    invite.show();
//
//                }
//
//                else if (dy > 0){
//                    invite.hide();
//                }
//
//                else if (dy == 0){
//                    invite.show();
//                }
//            }
//        });


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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").
                child(firebaseUser.getEmail().replace(".",","));
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

    public void showDatePickerStart(){
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                age_member = Integer.parseInt((Integer.toString(calculateAge(myCalendar.getTimeInMillis()))));

                updateLabelStart();

            }

            int calculateAge(long date){
                Calendar dob = Calendar.getInstance();
                dob.setTimeInMillis(date);
                Calendar today = Calendar.getInstance();
                int age = today.get(Calendar.MONTH) - dob.get(Calendar.MONTH);
                if(today.get(Calendar.HOUR_OF_DAY) < dob.get(Calendar.HOUR_OF_DAY)){
                    age--;
                }
                return age;
            }

        };

        new DatePickerDialog(dialog.getContext(), R.style.DialogTheme, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH))
                .show();

    }

    public void showDatePickerEnd(){
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                age_member = Integer.parseInt((Integer.toString(calculateAge(myCalendar.getTimeInMillis()))));

                updateLabelEnd();

            }

            int calculateAge(long date){
                Calendar dob = Calendar.getInstance();
                dob.setTimeInMillis(date);
                Calendar today = Calendar.getInstance();
                int age = today.get(Calendar.MONTH) - dob.get(Calendar.MONTH);
                if(today.get(Calendar.HOUR_OF_DAY) < dob.get(Calendar.HOUR_OF_DAY)){
                    age--;
                }
                return age;
            }

        };

        new DatePickerDialog(dialog.getContext(), R.style.DialogTheme, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH))
                .show();

    }


    public void getMember(){
        Intent i = getActivity().getIntent();
        final String deviceCode =i.getExtras().getString("DEVICECODE_KEY");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Devices").child(deviceCode).child("Member");
        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mConnect.clear();
                for (DataSnapshot connectSnapshot : dataSnapshot.getChildren()) {
                    User upload = connectSnapshot.getValue(User.class);


                    mConnect.add(upload);

//                    String time = getDateToday();
//                    if (time == upload.getExpired()){
//                        onDeleteItemClick();
//                    }

                }

                mAdapterInvite.notifyDataSetChanged();
                mProgressBarInvite.setVisibility(View.GONE);

                checkMembers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressBarInvite.setVisibility(View.INVISIBLE);
            }

        });

    }

    private String getDateToday(){
        DateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String today= dateFormat.format(date);
        return today;
    }



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


    @Override
    public void onClick(View v) {

    }



    private void showDialogPhoneNumber() {
        dialog.setContentView(R.layout.invite_user_popup);
        dialog.setCanceledOnTouchOutside(false);
        phoneNumber = (EditText) dialog.findViewById(R.id.email_member);
        start = (TextView) dialog.findViewById(R.id.start_time);
        member = (CheckBox) dialog.findViewById(R.id.always_member);
        expiredUser = (TextView) dialog.findViewById(R.id.exp);
        closePopup = (ImageView) dialog.findViewById(R.id.close_popup_phone);
        sendInvite = (Button) dialog.findViewById(R.id.send_invite);
        sendInvite.setOnClickListener(this);
        start.setOnClickListener(this);
        expiredUser.setOnClickListener(this);
        BounceView.addAnimTo(dialog);
        final Calendar myCalendar = Calendar.getInstance();


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
                    start.setVisibility(View.GONE);
                    expiredUser.setVisibility(View.GONE);
                    start.setText("always");
                    expiredUser.setText("always");
                }

                else {
                    start.setVisibility(View.VISIBLE);
                    expiredUser.setVisibility(View.VISIBLE);
                    start.setText("Start");
                    expiredUser.setText("End");
                }

            }
        });


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerStart();
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

        sendInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pd = new ProgressDialog(getActivity());
                pd.setMessage("Please wait...");
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setCanceledOnTouchOutside(false);
                pd.setCancelable(false);
                pd.show();
                final String str_invite = phoneNumber.getText().toString();
                final String str_username = Data.usernameConnect;
                final String str_exp = expiredUser.getText().toString();
                final String str_start = start.getText().toString();
                final String str_member = member.getText().toString();

                if (str_invite.isEmpty()){
                    phoneNumber.setError("Email required");
                    pd.hide();
                    return;
                }

                else if (str_start.isEmpty()){
                    start.setError("Start time required");
                    pd.hide();
                    return;
                }

                else if (str_exp.isEmpty()){
                    expiredUser.setError("Expired required");
                    pd.hide();
                    return;
                }
                long cutoff = new Date().getTime() - TimeUnit.MILLISECONDS.convert(30, TimeUnit.DAYS);
                reference = FirebaseDatabase.getInstance().getReference("Users").child(str_invite.replace(".",","));
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Intent i = getActivity().getIntent();
                            String deviceCode = i.getExtras().getString("DEVICECODE_KEY");

                            reference = FirebaseDatabase.getInstance().getReference("Devices").child(deviceCode).child("Member");
                            reference1 = FirebaseDatabase.getInstance().getReference().child("Users").child(str_invite
                                    .replace(".", ",")).child("Houses");
                            reference2 = FirebaseDatabase.getInstance().getReference("Devices").child(deviceCode)
                                    .child("Member");
                            reference3 = FirebaseDatabase.getInstance().getReference("Devices").child(deviceCode)
                                    .child("Member");


                            if (dataSnapshot.exists()) {

                                User getUser = dataSnapshot.getValue(User.class);
                                String fullName = getUser.getFullname();

                                String uploadId = getUser.getEmail().replace(".", ",");
                                String uploadId1 = reference1.push().getKey();

//                                reference.child(uploadId).setValue(user);
                                reference1.child(uploadId1).setValue(deviceCode);
                                reference2.child(uploadId).setValue(getUser);
                                reference3.child(uploadId).child("start_access").setValue(str_start);
                                reference3.child(uploadId).child("expired").setValue(str_exp);


                                Toast.makeText(getActivity(), str_invite + "added", Toast.LENGTH_SHORT).show();


                                dialog.dismiss();
                                pd.hide();
                                return;
                            }

                            else {
                                pd.hide();
                                phoneNumber.setError(str_invite + " Not found");
//                                Toast.makeText(getActivity(), str_invite + " Not found ", Toast.LENGTH_SHORT).show();

                            }

                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            pd.hide();
                            phoneNumber.setError(str_invite + " Not found");
//                            Toast.makeText(getActivity(), str_invite + " Not found ", Toast.LENGTH_SHORT).show();


                        }
                    });

                }


        });

    }


    private void updateLabelStart() {
        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        start.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateLabelEnd() {
        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        expiredUser.setText(sdf.format(myCalendar.getTime()));
    }


    private void userInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getEmail().replace(".", ","));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String showEmail = firebaseUser.getEmail();
                if (getContext() == null){
                    return;
                }
                User user = dataSnapshot.getValue(User.class);

                Data.usernameConnect = user.getFullname();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void viewWidgrt(View view){
        dialog = new Dialog(getActivity());
        phoneNumber = (EditText) view.findViewById(R.id.email_member);
        invite = (Button) view.findViewById(R.id.invite_btn_user);
        sendInvite = (Button) view.findViewById(R.id.send_invite);
        closePopup = (ImageView) view.findViewById(R.id.close_popup_phone);
        expiredUser = (TextView) view.findViewById(R.id.exp);
        start = (TextView) view.findViewById(R.id.start_time);
        member = (CheckBox) view.findViewById(R.id.always_member);
        lr = (LinearLayout) view.findViewById(R.id.lr_invite);

        mRecyclerViewInvite = view.findViewById(R.id.recycler_view_invite);
        mRecyclerViewInvite.setHasFixedSize(true);
        mRecyclerViewInvite.setLayoutManager(new LinearLayoutManager(getActivity()));

        mProgressBarInvite = view.findViewById(R.id.myDataUserLoaderProgressBar);
        mProgressBarInvite.setVisibility(View.VISIBLE);
        empty_members = view.findViewById(R.id.empty_member);

        mConnect = new ArrayList<>();
        mDevice = new ArrayList<>();
        mAdapterInvite = new RecylerViewAdapterUserInvite (getActivity(), mConnect);
        mRecyclerViewInvite.setAdapter(mAdapterInvite);
        mAdapterInvite.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(int position) {
        User clickedUser= mConnect.get(position);
//        House device = mDevice.get(position);
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

                final String key = dataSnapshot.getKey();
                reference.child(selectedKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
//                        reference0.child(key).removeValue();
                        Toast.makeText(getActivity(), "Item deleted", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getActivity(), key, Toast.LENGTH_SHORT).show();
                        return;

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

//    public void handleExpired(){
//        User user = new User();
//        final String selectedKey = user.getEmail().replace(".", ",");
//
//        Intent i = getActivity().getIntent();
//        final String deviceCode = i.getExtras().getString("DEVICECODE_KEY");
//
//        long cutoff = new Date().getTime() - TimeUnit.MILLISECONDS.convert(30, TimeUnit.DAYS);
//        Query oldItems = reference.child("Devices").child(deviceCode).child("Member").child(selectedKey).orderByChild("expired").endAt(cutoff);
//        oldItems.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                for (DataSnapshot itemSnapshot: snapshot.getChildren()) {
//                    itemSnapshot.getRef().removeValue();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                throw databaseError.toException();
//            }
//        });
//    }

//    private void handleExpired(int position) {
//        final DatabaseReference currentRef = mAdapterInvite.getew(position);
//        currentRef.child("endTime").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                long time = System.currentTimeMillis();  //get time in millis
//                long end = Long.parseLong( dataSnapshot.getValue().toString()); //get the end time from firebase database
//
//                //convert to int
//                int timenow = (int) time;
//                int endtime = (int) end;
//
//                //check if the endtime has been reached
//                if (end < time){
//                    currentRef.removeValue();  //remove the entry
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
}


