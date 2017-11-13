package com.example.chat.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.chat.R
import com.example.chat.friendlist.FriendListActivity
import com.example.chat.websocket.WebSocketManager
import com.mobile.utils.*

import kotlinx.android.synthetic.main.activity_login.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class LoginActivity : AppCompatActivity() {
    var id by preference<Int>("user", "id" to -1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        EventBus.getDefault().register(this)

        //没登陆过则输入id登陆
        loginButton.setOnClickListener {
            if (editText2.value.isEmpty() || !editText2.value.isNumber) {
                showToast("id格式有误")
                return@setOnClickListener
            }
            id = editText2.value.toInt()
            WebSocketManager.getInstance().connect("ws://120.77.38.183:80/IM")

        }
        //登陆过直接登陆
        if (-1 != id) {
            WebSocketManager.getInstance().connect("ws://120.77.38.183:80/IM")
            return
        }
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe
    fun onMsg(msg: String) {
        if (msg == "onOpen") {
            startActivity(Intent(this, FriendListActivity::class.java))
        }

    }
}

