package com.eor.onechat.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.TooManyListenersException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import timber.log.Timber;

/**
 * Created by sse on 17.02.18.
 */

public class WebSocketClient extends WebSocketListener implements ServerResponse {
    private static final int NORMAL_CLOSURE_STATUS = 1000;
    private static final String url = "ws://78.47.163.50:8080/ws";
    private final OkHttpClient okHttpClient;
    private JsonParser parser = new JsonParser();
    private Gson gson = new GsonBuilder().serializeNulls().create();
    private static final Auth authReq = new Auth("sselgq66-1426-11e8-b642-0ed5f89f718b", "Serg Salnikov");
    private Auth authRes = null;
    private WebSocket webSocket = null;
    private ConcurrentHashMap<String, ServerResponse> awaitingForResponse = new ConcurrentHashMap();

    @Override
    public void onServerResponse() {
        Timber.v("onServerResponse");
    }

    public void send(Proto.Method method, Object object, ServerResponse callback) {
        String uuid = UUID.randomUUID().toString();
        String packet = gson.toJson(new Proto(uuid, method, object));

        awaitingForResponse.put(uuid, callback);
        webSocket.send(packet);
        Timber.d("sent %s %s", uuid, callback.toString());
    }

    private void receive(String packet) {
        JsonObject transport = parser.parse(packet).getAsJsonObject();
        Proto proto = gson.fromJson(transport, Proto.class);
        if (proto.data != null) {
            JsonObject data = transport.getAsJsonObject("data");
        }
        ServerResponse callback = awaitingForResponse.get(proto.uuid);
        if (callback!=null) {
            callback.onServerResponse();
        }
        Timber.d("received %s %s", proto.uuid, packet);
    }

    public WebSocketClient() {
        Timber.v("constructor");
        okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        webSocket = okHttpClient.newWebSocket(request, this);
        okHttpClient.dispatcher().executorService().shutdown();
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Timber.v("onOpen");
        send(Proto.Method.AUTH, authReq,this);
        webSocket.close(NORMAL_CLOSURE_STATUS, "test timeout");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        receive(text);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        Timber.v("onMessage bytes.hex %s", bytes.hex());

    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        Timber.v("onClosing code %d reason %s", code, reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Timber.v("onFailure msg %s", t.getMessage());
    }
}
