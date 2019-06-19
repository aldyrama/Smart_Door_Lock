package org.d3ifcool.smart.Model;

public class Door {

    private String doorName;

    private String key;

    private int doorLock;

    private String madeDate;

    private String doorPin;

    private String status;

    private int position;

    private String connect;

    private int power;

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

    }

    public Door(String str_door, String str_pin) {

        this.doorName = str_door;

        this.doorPin = str_pin;

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

    public String getConnect() {
        return connect;
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

    public String getDoorPin() {

        return doorPin;

    }

    public void setDoorPin(String doorPin) {

        this.doorPin = doorPin;

    }

    public int getDoorLock() {

        return doorLock;

    }

    public void setDoorLock(int doorLock) {

        this.doorLock = doorLock;

    }

    public String getStatus() {

        return status;

    }

    public void setStatus(String status) {

        this.status = status;

    }

    public int getPosition() {

        return position;

    }

    public void setPosition(int position) {

        this.position = position;

    }

    public void setConnect(String connect) {
        this.connect = connect;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }
}
