package com.eor.onechat.net

import com.eor.onechat.data.model.Data
import com.eor.onechat.data.model.Places

class Receive(val chat_id: String, val text: String, val type: Type = Type.TEXT, val data: Data?, val places: Places) {

    enum class Type {
        BALANCE,
        TEXT,
        ACTIONS,
        PLACES,
        DATA
    }
}