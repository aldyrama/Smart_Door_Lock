package org.d3ifcool.smart.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.github.arturogutierrez.Badges;
import com.github.arturogutierrez.BadgesNotSupportedException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.d3ifcool.smart.Home.MainActivity;
import org.d3ifcool.smart.Home.StreamingActivity;
import org.d3ifcool.smart.R;

import static android.support.constraint.Constraints.TAG;

public class MyFirebaseMessagingService extends Service {

        private FirebaseDatabase database;
        private DatabaseReference DoorA, DoorB, KnockA, KnockB;
        Pair<DatabaseReference, ValueEventListener> mListener;
        @Override
        public void onCreate() {
                super.onCreate();

                database = FirebaseDatabase.getInstance();
                DoorA = database.getReference("DoorA_Status");
                DoorB = database.getReference("DoorB_Status");
                KnockA = database.getReference("Knock_SensorA");
                KnockB = database.getReference("DoorB_Status");
                readFirebase();

//                ValueEventListener valueEventListener = DoorA.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                int value = dataSnapshot.getValue(int.class);
//                                Log.d(TAG, "Value is: " + value);
//
//                                if (Float.valueOf(value) == 1){
//                                        notificationDoorOpen();
//                                }
//
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                });
//
//                mListener = new Pair<>(DoorA, valueEventListener);
//
        }

        private void readFirebase() {
                DoorA.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int value = dataSnapshot.getValue(int.class);
                                Log.d(TAG, "Value isA : " + value);

                                if (Float.valueOf(value) == 1){
                                        notificationDoorAOpen();
                                }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.w(TAG, "Failed to read value.", databaseError.toException());

                        }
                });

                DoorB.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int value = dataSnapshot.getValue(int.class);
                                Log.d(TAG, "Value isA : " + value);

                                if (Float.valueOf(value) == 1){
                                        notificationDoorBOpen();
                                }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.w(TAG, "Failed to read value.", databaseError.toException());

                        }
                });

                KnockA.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int value = dataSnapshot.getValue(int.class);
                                Log.d(TAG,"Value knockA is :" + value);

                                if (Float.valueOf(value) == 1){
                                        notificationDoorKnock();

                                }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.w(TAG, "Failed to read value.", databaseError.toException());
                        }
                });

//                try {
//                        Badges.setBadge(MyFirebaseMessagingService.this, 1);
//
//                }catch (BadgesNotSupportedException e){
//                        Toast.makeText(this, "That was error!", Toast.LENGTH_SHORT).show();
//                }

        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
                try {

                }catch (Exception e){
                        e.printStackTrace();
                }
                return super.onStartCommand(intent, flags, startId);
        }



        private void notificationDoorAOpen() {

                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("RssPullService");

                Intent resultIntent = new Intent(this, MainActivity.class);
                PendingIntent resultPandingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder notification = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo_lock))
                        .setSmallIcon(R.drawable.logo_lock)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText("Your front door is open")
                        .setVibrate(new long[]{0, 500, 1000})
                        .setContentIntent(resultPandingIntent)
                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_MAX);

                Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                notification.setSound(uri);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(1, notification.build());
        }

        private void notificationDoorBOpen() {

                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("RssPullService");

                Intent resultIntent = new Intent(this, MainActivity.class);
                PendingIntent resultPandingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder notification = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo_lock))
                        .setSmallIcon(R.drawable.logo_lock)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText("Your back door is open")
                        .setVibrate(new long[]{0, 500, 1000})
                        .setContentIntent(resultPandingIntent)
                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_MAX);

                Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                notification.setSound(uri);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(1, notification.build());
        }

        private void notificationDoorKnock() {

                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("RssPullService");

                Intent resultIntent = new Intent(this, StreamingActivity.class);
                PendingIntent resultPandingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder notification = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo_lock))
                        .setSmallIcon(R.drawable.logo_lock)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText("Guest")
                        .setVibrate(new long[]{0, 500, 1000})
                        .setContentIntent(resultPandingIntent)
                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_MAX);

                Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                notification.setSound(uri);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(1, notification.build());



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
}
