package org.d3ifcool.smart.Notification;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.d3ifcool.smart.Home.MainActivity;
import org.d3ifcool.smart.R;
import org.d3ifcool.smart.Setting.SettingActivity;

import static org.d3ifcool.smart.Setting.SettingActivity.SWITCH1;
import static org.d3ifcool.smart.Setting.SettingActivity.SWITCH2;
import static org.d3ifcool.smart.Setting.SettingActivity.SWITCH3;


public class MyFirebaseMessagingService extends Service implements SettingActivity.SettingInterface {

        private FirebaseDatabase database;
        private DatabaseReference Door, guest;
        private FirebaseAuth auth;
        private FirebaseUser firebaseUser;
        private String name, doorName, housename;
        private int lock;
        private Vibrator vibrator;
        private MediaPlayer mediaPlayer;
        private DatabaseReference refOnOff;
        private Boolean isInBackground;
        private SharedPreferences prefs;
        private SharedPreferences.Editor editor;
        private boolean onGuest, onDoor, onThief, thief;

        @Override
        public void onCreate() {
                super.onCreate();

                auth = FirebaseAuth.getInstance();

                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                database = FirebaseDatabase.getInstance();

                prefs = getSharedPreferences(SettingActivity.GLOBAL_SHARED_PREFS, MODE_PRIVATE);

                try {

                        refOnOff = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getEmail().replace(".", ","))
                                .child("Notifications");

                }catch (Exception e){


                }

                onGuest = prefs.getBoolean(SWITCH1, true);

                onDoor = prefs.getBoolean(SWITCH2, true);

                onThief = prefs.getBoolean(SWITCH3, true);

                ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
                ActivityManager.getMyMemoryState(myProcess);
                isInBackground = myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
                if(isInBackground) {

                        triggerHouses();

                        triggerDoors();
                }

//                }else{
//
//                }

//                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        }

        public void triggerHouses() {

                guest = FirebaseDatabase.getInstance().getReference();
                guest.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
                                try {

                                        for (DataSnapshot guesSnapshot : dataSnapshot.child("Users").child(firebaseUser.getEmail().replace(".", ","))
                                                .child("Houses").getChildren()) {

                                                String kode_device = guesSnapshot.getValue(String.class);

                                                Log.d("devices", "onDataChange: " + kode_device);

                                                guest.child("Devices").child(kode_device).addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                boolean statusGuset = dataSnapshot.child("guest").getValue(boolean.class);

                                                                boolean thiefDetect = dataSnapshot.child("thief").getValue(boolean.class);

                                                                Log.d("guest ", " :" + statusGuset);

                                                                name = dataSnapshot.child("name").getValue(String.class);

                                                                if (statusGuset) {

                                                                        notificationDoorKnock();

                                                                }

                                                                if (thiefDetect){

                                                                        thiefNotification();

                                                                }

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                });

                                        }

                                }catch (Exception e){}

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                });

        }

        public void triggerDoors() {

                Door = FirebaseDatabase.getInstance().getReference();
                Door.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                try {

                                        for (DataSnapshot guesSnapshot : dataSnapshot.child("Users").child(firebaseUser.getEmail().replace(".", ","))
                                                .child("Houses").getChildren()) {

                                                final String kode_device = guesSnapshot.getValue(String.class);

                                                Log.d("devices", "onDataChange: " + kode_device);

                                                Door.child("Devices").child(kode_device).addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                housename = dataSnapshot.child("name").getValue(String.class);

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }

                                                });

                                                for (DataSnapshot doorSnapshot : dataSnapshot.child("Devices").child(kode_device).child("Doors").getChildren()) {
                                                        String pin = doorSnapshot.getKey();
                                                        Log.d("pin", "onDataChange: " + pin);

                                                        Door.child("Devices").child(kode_device).child("Doors").child(pin).addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                        try {

                                                                                lock = dataSnapshot.child("doorLock").getValue(int.class);

                                                                                doorName = dataSnapshot.child("doorName").getValue(String.class);



                                                                                if (lock == 1) {

                                                                                        notificationDoorOpen();

                                                                                }

                                                                        } catch (Exception e) {

                                                                        }

                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }

                                                        });

                                                }

                                        }

                                }catch (Exception e){}

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                });

        }

        private void notificationDoorOpen() {

                onDoor = prefs.getBoolean(SWITCH2, true);

                if (onDoor) {

                        IntentFilter intentFilter = new IntentFilter();

                        intentFilter.addAction("RssPullService");

                        Intent resultIntent = new Intent(this, MainActivity.class);

                        PendingIntent resultPandingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        NotificationCompat.Builder notification = new NotificationCompat.Builder(this)

                                .setDefaults(NotificationCompat.DEFAULT_ALL)

                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo_lock))

                                .setSmallIcon(R.drawable.logo_lock)

                                .setContentTitle("Door")

                                .setContentText("the " + doorName + " door of the " + housename + " house is open!")

                                .setVibrate(new long[]{0, 500, 1000})

                                .setContentIntent(resultPandingIntent)

                                .setAutoCancel(true)

                                .setLights(0xff0000ff, 300, 1000) // blue color

                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

                                .setPriority(Notification.PRIORITY_MAX);

                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                        notificationManager.notify(1, notification.build());

                }

        }

        private void notificationDoorKnock() {

                onGuest = prefs.getBoolean(SWITCH1, true);

                if (onGuest) {

                        IntentFilter intentFilter = new IntentFilter();

                        intentFilter.addAction("RssPullS3ervice");

                        Intent resultIntent = new Intent(this, MainActivity.class);

                        PendingIntent resultPandingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        NotificationCompat.Builder notification = (NotificationCompat.Builder) new NotificationCompat.Builder(this)

                                .setDefaults(NotificationCompat.DEFAULT_ALL)

                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo_lock))

                                .setSmallIcon(R.drawable.logo_lock)

                                .setContentTitle("House")

                                .setContentText("guest in the " + name + " house!")

                                .setVibrate(new long[]{0, 500, 1000})

                                .setContentIntent(resultPandingIntent)

                                .setAutoCancel(true)

                                .setLights(0xff0000ff, 300, 1000) // blue color

                                .setPriority(Notification.PRIORITY_MAX);

                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                        notificationManager.notify(1, notification.build());

                }

        }

        private void thiefNotification() {

                onThief = prefs.getBoolean(SWITCH3, true);

                if (onThief) {

                        IntentFilter intentFilter = new IntentFilter();

                        intentFilter.addAction("RssPullS3ervice");

                        Intent resultIntent = new Intent(this, MainActivity.class);

                        PendingIntent resultPandingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        NotificationCompat.Builder notification = (NotificationCompat.Builder) new NotificationCompat.Builder(this)

                                .setDefaults(NotificationCompat.DEFAULT_ALL)

                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo_lock))

                                .setSmallIcon(R.drawable.logo_lock)

                                .setContentTitle("House")

                                .setContentText("thief in the " + name + " house!")

                                .setVibrate(new long[]{0, 500, 1000})

                                .setContentIntent(resultPandingIntent)

                                .setAutoCancel(true)

                                .setLights(0xff0000ff, 300, 1000) // blue color

                                .setPriority(Notification.PRIORITY_MAX);

                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                        notificationManager.notify(1, notification.build());

                }

        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {

                return null;

        }

        @Override
        public void onDestroy() {

                super.onDestroy();

        }

        @Override
        public void refresh() {

        }
}
