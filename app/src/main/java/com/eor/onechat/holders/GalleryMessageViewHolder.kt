package com.eor.onechat.holders

import android.view.View
import com.eor.onechat.R
import com.eor.onechat.data.model.Message
import com.eor.onechat.widgets.PlacesView
import com.stfalcon.chatkit.messages.MessageHolders

class GalleryMessageViewHolder(itemView: View) : MessageHolders.IncomingTextMessageViewHolder<Message>(itemView) {

    private val gallery: PlacesView = itemView.findViewById(R.id.gallery)

    override fun onBind(message: Message) {
        super.onBind(message)
        gallery.bind(message.places!!)
    }
}
