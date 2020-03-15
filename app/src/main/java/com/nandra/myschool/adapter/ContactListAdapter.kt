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
import com.nandra.myschool.utils.Utility.EXTRA_JID
import kotlinx.android.synthetic.main.chat_contact_fragment_item.view.*

class ContactListAdapter : ListAdapter<IRainbowContact, ContactListAdapter.ContactViewHolder>(chatContactDiffCallback),
Filterable {

    private var cachedList = listOf<IRainbowContact>()
    var filterState: Utility.ChatFilterState = Utility.ChatFilterState.NoFilter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_contact_fragment_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(getItem(position))
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
        notifyDataSetChanged()
    }

    inner class ContactViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(contact: IRainbowContact) {
            itemView.fragment_chat_contact_item_name_tv.text = Utility.nameBuilder(contact)
            itemView.fragment_chat_contact_item_company_tv.text = contact.companyName

            Glide.with(itemView.context)
                .load(contact.photo)
                .placeholder(R.drawable.ic_profile)
                .into(itemView.fragment_chat_contact_item_profile_picture)

            view.setOnClickListener {
                val jid = RainbowSdk.instance().conversations().getConversationFromContact(contact.jid).jid
                val intent = Intent(view.context, ChatDetailActivity::class.java).apply {
                    putExtra(EXTRA_JID, jid)
                }
                view.context.startActivity(intent)
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filterResult = FilterResults()

                if (constraint.isNotEmpty() and (filterState != Utility.ChatFilterState.NoFilter)) {
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
                    if (filterState != Utility.ChatFilterState.NoFilter) {
                        val value = this as List<IRainbowContact>
                        submitList(value)
                    }
                }
            }
        }
    }

    companion object {
        private val chatContactDiffCallback = object :DiffUtil.ItemCallback<IRainbowContact>() {
            override fun areItemsTheSame(oldItem: IRainbowContact, newItem: IRainbowContact): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: IRainbowContact, newItem: IRainbowContact): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}