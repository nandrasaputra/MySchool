package com.nandra.myschool.adapter.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ale.infra.manager.IMMessage
import com.ale.infra.manager.fileserver.RainbowFileDescriptor
import com.bumptech.glide.Glide
import com.nandra.myschool.R
import kotlinx.android.synthetic.main.chat_detail_sent_item.view.*

class ChatDetailSentMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bindView(message: IMMessage) {
        if (!message.isFileDescriptorAvailable) {
            setupView(message, MessageType.NORMAL)
        } else {
            setupView(message, MessageType.FILE)
        }
    }

    private fun setupView(message: IMMessage, type: MessageType) {
        when(type) {
            MessageType.NORMAL -> {
                itemView.chat_detail_sent_item_media_layout.visibility = View.GONE
                itemView.chat_detail_sent_item_message.visibility = View.VISIBLE
                itemView.chat_detail_sent_item_message.text = message.messageContent
            }
            MessageType.FILE -> {
                itemView.chat_detail_sent_item_media_layout.visibility = View.VISIBLE
                itemView.chat_detail_sent_item_message.visibility = View.GONE
                itemView.chat_detail_sent_item_description.text = "${message.messageContent} was shared"
            }
        }
    }

    private enum class MessageType{
        NORMAL,
        FILE
    }

    companion object {
        fun create(parent: ViewGroup) : ChatDetailSentMessageViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_detail_sent_item, parent, false)
            return ChatDetailSentMessageViewHolder(view)
        }
    }
}