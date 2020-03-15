package com.nandra.myschool.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ale.infra.proxy.conversation.IRainbowConversation
import com.bumptech.glide.Glide
import com.nandra.myschool.R
import com.nandra.myschool.ui.chat_detail.ChatDetailActivity
import com.nandra.myschool.utils.Utility
import com.nandra.myschool.utils.Utility.EXTRA_JID
import com.nandra.myschool.utils.Utility.convertToString
import com.nandra.myschool.utils.Utility.nameBuilder
import kotlinx.android.synthetic.main.chat_conversation_fragment_item.view.*

class ConversationListAdapter : ListAdapter<IRainbowConversation, ConversationListAdapter.ConversationViewHolder>(chatConversationDiffCallback),
    Filterable {

    private var cachedList = listOf<IRainbowConversation>()
    var filterState: Utility.ChatFilterState = Utility.ChatFilterState.NoFilter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_conversation_fragment_item, parent, false)
        return ConversationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateList(newList: List<IRainbowConversation>) {
        cachedList = newList
    }

    fun submitAndUpdateList(list: List<IRainbowConversation>) {
        cachedList = list
        submitList(list)
        notifyDataSetChanged()
    }

    fun restoreList() {
        submitList(cachedList)
        notifyDataSetChanged()
    }

    inner class ConversationViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(conversation: IRainbowConversation) {
            if(!conversation.isRoomType) {
                val contact = conversation.contact
                itemView.channel_detail_item_channel_name.text = nameBuilder(contact)

                Glide.with(itemView.context)
                    .load(conversation.contact.photo)
                    .placeholder(R.drawable.ic_profile)
                    .into(itemView.channel_detail_item_photo)
            } else {
                val roomName = conversation.room.name
                itemView.channel_detail_item_channel_name.text = roomName

                Glide.with(itemView.context)
                    .load(conversation.room.photo)
                    .placeholder(R.drawable.ic_profile)
                    .into(itemView.channel_detail_item_photo)
            }
            itemView.channel_detail_item_publisher_name.text = conversation.lastMessage.messageContent

            itemView.setOnClickListener {
                val jid = conversation.jid
                val intent = Intent(view.context, ChatDetailActivity::class.java).apply {
                    putExtra(EXTRA_JID, jid)
                }
                view.context.startActivity(intent)
            }

            //TODO: Optimize These Two
            val unreadMessageCount = conversation.unreadMsgNb
            if (unreadMessageCount != 0) {
                itemView.fragment_chat_conversation_unread_count_tv.visibility = View.VISIBLE
                itemView.fragment_chat_conversation_unread_count_tv.text = conversation.unreadMsgNb.toString()
            } else {
                itemView.fragment_chat_conversation_unread_count_tv.visibility = View.GONE
            }

            if (conversation.lastMessage.messageDate != null) {
                itemView.channel_detail_item_publish_date.text = conversation.lastMessage.messageDate.convertToString()
            } else {
                itemView.channel_detail_item_publish_date.visibility = View.GONE
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filterResult = FilterResults()

                if (constraint.isNotEmpty() and (filterState != Utility.ChatFilterState.NoFilter)) {
                    val filteredList = cachedList.filter {
                        val name = if (!it.isRoomType) {
                            val contact = it.contact
                            nameBuilder(contact)
                        } else {
                            it.room.name
                        }

                        (it.lastMessage.messageContent.toLowerCase().contains(constraint) or name.toLowerCase().contains(constraint))
                    }
                    filterResult.values = filteredList
                } else {
                    filterResult.values = cachedList
                }

                return filterResult
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                results.values?.run {
                    if (filterState != Utility.ChatFilterState.NoFilter) {
                        val value = this as List<IRainbowConversation>
                        submitList(value)
                    }
                }
            }
        }
    }

    companion object {
        private val chatConversationDiffCallback = object : DiffUtil.ItemCallback<IRainbowConversation>() {
            override fun areItemsTheSame(oldItem: IRainbowConversation, newItem: IRainbowConversation): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: IRainbowConversation, newItem: IRainbowConversation): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}