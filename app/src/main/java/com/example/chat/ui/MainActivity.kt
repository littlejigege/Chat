package com.example.chat.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import com.example.chat.R
import com.example.chat.model.Chat
import com.example.chat.model.Data
import com.example.chat.model.Message
import com.example.chat.model.User
import com.example.chat.websocket.WebSocketManager
import com.google.gson.Gson
import com.mobile.utils.Preference
import com.mobile.utils.showToast
import com.mobile.utils.toast
import com.mobile.utils.value


import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var user: User
    lateinit var chat: Chat
    val adapter by lazy { MsgAdapter(this, R.layout.msg_item, chat.messageList) }
    override fun onCreate(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (intent != null) {
            user = intent.getSerializableExtra("User") as User
            title = user.name
        }
        initView()

    }

    private fun initView() {
        sendButton.setOnClickListener {
            if (editText.value.isEmpty()) {
                showToast("不能发送空消息")
                return@setOnClickListener
            }
            val message = Message()
            message.content = Base64.encodeToString(editText.value.toByteArray(), Base64.DEFAULT)
            message.chatId = chat.id
            message.fromUser = Preference.get("user", kotlin.Pair<String, Any>("id", -1)) as Int
            message.toUser = user.id
            WebSocketManager.getInstance().sendText(message)
            addMsgToChat(message)
            editText.setText("")
        }
        WebSocketManager.getInstance().getChat(user)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMsg(msg: String) {
        val command = StringTokenizer(msg, "#")
        when (command.nextToken()) {
            "msg" -> {
                val message: Message = Gson().fromJson(command.nextToken(), Message::class.java)
                addMsgToChat(message)
            }
            "history" -> {
                chat = Gson().fromJson(command.nextToken(), Chat::class.java)
                listView.adapter = adapter
            }
            "login" -> {
                val id = command.nextToken().toInt()
                Data.friendList.filter { id == it.id }
                        .forEach { it.isOnline = true }
            }
            "logout" -> {
                val id = command.nextToken().toInt()
                Data.friendList.filter { id == it.id }
                        .forEach { it.isOnline = false }
            }
            "delete" -> {
                var pos: Int = -1
                val id = command.nextToken().toInt()
                Data.friendList
                        .filter { it.id == id }
                        .forEach { pos = Data.friendList.indexOf(it) }
                if (pos != -1) {
                    Data.friendList.removeAt(pos)
                }
            }
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    fun addMsgToChat(message: Message) {
        chat.messageList.add(message)
        adapter.notifyDataSetChanged()
        listView.smoothScrollToPosition(chat.messageList.size - 1)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_clear -> {
                WebSocketManager.getInstance().deleteHistory(user)
                chat.messageList.clear()
                adapter.notifyDataSetChanged()
            }
        }
        return super.onOptionsItemSelected(item)
    }


}
