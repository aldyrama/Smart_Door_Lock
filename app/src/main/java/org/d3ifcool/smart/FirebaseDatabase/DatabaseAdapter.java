package org.d3ifcool.smart.FirebaseDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.d3ifcool.smart.Account.Account;
import org.d3ifcool.smart.Model.Door;
import org.d3ifcool.smart.Model.House;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DatabaseAdapter {

    private Context mContext;

    public DatabaseAdapter(Context context) {
        mContext = context ;

    }

    public long addAccount(Account account){
        ContentValues values =new ContentValues();
        values.put(SmartContract.AccountEntry.KEY_USERNAME, String.valueOf(account.getmUsername()));
        values.put(SmartContract.AccountEntry.KEY_IMAGE ,account.getmImage());//account image
        values.put(SmartContract.AccountEntry.KEY_FULLNAME, String.valueOf(account.getmFullName()));
        values.put(SmartContract.AccountEntry.KEY_EMAIL, String.valueOf(account.getmEmail()));
        values.put(SmartContract.AccountEntry.KEY_PASSWORD, String.valueOf(account.getmPassword()));
        values.put(SmartContract.AccountEntry.KEY_TYPE_ACCOUNT, String.valueOf(account.getmTypeAccount()));

        mContext.getContentResolver().insert(SmartContract.AccountEntry.CONTENT_URI, values);

        long i = 1;
        return i;

    }

    public long addHouse(House house){
        ContentValues values = new ContentValues();
        values.put(SmartContract.HouseEntry.KEY_HOUSENAME, String.valueOf(house.getName()));
        values.put(SmartContract.HouseEntry.KEY_HOME_MADE_DATE, String.valueOf(house.getMadeDate()));

        mContext.getContentResolver().insert(SmartContract.HouseEntry.CONTENT_URI, values);

        long i = 1;
        return i;
    }

    public long addDoor(Door door){
        ContentValues values = new ContentValues();
        values.put(SmartContract.DoorEntry.KEY_DOORNAME, String.valueOf(door.getDoorName()));
        values.put(SmartContract.DoorEntry.KEY_DOOR_MADE_DATE, String.valueOf(door.getMadeDate()));

        long i = 1;
        return i;

    }

    public Account getAccount(){
        Account account = null;

        String[] projection= {
                SmartContract.AccountEntry.KEY_USERNAME,
                SmartContract.AccountEntry.KEY_FULLNAME,
                SmartContract.AccountEntry.KEY_IMAGE,
                SmartContract.AccountEntry.KEY_EMAIL,
                SmartContract.AccountEntry.KEY_PASSWORD,
                SmartContract.AccountEntry.KEY_TYPE_ACCOUNT
        };

        Cursor cursor = mContext.getContentResolver().query(SmartContract.AccountEntry.CONTENT_URI, projection,
                null,null,null);

        int indexColumnUsername = cursor.getColumnIndex(SmartContract.AccountEntry.KEY_USERNAME);
        int indexColumnFullname = cursor.getColumnIndex(SmartContract.AccountEntry.KEY_FULLNAME);
        int indexColumnImage = cursor.getColumnIndex(SmartContract.AccountEntry.KEY_IMAGE);
        int indexColumnEmail = cursor.getColumnIndex(SmartContract.AccountEntry.KEY_EMAIL);
        int indexColumnPassword = cursor.getColumnIndex(SmartContract.AccountEntry.KEY_PASSWORD);
        int indexColumnTypeAccount = cursor.getColumnIndex(SmartContract.AccountEntry.KEY_TYPE_ACCOUNT);

        if (cursor.moveToFirst()){
            do {
                account = new Account(
                        cursor.getString(indexColumnUsername),
                        cursor.getString(indexColumnFullname),
                        cursor.getString(indexColumnImage),
                        cursor.getString(indexColumnEmail),
                        cursor.getString(indexColumnPassword),
                        cursor.getString(indexColumnTypeAccount)
                );
            }

            while (cursor.moveToNext());
        }

        cursor.close();
        return account;
    }

    public ArrayList<House> getHouse(){

        ArrayList<House> houseList = new ArrayList<House>();
        String[] projection= {
                SmartContract.HouseEntry.KEY_HOUSENAME,
                SmartContract.HouseEntry.KEY_HOME_MADE_DATE
        };

        Cursor cursor = mContext.getContentResolver().query(SmartContract.HouseEntry.CONTENT_URI, projection,
                null,null,null);

        int indexColumnHouseName = cursor.getColumnIndex(SmartContract.HouseEntry.KEY_HOUSENAME);
        int indexColumnMadeDate = cursor.getColumnIndex(SmartContract.HouseEntry.KEY_HOME_MADE_DATE);

//        if (cursor.moveToFirst()){
//            do {
//                House house = new House(
//                        cursor.getString(indexColumnHouseName),
//                        cursor.getString(indexColumnMadeDate)
//                );
//                houseList.add(house);
//
//            }
//
//            while (cursor.moveToNext());
//        }

        cursor.close();
        return houseList;
    }

//    public ArrayList<Door> getDoor(){
//
//        ArrayList<Door> doorList = new ArrayList<Door>();
//        String[] projection= {
//                SmartContract.DoorEntry.KEY_DOORNAME,
//                SmartContract.DoorEntry.KEY_DOOR_MADE_DATE
//        };
//
//        Cursor cursor = mContext.getContentResolver().query(SmartContract.DoorEntry.CONTENT_URI, projection,
//                null,null,null);
//
//        int indexColumnDoorName = cursor.getColumnIndex(SmartContract.DoorEntry.KEY_DOORNAME);
//        int indexColumnMadeDate = cursor.getColumnIndex(SmartContract.DoorEntry.KEY_DOOR_MADE_DATE);
//
//        if (cursor.moveToFirst()){
//            do {
//                Door door = new Door(
//                        cursor.getString(indexColumnDoorName),
//                        cursor.getString(indexColumnMadeDate)
//                );
//                doorList.add(door);
//
//            }
//
//            while (cursor.moveToNext());
//        }
//
//        cursor.close();
//        return doorList;
//    }

    public int updateAccount(Account account, String curremtUsername) {
        ContentValues values = new ContentValues();
        values.put(SmartContract.AccountEntry.KEY_USERNAME ,account.getmUsername());
        values.put(SmartContract.AccountEntry.KEY_FULLNAME, account.getmFullName());
        values.put(SmartContract.AccountEntry.KEY_EMAIL, account.getmEmail());
//        values.put(SmartContract.AccountEntry.KEY_IMAGE ,account.getmImage());//account image
        values.put(SmartContract.AccountEntry.KEY_TYPE_ACCOUNT ,account.getmTypeAccount());
        values.put(SmartContract.AccountEntry.KEY_PASSWORD,account.getmPassword());
        // updating row
        return mContext.getContentResolver().update(SmartContract.AccountEntry.CONTENT_URI,
                values, SmartContract.AccountEntry.KEY_USERNAME + " = ?",
                new String[] { String.valueOf(curremtUsername) });
    }

    public int updateHouse(House house) {
        ContentValues values = new ContentValues();
        values.put(SmartContract.HouseEntry.KEY_HOUSENAME, String.valueOf(house.getName()));
        values.put(SmartContract.HouseEntry.KEY_HOME_MADE_DATE,String.valueOf(house.getMadeDate()));

        return mContext.getContentResolver().update(
                SmartContract.HouseEntry.CONTENT_URI,
                values,
                SmartContract.HouseEntry._ID + " = ?",
                new String[] { String.valueOf(house.getName()) });
    }

    public int updateDoor(Door door) {
        ContentValues values = new ContentValues();
        values.put(SmartContract.DoorEntry.KEY_DOORNAME, String.valueOf(door.getDoorName()));
        values.put(SmartContract.DoorEntry.KEY_DOOR_MADE_DATE,String.valueOf(door.getMadeDate()));

        return mContext.getContentResolver().update(
                SmartContract.HouseEntry.CONTENT_URI,
                values,
                SmartContract.DoorEntry._ID + " = ?",
                new String[] { String.valueOf(door.getDoorName()) });
    }

    public void deleteAccount(String username) {
        mContext.getContentResolver().delete(
                SmartContract.AccountEntry.CONTENT_URI,
                SmartContract.AccountEntry.KEY_USERNAME + " =?",
                new String[] { String.valueOf(username) });
    }

    public void deleteHouse(House house) {
        mContext.getContentResolver().delete(
                SmartContract.HouseEntry.CONTENT_URI,
                SmartContract.HouseEntry.KEY_HOUSENAME + "=?",
                new String[] {
                        String.valueOf(house.getName())

                });

    }

    public void deleteDoor(Door door) {
        mContext.getContentResolver().delete(
                SmartContract.DoorEntry.CONTENT_URI,
                SmartContract.DoorEntry.KEY_DOORNAME + "=?",
                new String[] {
                        String.valueOf(door.getDoorName())

                });

    }
}
