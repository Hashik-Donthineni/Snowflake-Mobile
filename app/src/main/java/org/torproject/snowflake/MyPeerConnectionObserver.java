package org.torproject.snowflake;

import android.util.Log;

import org.torproject.snowflake.interfaces.PeerConnectionObserverCallback;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.RtpReceiver;

public class MyPeerConnectionObserver implements PeerConnection.Observer {
    private final String TAG;
    private PeerConnectionObserverCallback peerconnectionObserverCallback;

    public MyPeerConnectionObserver(String tag, PeerConnectionObserverCallback callback) {
        TAG = tag + ": MyPeerConnectionObserver: ";
        this.peerconnectionObserverCallback = callback;
    }

    @Override
    public void onSignalingChange(PeerConnection.SignalingState signalingState) {
        Log.d(TAG, "onSignalingChange: " + signalingState);
    }

    @Override
    public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
        Log.d(TAG, "onIceConnectionChange: " + iceConnectionState);
        //TODO:Handle Connection Failure.
    }

    @Override
    public void onIceConnectionReceivingChange(boolean b) {
        Log.d(TAG, "onIceConnectionReceivingChange: ");
    }

    @Override
    public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
        Log.d(TAG, "onIceGatheringChange: " + iceGatheringState);
        if (iceGatheringState.compareTo(PeerConnection.IceGatheringState.COMPLETE) == 0)
            peerconnectionObserverCallback.onIceGatheringFinish();
    }

    @Override
    public void onIceCandidate(IceCandidate iceCandidate) {
        //Fired after each ICE candidate is gathered. AKA ICE trickling.
        Log.d(TAG, "onIceCandidate: SDP:" + iceCandidate.sdp);
    }

    @Override
    public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
        Log.d(TAG, "onIceCandidatesRemoved: ");
    }

    @Override
    public void onAddStream(MediaStream mediaStream) {
        Log.d(TAG, "onAddStream: ");
    }

    @Override
    public void onRemoveStream(MediaStream mediaStream) {
        Log.d(TAG, "onRemoveStream: ");
    }

    @Override
    public void onDataChannel(DataChannel dataChannel) {
        Log.d(TAG, "onDataChannel: State: " + dataChannel.state() + " Registering Observer...");
        dataChannel.registerObserver(new DataChannel.Observer() {
            @Override
            public void onBufferedAmountChange(long l) {

            }

            @Override
            public void onStateChange() {
                peerconnectionObserverCallback.dataChannelStateChange(dataChannel.state());
            }

            @Override
            public void onMessage(DataChannel.Buffer buffer) {
                Log.d(TAG, "onMessage: Received");
                peerconnectionObserverCallback.onMessage(buffer);
            }
        });
        peerconnectionObserverCallback.onDataChannel(dataChannel);
    }

    @Override
    public void onRenegotiationNeeded() {
        Log.d(TAG, "onRenegotiationNeeded: ");
    }

    @Override
    public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
        Log.d(TAG, "onAddTrack: ");
    }
}
