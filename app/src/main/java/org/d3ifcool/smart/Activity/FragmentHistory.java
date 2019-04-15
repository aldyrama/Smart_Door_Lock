package org.d3ifcool.smart.Activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
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

import org.d3ifcool.smart.Adapter.RecyclerViewAdapterHistory;
import org.d3ifcool.smart.Model.User;
import org.d3ifcool.smart.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FragmentHistory extends Fragment implements View.OnClickListener, RecyclerViewAdapterHistory.OnItemClickListener  {

    private TextView misEmpty, mFilter, dateview;
    private CardView cardView;
    private RecyclerView mRecyclerViewHistory;
    private RecyclerViewAdapterHistory mAdapterHistory;
    private ProgressBar mProgressBarHistory;
    private FirebaseStorage mStorageHistory;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private List<User> mHistory;
    private FirebaseUser firebaseUser;
    private Calendar myCalendar = Calendar.getInstance();
    private Dialog dialog;
    private String mCurrentDate;
    private LinearLayout lr;

    public FragmentHistory() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_history, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        viewWidget(view);

        getHistory();
        
        mFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDatePicker();

            }

        });

        return view;

    }

    private void showDatePicker() {

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);

                myCalendar.set(Calendar.MONTH, monthOfYear);

                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                myCalendar.setMinimalDaysInFirstWeek((int) (System.currentTimeMillis() - 1000));

                int day = myCalendar.get(Calendar.DAY_OF_MONTH);

                int month = myCalendar.get(Calendar.MONTH);

                int years = myCalendar.get(Calendar.YEAR);

                mCurrentDate = (day+"/"+month+"/"+years);

                updateLabel();

            }

        };

        Date newDate = myCalendar.getTime();

        new DatePickerDialog(dialog.getContext(), R.style.DialogTheme, date, myCalendar

                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),

                myCalendar.get(Calendar.DAY_OF_MONTH))

                .show();

    }

    private void updateLabel() {

        String myFormat = "MM/dd/yyyy";

        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateview.setText(sdf.format(myCalendar.getTime()));

    }

    public void checkHistory(){

        if (mAdapterHistory.getItemCount() != 0){

            misEmpty.setVisibility(View.INVISIBLE);

            mRecyclerViewHistory.setVisibility(View.VISIBLE);

            lr.setVisibility(View.VISIBLE);

        }

        else {

            lr.setVisibility(View.GONE);

            misEmpty.setVisibility(View.VISIBLE);

            mRecyclerViewHistory.setVisibility(View.INVISIBLE);

        }

    }

    private String getDateToday(){

        DateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy" );

        Date date = new Date();

        String today= dateFormat.format(date);

        return today;

    }


    public void getHistory(){

        Intent i = getActivity().getIntent();
        final String deviceCode =i.getExtras().getString("DEVICECODE_KEY");

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Devices").child(deviceCode).child("History");
        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mHistory.clear();

                try {

                    for (DataSnapshot connectSnapshot : dataSnapshot.getChildren()) {

                        User upload = connectSnapshot.getValue(User.class);

                        String date = upload.getDate();

                        String now = getDateToday();

                        Log.d("time", now);

                        if (date.equals(now)) {

                            mHistory.add(upload);

                        }

                    }

                    mAdapterHistory.notifyDataSetChanged();

                    mProgressBarHistory.setVisibility(View.INVISIBLE);

                    checkHistory();

                }catch (Exception e){}

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                mProgressBarHistory.setVisibility(View.INVISIBLE);

            }

        });

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onShowItemClick(int position) {

    }

    @Override
    public void onDeleteItemClick(int position) {

    }

    private void viewWidget(View view){

        dialog = new Dialog(getActivity());

        mRecyclerViewHistory = view.findViewById(R.id.recycler_view_history);

        mRecyclerViewHistory.setHasFixedSize(true);

        mRecyclerViewHistory.setLayoutManager(new LinearLayoutManager(getActivity()));

        mProgressBarHistory = view.findViewById(R.id.myHistoryLoaderProgressBar);

        mProgressBarHistory.setVisibility(View.VISIBLE);

        misEmpty = view.findViewById(R.id.empty_history);

        mFilter = view.findViewById(R.id.filter_history);

        cardView = view.findViewById(R.id.card_filter);

        dateview = view.findViewById(R.id.datenow);

        lr = view.findViewById(R.id.lr_history);

        dateview.setText("Today, " + getDateToday());

        mHistory = new ArrayList<>();

        mAdapterHistory = new RecyclerViewAdapterHistory (getActivity(), mHistory);

        mRecyclerViewHistory.setAdapter(mAdapterHistory);

        mAdapterHistory.setOnItemClickListener(this);
    }

}