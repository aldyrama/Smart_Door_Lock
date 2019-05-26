package org.d3ifcool.smart.Onvif;

import android.util.Log;

import org.videolan.libvlc.MediaPlayer;

import java.lang.ref.WeakReference;

class MyPlayerListener implements MediaPlayer.EventListener {

    private static String TAG = "PlayerListener";
    private WeakReference<Cam> mOwner;


    public MyPlayerListener(Cam owner) {
        mOwner = new WeakReference<Cam>(owner);
    }

    @Override
    public void onEvent(MediaPlayer.Event event) {
        Cam player = mOwner.get();

        switch(event.type) {
            case MediaPlayer.Event.EndReached:
                Log.d(TAG, "MediaPlayerEndReached");
//                player.releasePlayer();
                break;
            case MediaPlayer.Event.Playing:
            case MediaPlayer.Event.Paused:
            case MediaPlayer.Event.Stopped:
            default:
                break;
        }
    }
}
