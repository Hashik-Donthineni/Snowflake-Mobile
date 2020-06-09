package org.torproject.snowflake.pojo;

import org.json.JSONException;
import org.json.JSONObject;

/*SDP answer from AnswerBody.java
Answer:
{
    type: answer,
    sdp: [WebRTC SDP]
}
*/
public class AnswerBodySDP {
    private final String TYPE = "answer";
    private String sdp;

    public String getSdp() {
        return sdp;
    }

    public void setSdp(String sdp) {
        this.sdp = sdp;
    }

    public String toString(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", TYPE);
            jsonObject.put("sdp", sdp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
