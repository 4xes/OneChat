package com.eor.onechat.net;

import com.google.gson.annotations.SerializedName;

public class MessageSend {
    // request part
    @SerializedName("message_id")
    public String message_id;

    public MessageSend(String message_id) {
        this.message_id = message_id;
    }
}
