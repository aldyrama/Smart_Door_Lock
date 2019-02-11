package org.d3ifcool.smart.Internet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by cool on 11/8/2018.
 */

public class CheckConnection {

    Context mContext;
    boolean connected;

    public CheckConnection(Context mContext) {
        this.mContext = mContext;
    }

    public boolean checkInternetConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
        }
        else
            connected = false;

        return connected;
    }

    public String message(){
        return connected ? "Connect" : "Sorry, Not Connection" ;
    }
}
