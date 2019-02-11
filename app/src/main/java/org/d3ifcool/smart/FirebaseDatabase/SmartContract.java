package org.d3ifcool.smart.FirebaseDatabase;

import android.net.Uri;
import android.provider.BaseColumns;

public class SmartContract {
    public static final String CONTENT_AUTHORITY = "org.d3ifcool.smart" ;

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+ CONTENT_AUTHORITY);

    public static final String PATH_ACCOUNT = "ACCOUNT";
    public static final String PATH_HOUSE = "HOUSE";
    public static final String PATH_DOOR = "DOOR";

    public static final class AccountEntry implements BaseColumns{
        public static final Uri CONTENT_URI = Uri.withAppendedPath(
                BASE_CONTENT_URI, PATH_ACCOUNT
        );

        public static final String TABBLE_ACCOUNT = "account";

        public static final String KEY_USERNAME = "username";
        public static final String KEY_IMAGE = "image";
        public static final String KEY_FULLNAME = "fullname";
        public static final String KEY_EMAIL = "my_email";
        public static final String KEY_PASSWORD = "my_password";
        public static final String KEY_TYPE_ACCOUNT = "type_account";
    }

    public static final class HouseEntry implements BaseColumns{
        public static final Uri CONTENT_URI = Uri.withAppendedPath(
                BASE_CONTENT_URI, PATH_HOUSE
        );

        public static final String TABLE_HOUSE = "house";


        public static final String KEY_HOUSENAME = "housename";
        public static final String KEY_HOME_MADE_DATE = "home_made_date";
    }

    public static final class DoorEntry implements BaseColumns{
        public static final Uri CONTENT_URI = Uri.withAppendedPath(
                BASE_CONTENT_URI, PATH_DOOR
        );

        public static final String TABLE_DOOR = "door";

        public static final String KEY_DOORNAME = "doorname";
        public static final String KEY_DOOR_MADE_DATE = "door_made_date";
    }

}
