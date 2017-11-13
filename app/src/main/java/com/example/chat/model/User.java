package com.example.chat.model;

import android.provider.MediaStore;

import java.io.Serializable;

/**
 * Created by jimji on 2017/10/23.
 */

public class User implements Serializable {
    public User(int id) {
        this.id = id;
    }

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int id;
    public String name;
    public boolean hasNewMsg;
    public boolean isOnline;

}
