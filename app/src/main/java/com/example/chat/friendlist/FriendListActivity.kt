package com.example.chat.friendlist

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ImageView
import com.example.chat.R
import com.example.chat.model.Data
import com.example.chat.model.Message
import com.example.chat.model.User
import com.example.chat.ui.FriendListAdapter
import com.example.chat.ui.MainActivity
import com.example.chat.websocket.WebSocketManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.zxing.Result
import com.mobile.utils.ActivityManager
import com.mobile.utils.showToast
import com.vondear.rxtools.activity.ActivityScanerCode
import com.vondear.rxtools.interfaces.OnRxScanerListener
import com.vondear.rxtools.view.RxQRCode
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_friend_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import javax.inject.Inject

class FriendListActivity : DaggerAppCompatActivity(), FriendListContract.View {


    lateinit var adapter: FriendListAdapter
    @Inject lateinit var presenter: FriendListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_list)
        adapter = FriendListAdapter(this, R.layout.friend_item, Data.friendList)
        friendList.adapter = adapter
        presenter.takeView(this)
        friendList.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            Data.friendList[position].hasNewMsg = false
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("User", Data.friendList[position])
            startActivity(intent)
        }
        //长按删除
        friendList.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, position, _ ->
            AlertDialog.Builder(this).setTitle("确认信息")
                    .setMessage("删除好友" + Data.friendList[position].name)
                    .setNegativeButton("取消", { dialog, _ -> dialog.dismiss() })
                    .setPositiveButton("确认", { dialog, _ ->
                        dialog.dismiss()
                        presenter.deleteUser(Data.friendList[position])
                    }).show()
            true
        }
    }

    override fun onResume() {
        EventBus.getDefault().register(this)
        adapter.notifyDataSetChanged()
        super.onResume()
    }

    override fun onPause() {
        EventBus.getDefault().unregister(this)
        super.onPause()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMsg(msg: String) {
        val command = StringTokenizer(msg, "#")
        when (command.nextToken()) {
            "friendList" -> {
                val newList: MutableList<User> = Gson().fromJson(command.nextToken(), object : TypeToken<List<User>>() {}.type)
                Data.friendList.clear()
                Data.friendList.addAll(newList)
                adapter.notifyDataSetChanged()
            }
            "msg" -> {
                val message: Message = Gson().fromJson(command.nextToken(), Message::class.java)
                Data.friendList.filter { it.id == message.fromUser }
                        .forEach { it.hasNewMsg = true }
                adapter.notifyDataSetChanged()
            }
            "login" -> {
                val id = command.nextToken().toInt()
                Data.friendList.filter { id == it.id }
                        .forEach { it.isOnline = true }
                adapter.notifyDataSetChanged()
            }
            "logout" -> {
                val id = command.nextToken().toInt()
                Data.friendList.filter { id == it.id }
                        .forEach { it.isOnline = false }
                adapter.notifyDataSetChanged()
            }
            "user" -> {
                val codeView = ImageView(this)
                RxQRCode.createQRCode(command.nextToken(), codeView)
                AlertDialog.Builder(this).setTitle("我的名片")
                        .setView(codeView)
                        .show()
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
                adapter.notifyDataSetChanged()
            }
            "add" -> {
                Data.friendList.add(Gson().fromJson(command.nextToken(), User::class.java))
                adapter.notifyDataSetChanged()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.friend_list, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_friend -> {
                ActivityScanerCode.setScanerListener(object : OnRxScanerListener {
                    override fun onSuccess(type: String?, result: Result) {
                        val user: User
                        try {
                            user = Gson().fromJson(result.text, User::class.java)
                        } catch (e: Exception) {
                            showToast("二维码有误")
                            return
                        }
                        Data.friendList.filter { user.id == it.id }
                                .forEach {
                                    showToast("已添加过该账号")
                                    return
                                }
                        WebSocketManager.getInstance().addFriend(user)
                        Data.friendList.add(user)
                        adapter.notifyDataSetChanged()
                    }

                    override fun onFail(type: String?, message: String?) {

                    }
                })
                startActivity(Intent(this, ActivityScanerCode::class.java))
            }
            R.id.show_code -> {
                WebSocketManager.getInstance().getUser()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        ActivityManager.doubleExit()
    }

    override fun onLoad() {

    }

    override fun onLoadDone() {

    }

    override fun showFriendList(userList: MutableList<User>) {
        Data.friendList.clear()
        Data.friendList.addAll(userList)
    }

    override fun onMsgCamed(message: Message) {
        Data.friendList.filter { it.id == message.fromUser }
                .forEach { it.hasNewMsg = true }
        adapter.notifyDataSetChanged()
    }

    override fun onUserLogout(user: User) {
        val id = user.id
        Data.friendList.filter { id == it.id }
                .forEach { it.isOnline = false }
        adapter.notifyDataSetChanged()
    }

    override fun onUserLogin(user: User) {
        val id = user.id
        Data.friendList.filter { id == it.id }
                .forEach { it.isOnline = true }
        adapter.notifyDataSetChanged()
    }

    override fun onGetUser(userJson: String) {
        val codeView = ImageView(this)
        RxQRCode.createQRCode(userJson, codeView)
        AlertDialog.Builder(this).setTitle("我的名片")
                .setView(codeView)
                .show()
    }

    override fun onAddUser(user: User) {
        Data.friendList.add(user)
        adapter.notifyDataSetChanged()
    }

    override fun onDeleteUser(user: User) {
        var pos: Int = -1
        val id = user.id
        Data.friendList
                .filter { it.id == id }
                .forEach { pos = Data.friendList.indexOf(it) }
        if (pos != -1) {
            Data.friendList.removeAt(pos)
        }
        adapter.notifyDataSetChanged()
    }
}
