package com.example.chat.ui

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.chat.model.Message
import com.example.chat.model.User
import com.mobile.utils.Preference
import com.mobile.utils.gone
import com.mobile.utils.visiable
import kotlinx.android.synthetic.main.friend_item.view.*
import kotlinx.android.synthetic.main.msg_item.view.*

/**
 * Created by jimji on 2017/10/24.
 */
class FriendListAdapter(ctx: Context, val layoutId: Int, datas: List<User>) : ArrayAdapter<User>(ctx, layoutId, datas) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder
        if (convertView == null) {
            holder = ViewHolder(LayoutInflater.from(context).inflate(layoutId, parent, false))
            holder.view.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }
        if (getItem(position).hasNewMsg && getItem(position).isOnline) {
            holder.view.friendName.setTextColor(Color.RED)
        } else if (getItem(position).isOnline) {
            holder.view.friendName.setTextColor(Color.BLUE)
        } else {
            holder.view.friendName.setTextColor(Color.GRAY)
        }

        holder.view.friendName.text = getItem(position).name
        return holder.view
    }

    inner class ViewHolder(var view: View)

}