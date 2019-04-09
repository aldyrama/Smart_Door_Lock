package org.d3ifcool.smart.Home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.d3ifcool.smart.Adapter.RecylerViewAdapterUserInvite;
import org.d3ifcool.smart.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailUser extends AppCompatActivity implements  View.OnClickListener, RecylerViewAdapterUserInvite.OnItemClickListener {

    TextView emailDetailTextView,detailNameTextView,detailTypeAccount, deviceCode, startTime, endTime;
    ImageView close;
    CircleImageView photo;

    private void initializeWidgets(){
//        emailDetailTextView = findViewById(R.id.emailuser);
        deviceCode = findViewById(R.id.devicecodedetail);
        photo = findViewById(R.id.imgUser);
        detailNameTextView = findViewById(R.id.detailNameUser);
        detailTypeAccount = findViewById(R.id.typeaccount);
        startTime = findViewById(R.id.start_time);
        endTime = findViewById(R.id.end_time);


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_detail_user);
//        setStatustBarColor(R.color.colorWhite);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        );

        initializeWidgets();

        DisplayMetrics mt = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mt);

        int width = mt.widthPixels;
        int hight = mt.heightPixels;

        getWindow().setLayout((int)(width *.9), (int)(hight *.6));
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Intent i=this.getIntent();
        String name = i.getExtras().getString("NAME_KEY");
        String email = i.getExtras().getString("EMAIL_KEY");
        String image =i.getExtras().getString("IMAGE_KEY");
        String typeaccount = i.getExtras().getString("TYPEACCOUNT_KEY");
        String device =i.getExtras().getString("DEVICECODE_KEY");
        String startAccess =i.getExtras().getString("STARTACCESS");
        String expired =i.getExtras().getString("EXPIRED");


        detailNameTextView.setText(name);
        deviceCode.setText(device);
        detailTypeAccount.setText(typeaccount);
        startTime.setText(startAccess);
        endTime.setText(expired);
        Picasso.with(this)
                .load(image)
                .placeholder(R.drawable.userphoto)
                .fit()
                .centerCrop()
                .into(photo);



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

    @SuppressLint("ResourceAsColor")
    private void setStatustBarColor(@ColorRes int statustBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            int color = ContextCompat.getColor(this, statustBarColor);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
            window.setTitleColor(R.color.black);
        }
    }

    private String getDateToday(){
        DateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String today= dateFormat.format(date);
        return today;
    }

}
