package com.nandra.myschool.adapter.viewholder

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ale.infra.http.GetFileResponse
import com.ale.infra.manager.IMMessage
import com.ale.infra.manager.fileserver.IFileProxy
import com.ale.infra.manager.fileserver.RainbowFileDescriptor
import com.ale.rainbowsdk.RainbowSdk
import com.bumptech.glide.Glide
import com.nandra.myschool.R
import com.nandra.myschool.utils.Utility
import kotlinx.android.synthetic.main.chat_detail_activity.view.*
import kotlinx.android.synthetic.main.chat_detail_received_item.view.*
import kotlinx.android.synthetic.main.chat_detail_sent_item.view.*

class ChatDetailReceivedMessageViewHolder(view: View)
    : RecyclerView.ViewHolder(view) {

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
                itemView.chat_detail_received_item_media_layout.visibility = View.GONE
                itemView.chat_detail_received_item_message.visibility = View.VISIBLE
                itemView.chat_detail_received_item_message.text = message.messageContent
            }
            MessageType.FILE -> {
                itemView.chat_detail_received_item_media_layout.visibility = View.VISIBLE
                itemView.chat_detail_received_item_message.visibility = View.GONE
                itemView.chat_detail_received_item_description.text = "${message.messageContent} was shared"
            }
        }
    }

    private enum class MessageType{
        NORMAL,
        FILE
    }

    companion object {
        fun create(parent: ViewGroup) : ChatDetailReceivedMessageViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_detail_received_item, parent, false)
            return ChatDetailReceivedMessageViewHolder(view)
        }
    }
}