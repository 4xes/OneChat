package com.eor.onechat.net;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

/**
 * Created by sse on 17.02.18.
 */

public class Proto {
    @SerializedName("uuid") String uuid;
    @SerializedName("method") Method method;
    @SerializedName("data") Object data;

    static enum Method {
        @SerializedName("auth") AUTH,
        @SerializedName("direct") DIRECT,
        @SerializedName("receive") RECEIVE
     }
     Proto(Method method, Object data) {
        this.uuid = UUID.randomUUID().toString();
        this.method = method;
        this.data = data;
     }
    Proto(String uuid, Method method, Object data) {
        this.uuid = uuid;
        this.method = method;
        this.data = data;
    }
}
