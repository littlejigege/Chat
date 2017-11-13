package com.example.chat.friendlist;

import com.example.chat.di.ActivityScoped;

import dagger.Binds;
import dagger.Module;

/**
 * Created by jimji on 2017/11/13.
 */
@Module
public abstract class FriendListMoudle {
    @ActivityScoped
    @Binds
    abstract FriendListContract.Presenter providePresenter(FriendListPresenter presenter);
}
