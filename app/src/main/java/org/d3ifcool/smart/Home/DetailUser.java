package org.d3ifcool.smart.Home;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.d3ifcool.smart.Adapter.RecylerViewAdapterUserInvite;
import org.d3ifcool.smart.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailUser extends AppCompatActivity implements  View.OnClickListener, RecylerViewAdapterUserInvite.OnItemClickListener {

    private TextView detailNameTextView,
            detailTypeAccount,
            deviceCode,
            startTime,
            endTime;
    private CircleImageView photo;
    private ImageView close;

    private void initializeWidgets(){

        deviceCode = findViewById(R.id.devicecodedetail);

        photo = findViewById(R.id.imgUser);

        detailNameTextView = findViewById(R.id.detailNameUser);

        detailTypeAccount = findViewById(R.id.typeaccount);

        startTime = findViewById(R.id.start_time);

        endTime = findViewById(R.id.end_time);

        close = findViewById(R.id.close_popup);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popup_detail_user);

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

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
