package org.d3ifcool.smart.Activity;

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
import org.d3ifcool.smart.R;

public class DetailHistory extends AppCompatActivity {

    private ImageView photo, close;
    private TextView detailNameTextView, detailTypeAccount,
            startTime,endTime, today, door;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_detail_history);

        getWindow().setFlags(

                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN

        );

        initializeWidgets();

        DisplayMetrics mt = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(mt);

        int width = mt.widthPixels;

        int hight = mt.heightPixels;

        getWindow().setLayout((int)(width *.9), (int)(hight *.5));

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        Intent i=this.getIntent();

        String name = i.getExtras().getString("NAME_KEY");

        String image =i.getExtras().getString("IMAGE_KEY");

        String startAccess =i.getExtras().getString("START_KEY");

        String expired =i.getExtras().getString("EXPIRED");

        String time =i.getExtras().getString("TIME_KEY");

        String typeaccount = i.getExtras().getString("TYPEACCOUNT_KEY");

        String doors = i.getExtras().getString("DOOR_KEY");


        detailNameTextView.setText(name);

        detailTypeAccount.setText(typeaccount);

        today.setText(time);

        door.setText(doors);

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

    private void initializeWidgets(){

//        deviceCode = v.findViewById(R.id.devicecodedetail);

        photo = findViewById(R.id.imgUser);

        detailNameTextView = findViewById(R.id.detailNameUser);

        detailTypeAccount = findViewById(R.id.typeaccount);

        door = findViewById(R.id.door);

        today = findViewById(R.id.todaytime);

        close = findViewById(R.id.close_popup);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        startActivity(new Intent(DetailHistory.this, HousesDetail.class));

    }
}
