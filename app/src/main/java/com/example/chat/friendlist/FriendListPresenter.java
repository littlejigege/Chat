package com.example.chat.friendlist;

import com.example.chat.model.Message;
import com.example.chat.model.User;
import com.example.chat.websocket.MsgType;
import com.example.chat.websocket.WebSocketManager;
import com.example.chat.websocket.WsMsg;
import com.example.chat.websocket.WsObserver;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by jimji on 2017/11/13.
 */

public class FriendListPresenter implements FriendListContract.Presenter, WsObserver {
    private FriendListContract.View mFriendListView;

    @Inject
    public FriendListPresenter() {
    }

    @Override
    public void takeView(FriendListContract.View view) {
        mFriendListView = view;
        WebSocketManager.getInstance().observe(this,
                MsgType.AddUser,
                MsgType.FriendList,
                MsgType.DeleteUser,
                MsgType.Message,
                MsgType.Login,
                MsgType.Logout,
                MsgType.GetUser);
    }

    @Override
    public void dropView() {
        mFriendListView = null;
        WebSocketManager.getInstance().removeObserver(this);
    }

    @Override
    public void loadFriendList() {

    }

    @Override
    public void getUserMe() {

    }

    @Override
    public void addUser(User user) {
        WebSocketManager.getInstance().addFriend(user);
        mFriendListView.onAddUser(user);
    }

    @Override
    public void deleteUser(User user) {

    }

    @Override
    public void onWsMessage(WsMsg wsMsg) {
        switch (wsMsg.msgType) {
            case FriendList:
                mFriendListView.onLoadDone();
                mFriendListView.showFriendList((List<User>) wsMsg.data);
                break;
            case DeleteUser:
                mFriendListView.onDeleteUser((User) wsMsg.data);
                break;
            case Message:
                mFriendListView.onMsgCamed((Message) wsMsg.data);
                break;
            case GetUser:
                mFriendListView.onGetUser((String) wsMsg.data);
                break;
            case AddUser:
                mFriendListView.onAddUser((User) wsMsg.data);
                break;
            case Logout:
                mFriendListView.onUserLogout((User) wsMsg.data);
                break;
            case Login:
                mFriendListView.onUserLogin((User) wsMsg.data);
        }
    }
}
