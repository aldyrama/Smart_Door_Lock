package org.d3ifcool.smart.Model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;

public class House {

    private String name;

    private String deviceCode;

    private String key;

    private String madeDate;

    private boolean house_lock;

    private boolean guest;

    private boolean connect;

    private boolean thief;

    private int position;

    private String update;

    private int totalDevices;

    public House() {
        //empty constructor needed
    }

    public House(int position){

        this.position = position;

    }

    public House(String name) {

        if (name.trim().equals("")) {

            name = "No Name";

        }

        this.name = name;

//        this.madeDate = madeDate;

    }

    public House(String str_house, String str_device) {

        this.name = str_house;

        this.deviceCode = str_device;

    }

    public House(DataSnapshot devices) {

    }

    public boolean isThief() {

        return thief;

    }

    public String getName() {

        return name;

    }
    public void setName(String name) {

        this.name = name;

    }

    public String getDeviceCode() {

        return deviceCode;

    }

    public void setDeviceCode(String deviceCode) {

        this.deviceCode = deviceCode;

    }

    @Exclude
    public String getKey() {

        return key;

    }

    public String getUpdate() {

        return update;

    }

    @Exclude
    public void setKey(String key) {

        this.key = key;

    }

    public void setThief(boolean thief) {

        this.thief = thief;

    }

    public String getMadeDate() {

        return madeDate;

    }

    public void setMadeDate(String madeDate) {

        this.madeDate = madeDate;

    }

    public boolean isHouse_lock() {

        return house_lock;

    }

    public void setHouse_lock(boolean house_lock) {

        this.house_lock = house_lock;

    }

    public boolean isGuest() {

        return guest;

    }

    public void setGuest(boolean guest) {

        this.guest = guest;

    }

    public boolean isConnect() {

        return connect;

    }

    public void setConnect(boolean connect) {

        this.connect = connect;

    }

    public void setUpdate(String update) {

        this.update = update;

    }

    public int getTotalDevices() {
        return totalDevices;
    }

    public void setTotalDevices(int totalDevices) {
        this.totalDevices = totalDevices;
    }
}

