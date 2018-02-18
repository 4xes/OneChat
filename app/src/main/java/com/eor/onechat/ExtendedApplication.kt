package com.eor.onechat

import android.app.Application
import com.eor.onechat.net.Auth
import com.eor.onechat.net.Proto
import com.eor.onechat.net.ServerResponse
import com.eor.onechat.net.WebSocketClient
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import timber.log.Timber

/**
 * Created by sse on 18.02.18.
 */
class ExtendedApplication: Application() {

    var webSocketClient: WebSocketClient? = null
    private val authReq: Auth = Auth("sselgq66-1426-11e8-b642-0ed5f89f718b", "Serg Salnikov");
    var auth: Auth? = null

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Timber.v("onCreate")
        webSocketClient = WebSocketClient()

        val authResponse = object: ServerResponse {
            override fun onServerResponse(jsonObject: JsonObject?) {
                val gson = Gson()
                val authResponse = gson.fromJson(jsonObject, Auth::class.java)
                if (authResponse.result == 1) {
                    auth = authResponse
                    Timber.d("auth succeed, userId %s", authResponse.userId)
                } else {
                    Timber.d("auth failed")
                }

            }
        }

        webSocketClient?.send(Proto.Method.AUTH, authReq, authResponse)
    }
}