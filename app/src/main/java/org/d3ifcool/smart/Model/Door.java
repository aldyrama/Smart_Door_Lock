package org.d3ifcool.smart.Model;

public class Door {
    private String doorName;
    private String key;
    private String madeDate;
    private int position;

    public Door() {
        //empty constructor needed
    }
    public Door(int position){
        this.position = position;
    }

    public Door(String doorName) {
        if (doorName.trim().equals("")) {
            doorName = "No Name";
        }
        this.doorName = doorName;
//        this.madeDate = madeDate;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDoorName() {
        return doorName;
    }

    public void setDoorName(String doorName) {
        this.doorName = doorName;
    }

    public String getMadeDate() {
        return madeDate;
    }

    public void setMadeDate(String madeDate) {
        this.madeDate = madeDate;
    }
}
