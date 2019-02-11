package org.d3ifcool.smart.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.d3ifcool.smart.Model.Setting;
import org.d3ifcool.smart.R;

import java.util.List;
import java.util.Set;

public class SettingAdapter extends BaseAdapter {


    Activity activity;
    List<Setting> list;
    LayoutInflater inflater;

    //short to create constructer using command+n for mac & Alt+Insert for window


    public SettingAdapter(Activity activity) {
        this.activity = activity;
    }

    public SettingAdapter(Activity activity, List<Setting> list) {
        this.activity   = activity;
        this.list      = list;

        inflater        = activity.getLayoutInflater();
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder = null;

        if (view == null){

            view = inflater.inflate(R.layout.adapter_listview_setting, viewGroup, false);

            holder = new ViewHolder();

            holder.txtsetting_list = (TextView)view.findViewById(R.id.txt_setting);
            holder.ivCheckBox = (ImageView) view.findViewById(R.id.iv_check_box);

            view.setTag(holder);
        }else
            holder = (ViewHolder)view.getTag();

        Setting model = list.get(i);

        holder.txtsetting_list.setText(model.getTxtSetting());

        if (model.isCheck())
            holder.ivCheckBox.setBackgroundResource(R.drawable.switch_on);

        else
            holder.ivCheckBox.setBackgroundResource(R.drawable.switch_off);

        return view;

    }

    public void updateRecords(List<Setting>  users){
        this.list = users;

        notifyDataSetChanged();
    }

    class ViewHolder{

        TextView txtsetting_list;
        ImageView ivCheckBox;

    }
}

