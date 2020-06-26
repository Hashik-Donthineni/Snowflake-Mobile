package org.torproject.snowflake.serialization;

import org.webrtc.DataChannel;

import okio.ByteString;

public class RelaySerialization {
    public static ByteString clientToTor(DataChannel.Buffer buffer) {
        return ByteString.of(buffer.data.asReadOnlyBuffer());
    }

    public static DataChannel.Buffer torToClient(ByteString byteString) {
        return new DataChannel.Buffer(byteString.asByteBuffer(), true);
    }
}
