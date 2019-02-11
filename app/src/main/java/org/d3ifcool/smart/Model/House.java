package org.d3ifcool.smart.Model;

import com.google.firebase.database.Exclude;

public class House {
    private String name;
    private String key;
    private String madeDate;
    private int position;

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

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }

    public String getMadeDate() {
        return madeDate;
    }

    public void setMadeDate(String madeDate) {
        this.madeDate = madeDate;
    }
}

