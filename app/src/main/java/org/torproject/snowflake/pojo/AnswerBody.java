package org.torproject.snowflake.pojo;

import com.google.gson.annotations.SerializedName;

/*{
  Sid: [generated session id of proxy],
  Version: 1.1,
  Answer:
  {
    type: answer,
    sdp: [WebRTC SDP]
 }*/
public class AnswerBody {
    @SerializedName("Version")
    private final String VERSION = "1.1";
    @SerializedName("Sid")
    private String sid;
    @SerializedName("Answer")
    private String answerBodySDP;

    public AnswerBody(String s, String sdp) {
        sid = s;
        answerBodySDP = sdp;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getAnswerBodySDP() {
        return answerBodySDP;
    }

    public void setAnswerBodySDP(String answerBodySDP) {
        this.answerBodySDP = answerBodySDP;
    }
}
