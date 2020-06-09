package org.torproject.snowflake;

import android.util.Log;

import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;


public class SimpleSdpObserver implements SdpObserver {
    private final String TAG = "SimpleSdpObserver";
    private final String location;

    public SimpleSdpObserver(String loc) {
        location = loc; //Remote or local
    }

    @Override
    public void onCreateSuccess(SessionDescription sessionDescription) {
        Log.d(TAG, "onCreateSuccess: " + location + ":\n" + sessionDescription.description);
    }

    @Override
    public void onSetSuccess() {
        Log.d(TAG, "onSetSuccess: " + location);
    }

    @Override
    public void onCreateFailure(String s) {
        Log.d(TAG, "onCreateFailure: " + location + ":" + s);
    }

    @Override
    public void onSetFailure(String s) {
        Log.d(TAG, "onSetFailure: " + location + ":" + s);
    }
}
