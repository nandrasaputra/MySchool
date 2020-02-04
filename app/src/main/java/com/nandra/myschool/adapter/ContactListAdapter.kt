package com.nandra.myschool.adapter

import android.content.Context
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

class ContactListAdapter(private val context: Context) : ListAdapter<IRainbowContact, ContactListAdapter.ContactAdapterViewHolder>(chatContactDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactAdapterViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.chat_contact_fragment_item, parent, false)
        return ContactAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactAdapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ContactAdapterViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(contact: IRainbowContact) {
            val userFullName = "${contact.firstName} ${contact.lastName}"
            val userCompany = contact.companyName

            view.fragment_chat_contact_item_name_tv.text = userFullName
            view.fragment_chat_contact_item_company_tv.text = userCompany

            Glide.with(view.context)
                .load(contact.photo)
                .into(view.fragment_chat_contact_item_profile_picture)
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