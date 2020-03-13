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
import com.ale.infra.contact.IRainbowContact
import com.ale.rainbowsdk.RainbowSdk
import com.bumptech.glide.Glide
import com.nandra.myschool.R
import com.nandra.myschool.ui.chat_detail.ChatDetailActivity
import com.nandra.myschool.utils.Utility
import kotlinx.android.synthetic.main.create_new_chat_item.view.*

class CreateNewChatListAdapter : ListAdapter<IRainbowContact, CreateNewChatListAdapter.CreateNewChatViewHolder>(createNewChatDiffCallback),
Filterable {

    private var cachedList = listOf<IRainbowContact>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateNewChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.create_new_chat_item, parent, false)
        return CreateNewChatViewHolder(view)
    }

    fun updateList(newList: List<IRainbowContact>) {
        cachedList = newList
    }

    fun submitAndUpdateList(list: List<IRainbowContact>) {
        cachedList = list
        submitList(list)
        notifyDataSetChanged()
    }

    fun restoreList() {
        submitList(cachedList)
    }

    override fun onBindViewHolder(holder: CreateNewChatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CreateNewChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(contact: IRainbowContact) {
            itemView.activity_create_new_chat_item_user_name.text = Utility.nameBuilder(contact)
            itemView.activity_create_new_chat_item_user_company.text = contact.companyName

            Glide.with(itemView.context)
                .load(contact.photo)
                .placeholder(R.drawable.ic_profile)
                .into(itemView.activity_create_new_chat_item_photo)

            itemView.setOnClickListener {
                val jid = RainbowSdk.instance().conversations().getConversationFromContact(contact.jid).jid
                val intent = Intent(itemView.context, ChatDetailActivity::class.java).apply {
                    putExtra(Utility.EXTRA_JID, jid)
                }
                itemView.context.startActivity(intent)
            }
        }
    }

    companion object {
        private val createNewChatDiffCallback = object : DiffUtil.ItemCallback<IRainbowContact>() {
            override fun areItemsTheSame(oldItem: IRainbowContact, newItem: IRainbowContact): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: IRainbowContact, newItem: IRainbowContact): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResult = FilterResults()

                if (!constraint.isNullOrEmpty()) {
                    val filteredList = cachedList.filter {
                        val name = Utility.nameBuilder(it)
                        name.toLowerCase().contains(constraint)
                    }
                    filterResult.values = filteredList
                } else {
                    filterResult.values = cachedList
                }

                return filterResult
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                results.values?.run {
                    val value = this as List<IRainbowContact>
                    submitList(value)
                }
            }
        }
    }
}