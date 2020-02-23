package com.nandra.myschool.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ale.infra.manager.IMMessage
import com.nandra.myschool.adapter.viewholder.ChatDetailReceivedMessageViewHolder
import com.nandra.myschool.adapter.viewholder.ChatDetailSentMessageViewHolder

class ChatDetailListAdapter : ListAdapter<IMMessage, RecyclerView.ViewHolder>(channelDetailDiffUtilCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            VIEW_TYPE_MESSAGE_SENT -> { ChatDetailSentMessageViewHolder.create(parent) }
            VIEW_TYPE_MESSAGE_RECEIVE -> { ChatDetailReceivedMessageViewHolder.create(parent)}
            else -> throw IllegalArgumentException("Unknown View Type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)) {
            VIEW_TYPE_MESSAGE_SENT -> { (holder as ChatDetailSentMessageViewHolder).bindView(getItem(position)) }
            VIEW_TYPE_MESSAGE_RECEIVE -> { (holder as ChatDetailReceivedMessageViewHolder).bindView(getItem(position)) }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return if (message.isMsgSent) {
            VIEW_TYPE_MESSAGE_SENT
        } else {
            VIEW_TYPE_MESSAGE_RECEIVE
        }
    }

    companion object {
        private const val VIEW_TYPE_MESSAGE_SENT = 1
        private const val VIEW_TYPE_MESSAGE_RECEIVE = 2

        private val channelDetailDiffUtilCallback = object : DiffUtil.ItemCallback<IMMessage>() {
            override fun areItemsTheSame(oldItem: IMMessage, newItem: IMMessage): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: IMMessage, newItem: IMMessage): Boolean {
                return oldItem.messageId == newItem.messageId
            }

        }
    }

}