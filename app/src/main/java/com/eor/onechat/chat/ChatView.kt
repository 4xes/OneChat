package com.eor.onechat.chat

import com.eor.onechat.data.model.Place
import io.reactivex.Action

interface ChatView {
    fun addMessageClient(message: String)
    fun addMessageBot(message: String)
    fun addMessagePlaces(place1: Place, place2: Place)
    fun addText(text: String)
    fun addTitle(text: String)
    fun addSubtitle(text: String)
    fun addData(text: String? = null, title: String? = null, subtitle: String? = null)
    fun addActions(text: String, action: Action?, text2: String? = null, action2: Action? = null)
}