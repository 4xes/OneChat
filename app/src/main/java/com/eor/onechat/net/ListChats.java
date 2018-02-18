package com.eor.onechat.net;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListChats {
    // request part
    @SerializedName("chat_ids")
    public List<String> chatIds;

    public ListChats(List<String> chatIds) {
        this.chatIds = chatIds;
    }
}
