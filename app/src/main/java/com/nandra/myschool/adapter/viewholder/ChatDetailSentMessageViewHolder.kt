package com.nandra.myschool.adapter.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ale.infra.manager.IMMessage
import com.bumptech.glide.Glide
import com.nandra.myschool.R
import kotlinx.android.synthetic.main.chat_detail_sent_item.view.*

class ChatDetailSentMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bindView(message: IMMessage) {
        if (!message.isFileDescriptorAvailable) {
            setupView(message, MessageType.NORMAL)
        } else {
            //TODO: ISSUE, CANNOT LOAD IMAGE
            if (message.fileDescriptor.isImageType) {
                setupView(message, MessageType.IMAGE)
            } else {
                setupView(message, MessageType.OTHER)
            }
        }
    }

    private fun setupView(message: IMMessage, type: MessageType) {
        when(type) {
            MessageType.NORMAL -> {
                itemView.chat_detail_sent_item_media_layout.visibility = View.GONE
                itemView.chat_detail_sent_item_message.visibility = View.VISIBLE
                itemView.chat_detail_sent_item_message.text = message.messageContent
            }
            MessageType.IMAGE -> {
                itemView.chat_detail_sent_item_media_layout.visibility = View.VISIBLE
                itemView.chat_detail_sent_item_message.visibility = View.GONE
                Glide.with(itemView.context)
                    .load(message.fileDescriptor.image)
                    .into(itemView.chat_detail_sent_item_photo)
                itemView.chat_detail_sent_item_description.text = "${message.messageContent} is shared"
            }
            else -> {
                itemView.chat_detail_sent_item_media_layout.visibility = View.VISIBLE
                itemView.chat_detail_sent_item_message.visibility = View.GONE
                Glide.with(itemView.context)
                    .load(R.drawable.ic_attachment)
                    .into(itemView.chat_detail_sent_item_photo)
                itemView.chat_detail_sent_item_description.text = "${message.messageContent} is shared"
            }
        }
    }

    private enum class MessageType{
        NORMAL,
        IMAGE,
        OTHER
    }

    companion object {
        fun create(parent: ViewGroup) : ChatDetailSentMessageViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_detail_sent_item, parent, false)
            return ChatDetailSentMessageViewHolder(view)
        }
    }
}