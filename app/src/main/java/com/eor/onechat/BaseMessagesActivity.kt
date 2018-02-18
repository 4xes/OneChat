package com.eor.onechat

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.android.kit.base.BaseActivity
import com.android.kit.extensions.toast
import com.eor.onechat.calls.Permissions
import com.eor.onechat.data.model.Message
import com.squareup.picasso.Picasso
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessagesListAdapter
import java.text.SimpleDateFormat
import java.util.*

abstract class BaseMessagesActivity : BaseActivity(), MessagesListAdapter.SelectionListener, MessagesListAdapter.OnLoadMoreListener {

    protected lateinit var imageLoader: ImageLoader
    protected lateinit var messagesAdapter: MessagesListAdapter<Message>

    private var menu: Menu? = null
    private var selectionCount: Int = 0
//    private var lastLoadedDate: Date? = null

    private val messageStringFormatter: MessagesListAdapter.Formatter<Message>
        get() = MessagesListAdapter.Formatter { message ->
            val createdAt = SimpleDateFormat("MMM d, EEE 'at' h:mm a", Locale.getDefault())
                    .format(message.createdAt)

            var text: String? = message.text
            if (text == null) text = "[attachment]"

            String.format(Locale.getDefault(), "%s: %s (%s)",
                    message.user.name, text, createdAt)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageLoader = ImageLoader { imageView, url -> Picasso.with(this).load(url).into(imageView) }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.chat_actions_menu, menu)
        onSelectionChanged(0)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> messagesAdapter.deleteSelectedMessages()
            R.id.action_copy -> {
                messagesAdapter.copySelectedMessagesText(this, messageStringFormatter, true)
                toast(R.string.copied_message)
            }
        }
        return true
    }

    override fun onBackPressed() {
        if (selectionCount == 0) {
            super.onBackPressed()
        } else {
            messagesAdapter.unselectAllItems()
        }
    }

    override fun onLoadMore(page: Int, totalItemsCount: Int) {
        if (totalItemsCount < TOTAL_MESSAGES_COUNT) {
            loadMessages()
        }
    }

    override fun onSelectionChanged(count: Int) {
        this.selectionCount = count
        menu!!.findItem(R.id.action_delete).isVisible = count > 0
        menu!!.findItem(R.id.action_copy).isVisible = count > 0
    }

    private fun loadMessages() {
//        val messages = MockMessagesFabric.getMessages(lastLoadedDate)
//        lastLoadedDate = messages[messages.size - 1].createdAt
//        messagesAdapter.addToEnd(messages, false)
    }

    companion object {

        private const val TOTAL_MESSAGES_COUNT = 100
    }
}
