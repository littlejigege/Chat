package com.example.chat.websocket;

/**
 * Created by jimji on 2017/11/13.
 */

public class WsMsg<T> {
    public final MsgType msgType;
    public final T data;

    public WsMsg(MsgType msgType, T data) {
        this.msgType = msgType;
        this.data = data;
    }
}
