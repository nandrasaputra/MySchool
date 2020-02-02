package com.nandra.myschool.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ale.infra.contact.IRainbowContact
import com.nandra.myschool.R

class ContactAdapter(private val context: Context) : ListAdapter<IRainbowContact, ContactAdapter.ContactAdapterViewHolder>(chatDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactAdapterViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.chat_conversation_fragment_item, parent, false)
        return ContactAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactAdapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ContactAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(contact: IRainbowContact) {

        }
    }

    companion object {
        val chatDiffCallback = object :DiffUtil.ItemCallback<IRainbowContact>() {
            override fun areItemsTheSame(oldItem: IRainbowContact, newItem: IRainbowContact): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: IRainbowContact, newItem: IRainbowContact): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}