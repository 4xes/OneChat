package com.eor.onechat.net;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;

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

public class WebSocketClient extends WebSocketListener {
    private static final int NORMAL_CLOSURE_STATUS = 1000;
    private static final String url = "ws://78.47.163.50:8080/ws";
    private final OkHttpClient okHttpClient;
    private JsonParser parser = new JsonParser();
    private Gson gson = new GsonBuilder()
            .serializeNulls()
            .addSerializationExclusionStrategy(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                    final Expose expose = fieldAttributes.getAnnotation(Expose.class);
                    return expose != null && !expose.serialize();
                }

                @Override
                public boolean shouldSkipClass(Class<?> aClass) {
                    return false;
                }
            })
            .addDeserializationExclusionStrategy(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                    final Expose expose = fieldAttributes.getAnnotation(Expose.class);
                    return expose != null && !expose.deserialize();
                }

                @Override
                public boolean shouldSkipClass(Class<?> aClass) {
                    return false;
                }
            })
            .create();

    private Auth authRes = null;
    private WebSocket webSocket = null;
    private ConcurrentHashMap<String, ServerResponse> awaitingForResponse = new ConcurrentHashMap();

    public void send(Proto.Method method, Object object, ServerResponse callback) {
        String uuid = UUID.randomUUID().toString();
        String packet = gson.toJson(new Proto(uuid, method, object));

        awaitingForResponse.put(uuid, callback);
        webSocket.send(packet);
        Timber.d("sent %s %s", uuid, packet);
    }

    private void received(String packet) {
        JsonObject transport = parser.parse(packet).getAsJsonObject();
        Proto proto = gson.fromJson(transport, Proto.class);
        ServerResponse callback = awaitingForResponse.get(proto.uuid);

        if (proto.data != null) {
            JsonObject dataJson = transport.getAsJsonObject("data");
            if (callback != null) {
                callback.onServerResponse(dataJson);
            } else {
                switch (proto.method) {
                    case RECEIVE: {
                        Direct direct = gson.fromJson(dataJson, Direct.class);
                        switch (direct.type) {
                            case SDP:{
                                break;
                            }
                            case CANDY:{
                                break;
                            }
                            case BYE:{
                                break;
                            }
                        }
                        Timber.d("got DIRECT %s", direct.type.toString());
                        break;
                    }
                }
            }
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
//        webSocket.close(NORMAL_CLOSURE_STATUS, "test timeout");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        received(text);
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
