package com.eor.onechat.holders

import android.view.View
import android.widget.TextView
import com.android.kit.extensions.toDuration

import com.eor.onechat.R
import com.stfalcon.chatkit.messages.MessageHolders
import com.eor.onechat.data.model.Message
import com.stfalcon.chatkit.utils.DateFormatter

class OutVoiceMessageViewHolder(itemView: View) : MessageHolders.OutcomingTextMessageViewHolder<Message>(itemView) {

    private val tvDuration: TextView = itemView.findViewById(R.id.duration)
    private val tvTime: TextView = itemView.findViewById(R.id.time)

    override fun onBind(message: Message) {
        super.onBind(message)
        tvDuration.text = message.voice!!.duration.toDuration()
        tvTime.text = DateFormatter.format(message.createdAt, DateFormatter.Template.TIME)
    }
}
