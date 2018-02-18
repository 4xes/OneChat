package com.eor.onechat.holders

import android.view.View
import com.eor.onechat.R
import com.eor.onechat.data.model.Message
import com.eor.onechat.view.GalleryView
import com.stfalcon.chatkit.messages.MessageHolders

class GalleryMessageViewHolder(itemView: View) : MessageHolders.IncomingTextMessageViewHolder<Message>(itemView) {

    //private val tvDuration: TextView = itemView.findViewById(R.id.duration)
    private val gallery: GalleryView = itemView.findViewById(R.id.gallery)

    override fun onBind(message: Message) {
        super.onBind(message)
        gallery.bind(message.places!!)
    }
}
