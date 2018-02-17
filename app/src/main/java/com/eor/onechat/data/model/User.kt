package com.eor.onechat.data.model

import com.stfalcon.chatkit.commons.models.IUser

class User(private val id: String, private val name: String, private val avatar: String, val isOnline: Boolean) : IUser {

    override fun getId(): String {
        return id
    }

    override fun getName(): String {
        return name
    }

    override fun getAvatar(): String {
        return avatar
    }

    companion object {
        val ME_ID = "0"
        val BOT_ID = "1"
        val ME = User(ME_ID, "Me", "", true)
        val BOT = User(BOT_ID, "EORA", "", true)
    }
}
