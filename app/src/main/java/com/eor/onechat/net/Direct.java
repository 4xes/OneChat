package com.eor.onechat.net;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sse on 18.02.18.
 */

public class Direct {
    @Expose(deserialize = false)
    @SerializedName("to") String to;
    @Expose(serialize = false)
    @SerializedName("from") String from;

    @SerializedName("payload") Object payload;
    @SerializedName("type") Type type;
    public Direct(String to) {
        this.to = to;
    }
    public Direct(String to, Type type, Object payload) {
        this.to = to;
        this.type = type;
        this.payload = payload;
    }
    public enum Type {
        @SerializedName("sdp") SDP,
        @SerializedName("candy") CANDY,
        @SerializedName("bye") BYE
    }
}
