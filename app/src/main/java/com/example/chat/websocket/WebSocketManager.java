package com.example.chat.websocket;

import com.example.chat.model.Chat;
import com.example.chat.model.Friend;
import com.example.chat.model.Message;
import com.example.chat.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobile.utils.Preference;
import com.mobile.utils.ToastUtilsKt;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.StringTokenizer;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * Created by jimji on 2017/10/24.
 */

public class WebSocketManager {
    private static WebSocketManager wsManager;
    private WebSocket webSocket;
    private Map<WsObserver, MsgType[]> observers;

    public void observe(WsObserver observer, MsgType... types) {
        if (observers == null) {
            observers = new HashMap<>();
        }
        if (!observers.containsKey(observer)) {
            observers.put(observer, types);
        }
    }

    public void removeObserver(WsObserver observer) {
        if (observers != null) {
            observers.remove(observer);
        }
    }

    public static WebSocketManager getInstance() {
        if (wsManager == null) {
            wsManager = new WebSocketManager();
        }
        return wsManager;
    }

    class WebsocketListener extends WebSocketListener {
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            EventBus.getDefault().post(text);
            dispatchMsg(text);
        }

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            if (wsManager.webSocket == null) {
                wsManager.webSocket = webSocket;
            }
            wsManager.login();
            EventBus.getDefault().post("onOpen");
        }
    }

    private void login() {
        webSocket.send("login#" + Preference.INSTANCE.get("user", new kotlin.Pair<String, Object>("id", -1)));
    }

    public void connect(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newWebSocket(request, new WebsocketListener());

    }

    public void send(String string) {
        webSocket.send(string);
    }

    public void sendText(Message message) {
        send("chat#msg#" + new Gson().toJson(message));
    }

    public void getFriendList() {
        send("friend#list#" + Preference.INSTANCE.get("user", new kotlin.Pair<String, Object>("id", -1)));
    }

    public void getChat(User user) {
        send("chat#history#" + Preference.INSTANCE.get("user", new kotlin.Pair<String, Object>("id", -1)) + "#" + user.id);
    }

    public void deleteHistory(User user) {
        send("chat#delete#" + Preference.INSTANCE.get("user", new kotlin.Pair<String, Object>("id", -1)) + "#" + user.id);
    }

    public void deleteFriend(User user) {
        send("friend#delete#" + Preference.INSTANCE.get("user", new kotlin.Pair<String, Object>("id", -1)) + "#" + user.id);
    }

    public void addFriend(User user) {
        send("friend#add#" + new Gson().toJson(new Friend((Integer) Preference.INSTANCE.get("user", new kotlin.Pair<String, Object>("id", -1)), user.id)));
    }

    public void getUser() {

        send("getUser#" + Preference.INSTANCE.get("user", new kotlin.Pair<String, Object>("id", -1)));
    }


    private MsgType analyze(StringTokenizer command) {
        switch (command.nextToken()) {
            case "msg":
                return MsgType.Message;
            case "history":
                return MsgType.ChatHistory;
            case "login":
                return MsgType.Login;
            case "logout":
                return MsgType.Logout;
            case "delete":
                return MsgType.DeleteUser;
            case "user":
                return MsgType.GetUser;
            case "friendList":
                return MsgType.FriendList;
            default:
                throw new RuntimeException("MsgType not found");
        }

    }

    private void dispatchMsg(String text) {
        StringTokenizer command = new StringTokenizer(text, "#");
        WsMsg wsMsg = null;
        switch (analyze(command)) {
            case Login:
                wsMsg = new WsMsg<>(MsgType.Login, Integer.parseInt(command.nextToken()));
                break;
            case Logout:
                wsMsg = new WsMsg<>(MsgType.Logout, Integer.parseInt(command.nextToken()));
                break;
            case AddUser:
                wsMsg = new WsMsg<>(MsgType.AddUser, new Gson().fromJson(command.nextToken(), User.class));
                break;
            case GetUser:
                wsMsg = new WsMsg<>(MsgType.GetUser, command.nextToken());
                break;
            case Message:
                wsMsg = new WsMsg<>(MsgType.Message, new Gson().fromJson(command.nextToken(), Message.class));
                break;
            case DeleteUser:
                wsMsg = new WsMsg<>(MsgType.DeleteUser, Integer.parseInt(command.nextToken()));
                break;
            case FriendList:
                wsMsg = new WsMsg<>(MsgType.FriendList, new Gson().fromJson(command.nextToken(), new TypeToken<List<User>>() {
                }.getType()));
                break;
            case ChatHistory:
                wsMsg = new WsMsg<>(MsgType.ChatHistory, new Gson().fromJson(command.nextToken(), Chat.class));
                break;
        }
        final WsMsg finalWsMsg = wsMsg;
        ToastUtilsKt.inUiThread(text, new Function0<Unit>() {
            @Override
            public Unit invoke() {
                dispatchMsg(finalWsMsg);
                return null;
            }
        });
    }

    private void dispatchMsg(WsMsg wsMsg) {
        for (Map.Entry<WsObserver, MsgType[]> entry : observers.entrySet()) {
            for (MsgType msgType : entry.getValue()) {
                if (msgType == wsMsg.msgType) {
                    entry.getKey().onWsMessage(wsMsg);
                    break;
                }
            }
        }
    }
}
