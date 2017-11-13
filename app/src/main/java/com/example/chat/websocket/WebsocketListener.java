package com.example.chat.websocket;

import android.util.Pair;

import com.mobile.utils.Preference;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * Created by jimji on 2017/10/24.
 */

public class WebsocketListener extends WebSocketListener {
    @Override
    public void onMessage(WebSocket webSocket, String text) {

    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        webSocket.send("login#" + Preference.INSTANCE.get("user", new kotlin.Pair<String, Object>("id", -1)));
    }
}
