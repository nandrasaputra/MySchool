package com.nandra.myschool.adapter.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ale.infra.manager.IMMessage
import com.nandra.myschool.R
import kotlinx.android.synthetic.main.chat_detail_activity.view.*
import kotlinx.android.synthetic.main.chat_detail_received_item.view.*
import kotlinx.android.synthetic.main.chat_detail_sent_item.view.*

class ChatDetailReceivedMessageViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    fun bindView(message: IMMessage) {
        itemView.chat_detail_received_item_message.text = message.messageContent
    }

    companion object {
        fun create(parent: ViewGroup) : ChatDetailReceivedMessageViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_detail_received_item, parent, false)
            return ChatDetailReceivedMessageViewHolder(view)
        }
    }
}