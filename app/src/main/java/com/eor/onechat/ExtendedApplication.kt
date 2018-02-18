package com.eor.onechat

import android.app.Application
import android.content.Intent
import android.provider.Settings
import com.eor.onechat.calls.CallActivity
import com.eor.onechat.net.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import timber.log.Timber
import java.util.ArrayList

/**
 * Created by sse on 18.02.18.
 */
class ExtendedApplication: Application(), WebSocketClient.IDirect {

    var webSocketClient: WebSocketClient? = null

    var auth: Auth? = null

    var remoteIceCandidates = ArrayList<IceCandidate>()
    var sdp: SessionDescription? = null

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Timber.v("onCreate")

        val androidId = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        val authReq = Auth(androidId, "Robot Androidovich");

        webSocketClient = WebSocketClient(this)

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

    override fun onDirect(dataJson: JsonObject?) {
        val gson = Gson()
        val direct = gson.fromJson(dataJson, Direct::class.java)
        when (direct.type) {
            Direct.Type.SDP -> {
                val sdp = gson.fromJson(dataJson?.getAsJsonObject("payload"), SessionDescription::class.java)
                Timber.d("SDP %s %s", sdp.type.toString(), sdp.description)
                this.sdp = sdp
                if (sdp.type == SessionDescription.Type.OFFER) {
                    var intent = Intent(this, CallActivity::class.java)
                    startActivity(intent)
                }
            }
            Direct.Type.CANDY -> {
                val candidate = gson.fromJson(dataJson?.getAsJsonObject("payload"), IceCandidate::class.java)
                Timber.d("CANDI %s %s", candidate.sdpMid, candidate.sdp)
                remoteIceCandidates.add(candidate)
            }
            Direct.Type.BYE -> {
            }
        }
    }
}