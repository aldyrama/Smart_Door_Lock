package org.d3ifcool.smart.Home;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FragmentUser extends Fragment implements View.OnClickListener, RecylerViewAdapterUserInvite.OnItemClickListener{

    private static final String TAG = "User";

    User mUser;
    View v;
    DatabaseReference mDatabase;
    FirebaseUser firebaseUser;
    FirebaseAuth auth;
    DatabaseReference reference;

    Dialog dialog;
    Button sendInvite;
    EditText phoneNumber;
    CountryCodePicker phoneCode;
    ImageView closePopup;
    FloatingActionButton invite;
    ProgressDialog pd;
    TextView username, empty_members;
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

    private void openDetailActivity(String[] data){
        Intent intent = new Intent(getActivity(), DetailUser.class);
        intent.putExtra("NAME_KEY",data[0]);
        intent.putExtra("EMAIL_KEY",data[1]);
        intent.putExtra("IMAGE_KEY",data[2]);
        intent.putExtra("TYPEACCOUNT_KEY",data[3]);

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

//        Intent i = getActivity().getIntent();
//        String name =i.getExtras().getString("NAME_KEY");
//        username.setText(name);

        Intent i = getActivity().getIntent();
        final String deviceCode =i.getExtras().getString("DEVICECODE_KEY");




        mRecyclerViewInvite.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                if (dy < 0){
                    invite.show();

                }

                else if (dy > 0){
                    invite.hide();
                }

                else if (dy == 0){
                    invite.show();
                }
            }
        });


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

                } else {
                    invite.setVisibility(View.GONE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

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
//                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
////                    Map<String,Object> map =  connectSnapshot.getValue(genericTypeIndicator);
//                    Map<String, Object> map = (Map<String, Object>) connectSnapshot.getValue();

                    User upload = connectSnapshot.getValue(User.class);
//                    User upload = new User();
//                    upload.setEmail( (String) map.get("email"));
//                    upload.setFullname((String) map.get("fullname"));
                    mConnect.add(upload);

//                for (DataSnapshot houseSnapshot : dataSnapshot.getChildren()) {
//
//                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
//                    Map<String,Object> map =  houseSnapshot.getValue(genericTypeIndicator);
//
//                        Connect invite = new Connect();
//                        invite.setUsers((String) map.get("users"));
//                        mConnect.add(invite);

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
        phoneNumber = (EditText) dialog.findViewById(R.id.email_member);
        closePopup = (ImageView) dialog.findViewById(R.id.close_popup_phone);
        sendInvite = (Button) dialog.findViewById(R.id.send_invite);
        sendInvite.setOnClickListener(this);


        closePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
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

                if (str_invite.isEmpty()){
                    phoneNumber.setError("Email required");
                    pd.hide();
                    return;
                }

                reference = FirebaseDatabase.getInstance().getReference("Users").child(str_invite.replace(".",","));
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Intent i = getActivity().getIntent();
                            String deviceCode = i.getExtras().getString("DEVICECODE_KEY");

                            reference = FirebaseDatabase.getInstance().getReference("Devices").child(deviceCode).child("Member");

                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Users").
                                    child(str_invite.replace(".", ",")).child("Houses");

                            DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Devices").child(deviceCode).child("Member");
                            Connect user = new Connect(str_invite);

                            if (dataSnapshot.exists()) {

                                User getUser = dataSnapshot.getValue(User.class);
                                String fullName = getUser.getFullname();

                                String uploadId = reference.push().getKey();
//                                reference.child(uploadId).setValue(user);
                                reference1.child(uploadId).setValue(deviceCode);
                                reference2.child(uploadId).setValue(getUser);

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
//                RequestBuilder<Drawable> photo = Glide.with(getContext()).load(user.getImageurl());
//                Glide.with(getContext()).load(user.getImageurl()).into(image_profile);
//                name.setText(user.getFullname());
//                username.setText(user.getUsername());
//                email.setText(showEmail);
//                account.setText(user.getTypeAccount());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void viewWidgrt(View view){
        dialog = new Dialog(getActivity());
        phoneNumber = (EditText) view.findViewById(R.id.email_member);
        invite = (FloatingActionButton) view.findViewById(R.id.invite_btn_user);
        sendInvite = (Button) view.findViewById(R.id.send_invite);
        closePopup = (ImageView) view.findViewById(R.id.close_popup_phone);

        mRecyclerViewInvite = view.findViewById(R.id.recycler_view_invite);
        mRecyclerViewInvite.setHasFixedSize(true);

        mRecyclerViewInvite.setLayoutManager(new LinearLayoutManager(getActivity()));
//        mAdapterInvite = new RecylerViewAdapterUserInvite(getContext(), mConnect);

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
        String[] userData={clickedUser.getFullname(), clickedUser.getEmail(), clickedUser.getImageurl(), clickedUser.getTypeAccount()};
        openDetailActivity(userData);


    }

    @Override
    public void onShowItemClick(int position) {
        User clickedUser= mConnect.get(position);
        String[] userData={clickedUser.getFullname(), clickedUser.getEmail(), clickedUser.getImageurl(), clickedUser.getTypeAccount()};
        openDetailActivity(userData);

    }

    @Override
    public void onDeleteItemClick(int position) {
//        User selectedItem = mConnect.get(position);
//        final String selectedKey = selectedItem.getKey();
//        Data.usernameConnect = selectedKey;
//
//        Intent i = getActivity().getIntent();
//        final String deviceCode = i.getExtras().getString("DEVICECODE_KEY");
//        reference = FirebaseDatabase.getInstance().getReference().child("Devices").child(deviceCode).child("Member");
//
//        mDatabaseRef.child(selectedKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
////                mDatabaseRef.child(selectedKey).removeValue();
//                Toast.makeText(getActivity(), "Item deleted", Toast.LENGTH_SHORT).show();
//                return;
//
//            }
//        });


    }
}


