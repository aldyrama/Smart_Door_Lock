package org.d3ifcool.smart.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.github.arturogutierrez.Badges;
import com.github.arturogutierrez.BadgesNotSupportedException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.d3ifcool.smart.Data;
import org.d3ifcool.smart.Home.MainActivity;
import org.d3ifcool.smart.Home.StreamingActivity;
import org.d3ifcool.smart.R;

import java.util.Map;

import static android.support.constraint.Constraints.TAG;
import static android.view.View.inflate;

public class MyFirebaseMessagingService extends Service{

        private FirebaseDatabase database;
        private DatabaseReference Door, Doors, guest;
        Pair<DatabaseReference, ValueEventListener> mListener;
        FirebaseAuth auth;
        FirebaseUser firebaseUser;
        String name, doorName, housename;
        int lock;
        Vibrator vibrator;
        MediaPlayer mediaPlayer;

        @Override
        public void onCreate() {
                super.onCreate();

                auth = FirebaseAuth.getInstance();
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                database = FirebaseDatabase.getInstance();
                guestOn();
                triggerDoors();
                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        }

        public void guestOn(){
                try {

                        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getEmail().replace(".", ","))
                                .child("Notifications");
                        reference.keepSynced(false);
                        reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        try {
                                                String notif = dataSnapshot.child("guest").getValue(String.class);
                                                Log.d("notif", "data " + notif);
                                                if (notif.equals("enable")) {

                                                        triggerHouses();

                                                } else if (notif.equals("disable")){
                                                        reference.child("guest").getRef().removeValue();

                                                }
                                        } catch (Exception e) {
                                        }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                        });
                }catch (Exception e){}
        }

        private void triggerHouses() {

                guest = FirebaseDatabase.getInstance().getReference();
                guest.keepSynced(false);
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
                                                                name = dataSnapshot.child("name").getValue(String.class);

                                                                if (statusGuset == true) {

                                                                        notificationDoorKnock();

                                                                }

                                                                return;
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

        private void triggerDoors() {
                Door = FirebaseDatabase.getInstance().getReference();
                Door.keepSynced(false);
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

                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("RssPullService");

                Intent resultIntent = new Intent(this, MainActivity.class);
                PendingIntent resultPandingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder notification = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
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

                vibrator.vibrate(600);
                play();
//                Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                notification.setSound(uri);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(1, notification.build());
        }

        private void notificationDoorKnock() {

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

                vibrator.vibrate(600);
                play();
//                Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                notification.setSound(uri);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(1, notification.build());



        }

        public void play(){
                if (mediaPlayer == null){
                        mediaPlayer = MediaPlayer.create(this, R.raw.iphone_notif);

                }

                mediaPlayer.start();
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

        public void countNotif(View view){
                try {

                        Badges.setBadge(MyFirebaseMessagingService.this, 1);

                }catch (BadgesNotSupportedException e){
                        Toast.makeText(this, "That was error!", Toast.LENGTH_SHORT).show();

                }
        }


//        @Override
//        public void refresh() {
//
//        }
//
//        @Override
//        public void notifGuest(String notif) {
//                notificationDoorKnock(notif);
//        }


}
