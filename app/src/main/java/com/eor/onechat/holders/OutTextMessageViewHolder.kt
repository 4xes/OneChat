package com.eor.onechat.holders

import android.annotation.SuppressLint
import android.view.View

import com.eor.onechat.data.model.Message
import com.stfalcon.chatkit.messages.MessageHolders

class OutTextMessageViewHolder(itemView: View) : MessageHolders.OutcomingTextMessageViewHolder<Message>(itemView) {

    @SuppressLint("SetTextI18n")
    override fun onBind(message: Message) {
        super.onBind(message)

        time?.text = "${message.status} ${time.text}"
    }
}