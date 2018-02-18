package com.eor.onechat.holders

import android.view.View
import com.eor.onechat.data.model.Message
import com.stfalcon.chatkit.messages.MessageHolders

class InTextMessageViewHolder(itemView: View) : MessageHolders.IncomingTextMessageViewHolder<Message>(itemView) {

    //private val onlineIndicator: View? = itemView.findViewById(R.id.onlineIndicator)

    override fun onBind(message: Message) {
        super.onBind(message)

        val isOnline = message.user.isOnline
//        if (isOnline) {
//            onlineIndicator?.setBackgroundResource(R.drawable.shape_bubble_online)
//        } else {
//            onlineIndicator?.setBackgroundResource(R.drawable.shape_bubble_offline)
//        }
    }
}