package com.eor.onechat.net;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sse on 17.02.18.
 */

public class Auth {
    // request part
    @SerializedName("device_id") String deviceId;
    @SerializedName("name") String name;
    @SerializedName("photo") String photo;

    //response part
    @Expose(serialize = false)
    @SerializedName("result") public int result;
    @Expose(serialize = false)
    @SerializedName("user_id") public String userId;
    public Auth(String deviceId, String name) {
        this.deviceId = deviceId;
        this.name = name;
    }
    public Auth(int result, String userId) {
        this.result = result;
        this.userId = userId;
    }
}
