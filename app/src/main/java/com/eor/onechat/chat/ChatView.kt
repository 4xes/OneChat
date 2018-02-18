package com.eor.onechat.chat

import com.eor.onechat.data.model.Place

interface ChatView {
    fun addMessageClient(message: String)
    fun addMessageBot(message: String)
    fun addMessagePlaces(place1: Place, place2: Place)
    fun addText(text: String)
    fun addTitle(text: String)
    fun addSubtitle(text: String)
    fun addData(text: String? = null, title: String? = null, subtitle: String? = null)
}