package org.d3ifcool.smart.Home;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.d3ifcool.smart.Adapter.RecylerViewAdapterUserInvite;
import org.d3ifcool.smart.Family.FamilyActivity;
import org.d3ifcool.smart.R;

public class DetailUser extends AppCompatActivity implements  View.OnClickListener, RecylerViewAdapterUserInvite.OnItemClickListener {

    TextView nameDetailTextView,descriptionDetailTextView,dateDetailTextView,categoryDetailTextView;
    ImageView close;

    private void initializeWidgets(){
        nameDetailTextView= findViewById(R.id.usernameuser);
        close = findViewById(R.id.close_detail);


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_detail_user);
        initializeWidgets();

        DisplayMetrics mt = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mt);

        int width = mt.widthPixels;
        int hight = mt.heightPixels;

        getWindow().setLayout((int)(width *.8), (int)(hight *.6));
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Intent i=this.getIntent();
        String name=i.getExtras().getString("NAME_KEY");

        nameDetailTextView.setText(name);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DetailUser.this, FamilyActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

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
}
