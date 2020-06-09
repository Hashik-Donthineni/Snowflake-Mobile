package org.torproject.snowflake.pojo;

import com.google.gson.annotations.SerializedName;

/*If a client is matched:
HTTP 200 OK
{
  Status: "client match",
  {
    type: offer,
    sdp: [WebRTC SDP]
  }
}

If a client is not matched:
HTTP 200 OK
{
    Status: "no match"
}*/
public class SDPOfferResponse {
    @SerializedName("Status")
    private String status;

    @SerializedName("Offer")
    private String offer;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }
}