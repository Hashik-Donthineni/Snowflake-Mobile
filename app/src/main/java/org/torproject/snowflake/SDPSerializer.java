package org.torproject.snowflake;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.SessionDescription;

public class SDPSerializer {

    public static SessionDescription deserializeOffer(String SDP) throws JSONException {
        return new SessionDescription(SessionDescription.Type.OFFER,
                new JSONObject(SDP).get("sdp").toString());
    }

    public static String serializeAnswer(SessionDescription SDP){
        return SDP.description;
    }
}
