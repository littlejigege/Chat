package com.example.chat.friendlist;

import com.example.chat.BasePresenter;
import com.example.chat.BaseView;
import com.example.chat.model.Message;
import com.example.chat.model.User;

import java.util.List;

/**
 * Created by jimji on 2017/11/13.
 */

public interface FriendListContract {
    interface View extends BaseView<Presenter> {
        void onLoad();

        void onLoadDone();

        void showFriendList(List<User> userList);

        void onMsgCamed(Message message);

        void onUserLogout(User user);

        void onUserLogin(User user);

        void onGetUser(String userJson);

        void onAddUser(User user);

        void onDeleteUser(User user);
    }

    interface Presenter extends BasePresenter<View> {
        void loadFriendList();

        void getUserMe();

        void addUser(User user);

        void deleteUser(User user);
    }
}
