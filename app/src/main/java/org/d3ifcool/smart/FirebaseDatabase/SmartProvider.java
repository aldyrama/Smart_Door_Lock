package org.d3ifcool.smart.FirebaseDatabase;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

public class SmartProvider extends ContentProvider {

    private static final int ACCOUNT = 10 ;
    private static final int HOUSE = 1;
    private static final int DOOR = 2;

    private static final UriMatcher sUriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(SmartContract.CONTENT_AUTHORITY,
                SmartContract.PATH_HOUSE, HOUSE);
        sUriMatcher.addURI(SmartContract.CONTENT_AUTHORITY,
                SmartContract.PATH_ACCOUNT,ACCOUNT);
        sUriMatcher.addURI(SmartContract.CONTENT_AUTHORITY,
                SmartContract.PATH_DOOR,DOOR);
    }

    private DatabaseHelper mDatabaseHelper;


    @Override
    public boolean onCreate() {
        mDatabaseHelper = new DatabaseHelper(getContext());
        return true;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {

        SQLiteDatabase db  = mDatabaseHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match){
            case HOUSE :
                cursor = db.query(SmartContract.HouseEntry.TABLE_HOUSE, projection,
                        selection,selectionArgs,null,null,sortOrder);
                break;

            case DOOR :
                cursor = db.query(SmartContract.DoorEntry.TABLE_DOOR, projection,
                        selection,selectionArgs, null, null,sortOrder);
                break;

            case ACCOUNT :
                cursor = db.query(SmartContract.AccountEntry.TABBLE_ACCOUNT, projection,
                        selection,selectionArgs,null,null,sortOrder);

                default:
                    throw new IllegalArgumentException(
                            "Cannot query unknow uri" + uri
                    );
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;

    }

    @Nullable
    @Override
    public String getType(
            @NonNull Uri uri) {

        return null;

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri,
                      @Nullable ContentValues values) {

        SQLiteDatabase db  = mDatabaseHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        long id=0;

        switch (match){
            case HOUSE :
                String house = values.getAsString(SmartContract.HouseEntry.KEY_HOUSENAME);
                if (TextUtils.isEmpty(house)) {
                    throw new IllegalArgumentException(" housename require a name");
                }
                id = db.insert(SmartContract.HouseEntry.TABLE_HOUSE, null, values);

                break;

            case DOOR :
                String door = values.getAsString(SmartContract.DoorEntry.KEY_DOORNAME);
                if (TextUtils.isEmpty(door)){
                    throw new IllegalArgumentException(" doorname require a name");
                }
                id = db.insert(SmartContract.DoorEntry.TABLE_DOOR,null, values);

                break;

            case ACCOUNT :
                String account = values.getAsString(SmartContract.AccountEntry.KEY_USERNAME);
                if (TextUtils.isEmpty(account)){
                    throw new IllegalArgumentException(" username require a name");
                }
                id = db.insert(SmartContract.AccountEntry.TABBLE_ACCOUNT,null,values);

                break;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri,id);


    }

    @Override
    public int delete(@NonNull Uri uri,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int id=0;

        switch (match){
            case HOUSE :
                id = db.delete(
                        SmartContract.HouseEntry.TABLE_HOUSE,
                        selection,
                        selectionArgs
                );

                break;

            case DOOR :
                id = db.delete(
                        SmartContract.DoorEntry.TABLE_DOOR,
                        selection,
                        selectionArgs
                );

                break;

            case ACCOUNT :
                id = db.delete(
                        SmartContract.AccountEntry.TABBLE_ACCOUNT,
                        selection,
                        selectionArgs
                );

                break;
        }

        db.close();
        return id;

    }

    @Override
    public int update(@NonNull Uri uri,
                      @Nullable ContentValues values,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        SQLiteDatabase db  = mDatabaseHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        long id=0;

        switch (match) {
            case HOUSE:
                String house = values.getAsString(SmartContract.HouseEntry.KEY_HOUSENAME);
                if (TextUtils.isEmpty(house)) {
                    throw new IllegalArgumentException(" house require a name");
                }
                id = db.update(SmartContract.HouseEntry.TABLE_HOUSE, values,
                        selection,
                        selectionArgs);

                break;

            case DOOR:
                String door = values.getAsString(SmartContract.DoorEntry.KEY_DOORNAME);
                if (TextUtils.isEmpty(door)) {
                    throw new IllegalArgumentException(" door required a name");
                }
                id = db.update(SmartContract.DoorEntry.TABLE_DOOR, values,
                        selection,
                        selectionArgs);

                break;

            case ACCOUNT :
                String account = values.getAsString(SmartContract.AccountEntry.KEY_USERNAME);
                if (TextUtils.isEmpty(account)){
                    throw new IllegalArgumentException(" username required a name");
                }
                id = db.update(SmartContract.AccountEntry.TABBLE_ACCOUNT, values,
                        selection,
                        selectionArgs);

                break;
        }

        return (int) id;

    }
}
