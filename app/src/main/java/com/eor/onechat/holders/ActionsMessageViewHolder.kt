package com.eor.onechat.holders

import android.view.View
import com.eor.onechat.R
import com.eor.onechat.data.model.Message
import com.eor.onechat.widgets.ActionsView
import com.stfalcon.chatkit.messages.MessageHolders

class ActionsMessageViewHolder(itemView: View) : MessageHolders.IncomingTextMessageViewHolder<Message>(itemView) {

    private val actions: ActionsView = itemView.findViewById(R.id.actions)

    override fun onBind(message: Message) {
        super.onBind(message)
        actions.bind(message.actions!!)
    }
}
