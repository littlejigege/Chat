package com.example.chat.websocket;

/**
 * Created by jimji on 2017/11/13.
 */

public interface WsObserver {
    void onWsMessage(WsMsg wsMsg);
}

class WsObserverBound {
    WsObserver observer;
    MsgType[] types;

    public WsObserverBound(WsObserver observer, MsgType[] types) {
        this.observer = observer;
        this.types = types;
    }
}
