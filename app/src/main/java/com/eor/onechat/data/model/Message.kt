package com.eor.onechat.data.model

import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.MessageContentType
import java.util.*

class Message @JvmOverloads constructor(private val id: String, private val user: User, private var text: String? = "", private var createdAt: Date? = Date(), var places: Places? = null, var data: Data? = null) : IMessage, MessageContentType.Image,
        MessageContentType {

    private var image: Image? = null

    val status: String
        get() = "Sent"

    override fun getId(): String {
        return id
    }

    override fun getText(): String? {
        return text
    }

    override fun getCreatedAt(): Date? {
        return createdAt
    }

    override fun getUser(): User {
        return this.user
    }

    override fun getImageUrl(): String? {
        return if (image == null) null else image!!.url
    }


    companion object {

        const val CONTENT_PLACES: Byte = 1
        const val CONTENT_DATA: Byte = 2

        private fun uniqueId() = java.lang.Long.toString(UUID.randomUUID().leastSignificantBits)

        fun message(user: User, text: String) = Message(uniqueId(), user, text)

        fun botMessage(text: String) = message(User.BOT, text)

        fun userMessage(text: String) = message(User.ME, text)

        fun gallery() = Message(Message.uniqueId(), User.BOT, places = Places.test())

        fun data() = Message(Message.uniqueId(), User.BOT, data = Data.test())

    }

}
