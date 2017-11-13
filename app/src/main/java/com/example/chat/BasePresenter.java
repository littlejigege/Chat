package com.example.chat;

/**
 * Created by jimji on 2017/11/13.
 */

public interface BasePresenter<T> {
    void takeView(T view);

    void dropView();
}
