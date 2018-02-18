package com.eor.onechat.holders

import android.view.View
import com.eor.onechat.R
import com.eor.onechat.data.model.Message
import com.eor.onechat.widgets.DataView
import com.stfalcon.chatkit.messages.MessageHolders

class DataMessageViewHolder(itemView: View) : MessageHolders.IncomingTextMessageViewHolder<Message>(itemView) {

    private val data: DataView = itemView.findViewById(R.id.data)

    override fun onBind(message: Message) {
        super.onBind(message)
        data.bind(message.data!!)
    }
}
