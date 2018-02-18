package com.eor.onechat

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import com.eor.onechat.calls.CallActivity
import com.eor.onechat.calls.Permissions
import com.eor.onechat.chat.ChatView
import com.eor.onechat.data.model.Message
import com.eor.onechat.data.model.Place
import com.eor.onechat.data.model.User
import com.eor.onechat.holders.*
import com.eor.onechat.net.Auth
import com.eor.onechat.net.Proto
import com.eor.onechat.net.ServerResponse
import com.eor.onechat.net.WebSocketClient
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.chatkit.messages.MessagesList
import com.stfalcon.chatkit.messages.MessagesListAdapter
import io.reactivex.Action
import kotlinx.android.synthetic.main.activity_messages.*
import kotlinx.android.synthetic.main.layout_actions.*
import timber.log.Timber

class MessagesActivity : BaseMessagesActivity(), MessageInput.InputListener, MessageInput.AttachmentsListener, MessageHolders.ContentChecker<Message>, DialogInterface.OnClickListener, ChatView, WebSocketClient.IDirect {

    private lateinit var messagesList: MessagesList
    private lateinit var webSocketClient: WebSocketClient
    private var auth: Auth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Permissions.requestMultiplePermissions(this, false)
        setContentView(R.layout.activity_messages)
        setSupportActionBar(toolbar)

        messagesList = findViewById(R.id.messagesList)
        initAdapter()

        val input = findViewById<MessageInput>(R.id.input)
        input.setInputListener(this)
        input.setAttachmentsListener(this)

        webSocketClient = WebSocketClient(this)

        val authResponse = ServerResponse { jsonObject ->
            val gson = Gson()
            val authResponse = gson.fromJson(jsonObject, Auth::class.java)
            if (authResponse.result == 1) {
                auth = authResponse
                Timber.d("auth succeed, userId %s", authResponse.userId)
            } else {
                Timber.d("auth failed")
            }
        }

        val androidId = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        val authReq = Auth(androidId, "Robot Androidovich")

        webSocketClient.send(Proto.Method.AUTH, authReq, authResponse)

        action_call.setOnClickListener {
            var intent = Intent(this, CallActivity::class.java)
            intent.setAction("OFFER")
            intent.putExtra(CallActivity.EXTRA_LOOPBACK, false)
            intent.putExtra(CallActivity.EXTRA_VIDEO_CALL, true)
            intent.putExtra(CallActivity.EXTRA_SCREENCAPTURE, false)
            intent.putExtra(CallActivity.EXTRA_CAMERA2, false)
            intent.putExtra(CallActivity.EXTRA_VIDEO_WIDTH, 640)
            intent.putExtra(CallActivity.EXTRA_VIDEO_HEIGHT, 480)
            intent.putExtra(CallActivity.EXTRA_VIDEO_FPS, 10)
            intent.putExtra(CallActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, false)
            intent.putExtra(CallActivity.EXTRA_VIDEO_BITRATE, 2000)
            intent.putExtra(CallActivity.EXTRA_VIDEOCODEC, "VP8")
            intent.putExtra(CallActivity.EXTRA_HWCODEC_ENABLED, true)
            intent.putExtra(CallActivity.EXTRA_CAPTURETOTEXTURE_ENABLED, false)
            intent.putExtra(CallActivity.EXTRA_AUDIOCODEC, "OPUS")
            intent.putExtra(CallActivity.EXTRA_TRACING, false)
            intent.putExtra(CallActivity.EXTRA_CMDLINE, false)
            intent.putExtra(CallActivity.EXTRA_DATA_CHANNEL_ENABLED, false)
            startActivity(intent)
        }
    }

    override fun onDirect(dataJson: JsonObject?) {
        Log.d("TAG", dataJson?.asString ?: "")
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
            Message.CONTENT_DATA -> return (message.data != null)
            Message.CONTENT_ACTIONS -> return (message.actions != null)
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
                .registerContentType(
                        Message.CONTENT_DATA,
                        DataMessageViewHolder::class.java,
                        R.layout.item_data_message,
                        DataMessageViewHolder::class.java,
                        R.layout.item_data_message,
                        this)
                .registerContentType(
                        Message.CONTENT_ACTIONS,
                        ActionsMessageViewHolder::class.java,
                        R.layout.item_actions_message,
                        ActionsMessageViewHolder::class.java,
                        R.layout.item_actions_message,
                        this)


        messagesAdapter = MessagesListAdapter(User.ME_ID, holders, super.imageLoader)
        messagesAdapter.enableSelectionMode(this)
        messagesAdapter.setLoadMoreListener(this)

        testData()

        messagesList.setAdapter(super.messagesAdapter)
        messagesList.drawingTime
    }


    private fun testData() {
//        addMessageBot("Hello from bot")
//        addMessageClient("Hello from me")
//        val place1 = Place("Сбербанк России", "Вокзальная ул. 2", "https://avatars.mds.yandex.net/get-altay/235931/2a0000015c53448a7d5e93089463748c763d/XXL")
//        val place2 = Place("Сбербанк России", "Можайское ш. 71В", "https://avatars.mds.yandex.net/get-altay/367512/2a0000015b3bada2607128874b1f2ba3c79b/XXL")
//        addMessagePlaces(place1, place2)
//        addData("text", "title", "subtitle")
//        addText("text")
//        addTitle("title")
//        addSubtitle("subtitle")
//        addMessageBot("Hello again")
//        addActions("test", {
//            toast("test")
//        })
//        addActions("ok", {
//            toast("ok")
//        }, "no", {
//            toast("no")
//        })
    }

    override fun addMessageClient(message: String) {
        messagesAdapter.addToStart(Message.userMessage(message), true)
    }

    override fun addMessageBot(message: String) {
        messagesAdapter.addToStart(Message.botMessage(message), true)
    }

    override fun addMessagePlaces(place1: Place, place2: Place) {
        messagesAdapter.addToStart(Message.gallery(place1, place2), true)
    }

    override fun addText(text: String) {
        messagesAdapter.addToStart(Message.data(text, null, null), true)
    }

    override fun addTitle(text: String) {
        messagesAdapter.addToStart(Message.data(null, text, null), true)
    }

    override fun addSubtitle(text: String) {
        messagesAdapter.addToStart(Message.data(null, null, text), true)
    }

    override fun addData(text: String?, title: String?, subtitle: String?) {
        messagesAdapter.addToStart(Message.data(text, title, subtitle), true)
    }

    override fun addActions(text: String, action: Action?, text2: String?, action2: Action?) {
        messagesAdapter.addToStart(Message.actions(text, action, text2, action2), true)
    }
}
