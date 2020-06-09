package org.torproject.snowflake.pojo;

import com.google.gson.annotations.SerializedName;

/*{
  Sid: [generated session id of proxy],
  Version: 1.1,
  Type: "mobile"
}*/
public class OfferRequestBody {
    private String sid;
    @SerializedName("Version")
    private final String VERSION = "1.1";
    @SerializedName("Type")
    private final String TYPE = "mobile";

    public OfferRequestBody(String sid){
        this.sid = sid;
    }
}