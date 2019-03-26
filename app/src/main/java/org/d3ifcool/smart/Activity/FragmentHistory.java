package org.d3ifcool.smart.Activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import org.d3ifcool.smart.Adapter.RecyclerViewAdapterHistory;
import org.d3ifcool.smart.BottomNavigation.SectionsPageAdapter;
import org.d3ifcool.smart.Model.History;
import org.d3ifcool.smart.Model.User;
import org.d3ifcool.smart.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class FragmentHistory extends Fragment implements View.OnClickListener, RecyclerViewAdapterHistory.OnItemClickListener  {

    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    private TextView misEmpty, mFilter;
    private CardView cardView;

    private RecyclerView mRecyclerViewHistory;
    private RecyclerViewAdapterHistory mAdapterHistory;
    private ProgressBar mProgressBarHistory;
    private FirebaseStorage mStorageHistory;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private List<User> mHistory;
    FirebaseUser firebaseUser;
    Calendar myCalendar = Calendar.getInstance();
    Dialog dialog;

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

//                updateLabel();

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


    public void checkHistory(){
        if (mAdapterHistory.getItemCount() != 0){
            misEmpty.setVisibility(View.INVISIBLE);
            mRecyclerViewHistory.setVisibility(View.VISIBLE);
            mFilter.setVisibility(View.VISIBLE);
            cardView.setVisibility(View.VISIBLE);
        }

        else {
            misEmpty.setVisibility(View.VISIBLE);
            mRecyclerViewHistory.setVisibility(View.INVISIBLE);
            mFilter.setVisibility(View.INVISIBLE);
            cardView.setVisibility(View.INVISIBLE);
        }
    }


    public void getHistory(){
        Intent i = getActivity().getIntent();
        final String deviceCode =i.getExtras().getString("DEVICECODE_KEY");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Devices").child(deviceCode).child("History");
        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mHistory.clear();
                for (DataSnapshot connectSnapshot : dataSnapshot.getChildren()) {

                    User upload = connectSnapshot.getValue(User.class);
                    mHistory.add(upload);
                }
                mAdapterHistory.notifyDataSetChanged();
                mProgressBarHistory.setVisibility(View.INVISIBLE);
                checkHistory();

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

        mHistory = new ArrayList<>();
        mAdapterHistory = new RecyclerViewAdapterHistory (getActivity(), mHistory);
        mRecyclerViewHistory.setAdapter(mAdapterHistory);
        mAdapterHistory.setOnItemClickListener(this);
    }

}