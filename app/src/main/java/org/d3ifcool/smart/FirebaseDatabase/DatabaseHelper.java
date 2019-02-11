package org.d3ifcool.smart.FirebaseDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static org.d3ifcool.smart.FirebaseDatabase.SmartContract.AccountEntry;
import static org.d3ifcool.smart.FirebaseDatabase.SmartContract.HouseEntry;
import static org.d3ifcool.smart.FirebaseDatabase.SmartContract.DoorEntry;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context mContext;

    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_NAME = "smart.db";

    private static final String CREATE_ACCOUNT_TABLE =
            "CREATE TABLE " +
                    AccountEntry.TABBLE_ACCOUNT + "("
                    + AccountEntry.KEY_USERNAME + " TEXT PRIMARY KEY,"
                    + AccountEntry.KEY_IMAGE + " TEXT,"
                    + AccountEntry.KEY_FULLNAME + " TEXT,"
                    + AccountEntry.KEY_EMAIL + " TEXT,"
                    + AccountEntry.KEY_TYPE_ACCOUNT + "TEXT,"
                    + AccountEntry.KEY_PASSWORD + " TEXT)";

    private static final String CREATE_HOUSE_TABLE =
            "CREATE TABLE " +
                    HouseEntry.TABLE_HOUSE + "("
                    + HouseEntry.KEY_HOUSENAME + " TEXT PRIMARY KEY,"
                    + HouseEntry.KEY_HOME_MADE_DATE + " TEXT)";

    private static final String CREATE_DOOR_TABLE =
            "CREATE TABLE " +
                    DoorEntry.TABLE_DOOR + "("
                    + DoorEntry.KEY_DOORNAME + " TEXT PRIMARY KEY,"
                    + DoorEntry.KEY_DOOR_MADE_DATE + " TEXT)";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {

            db.execSQL(CREATE_ACCOUNT_TABLE);
            db.execSQL(CREATE_HOUSE_TABLE);
            db.execSQL(CREATE_DOOR_TABLE);

        }

        catch (Exception e){
            Log.e("Create database", "Error to create");
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
