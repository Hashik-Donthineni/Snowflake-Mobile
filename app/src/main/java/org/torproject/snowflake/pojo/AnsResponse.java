package org.torproject.snowflake.pojo;

import com.google.gson.annotations.SerializedName;

/*If the client retrieved the answer: HTTP 200 OK

        {
        Status: "success"
        }
If the client left: HTTP 200 OK
        {
        Status: "client gone"
        }
3) If the request is malformed: HTTP 400 BadRequest*/

public class AnsResponse {
    @SerializedName("Status")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
