package com.eor.onechat.net;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sse on 18.02.18.
 */

public class Direct {
    @SerializedName("to") String to;
    public Direct(String to) {
        this.to = to;
    }
}
