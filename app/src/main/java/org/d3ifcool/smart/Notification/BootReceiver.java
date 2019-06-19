package org.d3ifcool.smart.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        Intent i = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
//                .putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName())
//                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(i);
        context.startService(new Intent(context, MyFirebaseMessagingService.class));

    }
}
