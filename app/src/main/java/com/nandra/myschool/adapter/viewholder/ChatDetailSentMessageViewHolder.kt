package com.nandra.myschool.adapter.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ale.infra.manager.IMMessage
import com.nandra.myschool.R
import com.nandra.myschool.utils.Utility.convertToString
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
                when (message.deliveryState) {
                    IMMessage.DeliveryState.SENT_SERVER_RECEIVED -> {
                        itemView.chat_detail_message_read_status_layout.visibility = View.VISIBLE
                        itemView.chat_detail_message_read_status_message.text = "Sent"
                        itemView.chat_detail_message_read_status_date.text = message.messageDate.convertToString()
                    }
                    IMMessage.DeliveryState.SENT_CLIENT_READ -> {
                        itemView.chat_detail_message_read_status_layout.visibility = View.VISIBLE
                        itemView.chat_detail_message_read_status_message.text = "Read"
                        if (message.messageDateRead != null) {
                            itemView.chat_detail_message_read_status_date.text = message.messageDateRead.convertToString()
                        } else {
                            itemView.chat_detail_message_read_status_date.text = message.messageDate.convertToString()
                        }
                    }
                    IMMessage.DeliveryState.SENT_CLIENT_RECEIVED -> {
                        itemView.chat_detail_message_read_status_layout.visibility = View.VISIBLE
                        itemView.chat_detail_message_read_status_message.text = "Delivered"
                        itemView.chat_detail_message_read_status_date.text = message.messageDate.convertToString()
                    }
                    else -> {
                        itemView.chat_detail_message_read_status_layout.visibility = View.GONE
                    }
                }
            }
            MessageType.FILE -> {
                itemView.chat_detail_sent_item_media_layout.visibility = View.VISIBLE
                itemView.chat_detail_sent_item_message.visibility = View.GONE
                itemView.chat_detail_sent_item_description.text = "${message.messageContent} was shared"
                itemView.chat_detail_message_content_layout.visibility = View.GONE
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