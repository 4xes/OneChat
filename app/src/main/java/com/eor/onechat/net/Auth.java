package com.eor.onechat.net;

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
    @SerializedName("result") transient int result;
    @SerializedName("user_id") transient String userId;
    Auth(String deviceId, String name) {
        this.deviceId = deviceId;
        this.name = name;
    }
}
