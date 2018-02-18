package com.eor.onechat.net;

import com.google.gson.JsonObject;

/**
 * Created by sse on 18.02.18.
 */

public interface ServerResponse {
    void onServerResponse(JsonObject jsonObject);
}
