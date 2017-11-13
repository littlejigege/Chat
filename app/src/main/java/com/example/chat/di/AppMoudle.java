package com.example.chat.di;

import com.example.chat.friendlist.FriendListActivity;
import com.example.chat.friendlist.FriendListMoudle;
import com.example.chat.ui.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by jimji on 2017/11/13.
 */
@Module
public abstract class AppMoudle {
    @ActivityScoped
    @ContributesAndroidInjector(modules = {FriendListMoudle.class})
    abstract FriendListActivity friendListActivity();

}
