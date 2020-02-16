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

class ChatDetailReceivedMessageViewHolder(
    view: View,
    private val listReceivedItem: List<RainbowFileDescriptor>
)
    : RecyclerView.ViewHolder(view) {

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
                itemView.chat_detail_received_item_media_layout.visibility = View.GONE
                itemView.chat_detail_received_item_message.visibility = View.VISIBLE
                itemView.chat_detail_received_item_message.text = message.messageContent
            }
            MessageType.IMAGE -> {
                itemView.chat_detail_received_item_media_layout.visibility = View.VISIBLE
                itemView.chat_detail_received_item_message.visibility = View.GONE
                val image = listReceivedItem.find {
                    it.id == message.fileDescriptor.id
                }
                Glide.with(itemView.context)
                    .load(image?.thumbnailFile)
                    .into(itemView.chat_detail_received_item_photo)
                itemView.chat_detail_received_item_description.text = "${message.messageContent} is shared"
            }
            else -> {
                itemView.chat_detail_received_item_media_layout.visibility = View.VISIBLE
                itemView.chat_detail_received_item_message.visibility = View.GONE
                Glide.with(itemView.context)
                    .load(R.drawable.ic_attachment)
                    .into(itemView.chat_detail_received_item_photo)
                itemView.chat_detail_received_item_description.text = "${message.messageContent} is shared"
            }
        }
    }

    private enum class MessageType{
        NORMAL,
        IMAGE,
        OTHER
    }

    companion object {
        fun create(parent: ViewGroup, listReceivedItem: List<RainbowFileDescriptor>) : ChatDetailReceivedMessageViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_detail_received_item, parent, false)
            return ChatDetailReceivedMessageViewHolder(view, listReceivedItem)
        }
    }
}