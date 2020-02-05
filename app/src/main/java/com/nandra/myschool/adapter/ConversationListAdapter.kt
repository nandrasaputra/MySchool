package com.nandra.myschool.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ale.infra.proxy.conversation.IRainbowConversation
import com.bumptech.glide.Glide
import com.nandra.myschool.R
import kotlinx.android.synthetic.main.chat_conversation_fragment_item.view.*
import java.util.*

class ConversationListAdapter(
    val clickCallback: (IRainbowConversation) -> Unit
) : ListAdapter<IRainbowConversation, ConversationListAdapter.ConversationViewHolder>(chatConversationDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_conversation_fragment_item, parent, false)
        return ConversationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ConversationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(conversation: IRainbowConversation) {
            if(!conversation.isRoomType) {
                val contact = conversation.contact
                val fullName = "${contact.firstName} ${contact.lastName}"
                itemView.fragment_chat_conversation_item_name_tv.text = fullName

                Glide.with(itemView.context)
                    .load(conversation.contact.photo)
                    .into(itemView.fragment_chat_conversation_item_profile_picture)
            } else {
                val roomName = conversation.room.name
                itemView.fragment_chat_conversation_item_name_tv.text = roomName

                Glide.with(itemView.context)
                    .load(conversation.room.photo)
                    .into(itemView.fragment_chat_conversation_item_profile_picture)
            }
            itemView.fragment_chat_conversation_item_message_tv.text = conversation.lastMessage.messageContent

            //TODO: Implement These Two!!!
            itemView.fragment_chat_conversation_sent_date_tv.text = "!Implemented"
            itemView.fragment_chat_conversation_unread_count_tv.text = "!"

        }
    }
    companion object {
        val chatConversationDiffCallback = object : DiffUtil.ItemCallback<IRainbowConversation>() {
            override fun areItemsTheSame(oldItem: IRainbowConversation, newItem: IRainbowConversation): Boolean {
                return oldItem == newItem
            }

            //TODO: id OR jid?
            override fun areContentsTheSame(oldItem: IRainbowConversation, newItem: IRainbowConversation): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    private fun Date.convertToString() {

    }
}