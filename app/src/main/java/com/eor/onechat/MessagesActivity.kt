package com.eor.onechat

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import com.eor.onechat.calls.CallActivity
import com.eor.onechat.data.model.Message
import com.eor.onechat.data.model.User
import com.eor.onechat.holders.GalleryMessageViewHolder
import com.eor.onechat.holders.InTextMessageViewHolder
import com.eor.onechat.holders.OutTextMessageViewHolder
import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.chatkit.messages.MessagesList
import com.stfalcon.chatkit.messages.MessagesListAdapter
import kotlinx.android.synthetic.main.activity_messages.*
import kotlinx.android.synthetic.main.layout_actions.*

class MessagesActivity : BaseMessagesActivity(), MessageInput.InputListener, MessageInput.AttachmentsListener, MessageHolders.ContentChecker<Message>, DialogInterface.OnClickListener {


    private lateinit var messagesList: MessagesList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        setSupportActionBar(toolbar)

        messagesList = findViewById(R.id.messagesList)
        initAdapter()

        val input = findViewById<MessageInput>(R.id.input)
        input.setInputListener(this)
        input.setAttachmentsListener(this)

        action_call.setOnClickListener {
            startActivity(Intent(this, CallActivity::class.java))
        }
    }

    override fun onSubmit(input: CharSequence): Boolean {
        messagesAdapter.addToStart(Message.userMessage(input.toString()), true)
        return true
    }

    override fun onAddAttachments() {
        AlertDialog.Builder(this)
                .setItems(R.array.view_types_dialog, this)
                .show()
    }

    override fun hasContentFor(message: Message, type: Byte): Boolean {
        when (type) {
            Message.CONTENT_PLACES -> return (message.places != null)
        }
        return false
    }


    override fun onClick(dialogInterface: DialogInterface, i: Int) {
//        when (i) {
//            0 -> messagesAdapter.addToStart(MockMessagesFabric.imageMessage, true)
//            1 -> messagesAdapter.addToStart(MockMessagesFabric.voiceMessage, true)
//        }
    }

    private fun initAdapter() {
        val holders = MessageHolders()
                .setIncomingTextConfig(InTextMessageViewHolder::class.java, R.layout.item_incoming_text_message)
                .setOutcomingTextConfig(OutTextMessageViewHolder::class.java, R.layout.item_outcoming_text_message)
                .registerContentType(
                        Message.CONTENT_PLACES,
                        GalleryMessageViewHolder::class.java,
                        R.layout.item_gallery_message,
                        GalleryMessageViewHolder::class.java,
                        R.layout.item_gallery_message,
                        this)


        messagesAdapter = MessagesListAdapter(User.ME_ID, holders, super.imageLoader)
        messagesAdapter.enableSelectionMode(this)
        messagesAdapter.setLoadMoreListener(this)
        messagesAdapter.addToStart(Message.botMessage("Привет"), true)
        messagesAdapter.addToStart(Message.userMessage("Привет"), true)
        messagesAdapter.addToStart(Message.gallery(), true)

        messagesList.setAdapter(super.messagesAdapter)
        messagesList.drawingTime
    }

}
