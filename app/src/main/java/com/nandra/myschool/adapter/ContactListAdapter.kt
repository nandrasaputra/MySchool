package com.nandra.myschool.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ale.infra.contact.IRainbowContact
import com.bumptech.glide.Glide
import com.nandra.myschool.R
import kotlinx.android.synthetic.main.chat_contact_fragment_item.view.*

class ContactListAdapter : ListAdapter<IRainbowContact, ContactListAdapter.ContactViewHolder>(chatContactDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_contact_fragment_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(contact: IRainbowContact) {
            val userFullName = "${contact.firstName} ${contact.lastName}"
            val userCompany = contact.companyName

            itemView.fragment_chat_contact_item_name_tv.text = userFullName
            itemView.fragment_chat_contact_item_company_tv.text = userCompany

            Glide.with(itemView.context)
                .load(contact.photo)
                .into(itemView.fragment_chat_contact_item_profile_picture)
        }
    }

    companion object {
        val chatContactDiffCallback = object :DiffUtil.ItemCallback<IRainbowContact>() {
            override fun areItemsTheSame(oldItem: IRainbowContact, newItem: IRainbowContact): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: IRainbowContact, newItem: IRainbowContact): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}