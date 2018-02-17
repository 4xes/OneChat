package com.eor.onechat.data.mock

import com.eor.onechat.data.model.Message
import com.eor.onechat.data.model.User

import java.util.ArrayList
import java.util.Calendar
import java.util.Date

object MockMessagesFabric {
    val imageMessage: Message
        get() {
            val message = Message(MockData.randomId, user, null)
            message.setImage(Message.Image(MockData.randomImage))
            return message
        }

    val voiceMessage: Message
        get() {
            val message = Message(MockData.randomId, user, null)
            message.voice = Message.Voice("http://example.com", (MockData.rnd.nextInt(200) + 30).toLong())
            return message
        }

    val textMessage: Message
        get() = getTextMessage(MockData.randomMessage)

    fun getTextMessage(text: String): Message {
        return Message(MockData.randomId, user, text)
    }

    fun getMessages(startDate: Date?): ArrayList<Message> {
        val messages = ArrayList<Message>()
        for (i in 0..9/*days count*/) {
            val countPerDay = MockData.rnd.nextInt(5) + 1

            for (j in 0 until countPerDay) {
                val message: Message
                if (i % 2 == 0 && j % 3 == 0) {
                    message = imageMessage
                } else {
                    message = textMessage
                }

                val calendar = Calendar.getInstance()
                if (startDate != null) calendar.time = startDate
                calendar.add(Calendar.DAY_OF_MONTH, -(i * i + 1))

                message.setCreatedAt(calendar.time)
                messages.add(message)
            }
        }
        return messages
    }

    private val user: User
        get() {
            val even = MockData.rnd.nextBoolean()
            return User(
                    if (even) "0" else "1",
                    if (even) MockData.names[0] else MockData.names[1],
                    if (even) MockData.avatars[0] else MockData.avatars[1],
                    true)
        }
}
