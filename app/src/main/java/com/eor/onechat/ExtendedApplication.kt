package com.eor.onechat

import android.app.Application
import com.eor.onechat.net.WebSocketClient
import timber.log.Timber

/**
 * Created by sse on 18.02.18.
 */
class ExtendedApplication: Application() {

    private var webSocketClient: WebSocketClient? = null
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Timber.d("onCreate")
        webSocketClient = WebSocketClient()
    }
}