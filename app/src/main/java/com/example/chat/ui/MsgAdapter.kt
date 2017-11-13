package com.example.chat.ui

import android.content.Context
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.chat.model.Message
import com.mobile.utils.Preference
import com.mobile.utils.gone
import com.mobile.utils.visiable
import kotlinx.android.synthetic.main.msg_item.view.*

/**
 * Created by jimji on 2017/10/23.
 */
class MsgAdapter(ctx: Context, val layoutId: Int, datas: List<Message>) : ArrayAdapter<Message>(ctx, layoutId, datas) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder
        if (convertView == null) {
            holder = ViewHolder(LayoutInflater.from(context).inflate(layoutId, parent, false))
            holder.view.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }
        if (getItem(position).fromUser == Preference.get("user", "id" to 0) as Int) {
            holder.view.msgLeft.gone()
            holder.view.msgRight.visiable()
            holder.view.msgRight.text = String(Base64.decode(getItem(position).content, Base64.DEFAULT))
        } else {
            holder.view.msgRight.gone()
            holder.view.msgLeft.visiable()
            holder.view.msgLeft.text = String(Base64.decode(getItem(position).content, Base64.DEFAULT))
        }
        return holder.view
    }

    inner class ViewHolder(var view: View)


}