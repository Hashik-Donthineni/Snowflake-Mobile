package org.torproject.snowflake.interfaces;

import org.webrtc.DataChannel;

/**
 * Callback to MyPersistentService when even happens form PeerConnectionObserver.
 */
public interface PeerConnectionObserverCallback {
    void onIceGatheringFinish();

    void onMessage(DataChannel.Buffer buffer);

    void onDataChannel(DataChannel dataChannel);

    void dataChannelStateChange(final DataChannel.State state);

    void iceConnectionFailed();
}
