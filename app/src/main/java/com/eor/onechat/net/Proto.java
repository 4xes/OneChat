package com.eor.onechat.net;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

/**
 * Created by sse on 17.02.18.
 */

public class Proto {
    @SerializedName("uuid") String uuid;
    @SerializedName("method") Method method;
    @SerializedName("data") Object data;
    @Expose(serialize = false)
    @SerializedName("from") public String from;

    public static enum Method {
        @SerializedName("auth") AUTH,
        @SerializedName("direct") DIRECT,
        @SerializedName("receive")RECEIVE,
        @SerializedName("chat.getList")CHAT_GET_LIST,
        @SerializedName("chat.sendMessage")CHAT_SEND_MESSAGE,
        @SerializedName("chat.receive")CHAT_RECEIVE
     }
     public Proto(Method method, Object data) {
        this.uuid = UUID.randomUUID().toString();
        this.method = method;
        this.data = data;
     }
    public Proto(String uuid, Method method, Object data) {
        this.uuid = uuid;
        this.method = method;
        this.data = data;
    }
}
