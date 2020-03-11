package com.nandra.myschool.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ale.infra.contact.IRainbowContact
import com.ale.rainbowsdk.RainbowSdk
import com.bumptech.glide.Glide
import com.nandra.myschool.R
import com.nandra.myschool.utils.Utility
import kotlinx.android.synthetic.main.add_new_contact_item.view.*
import kotlinx.android.synthetic.main.create_new_chat_item.view.*

class AddNewContactListAdapter : ListAdapter<IRainbowContact, AddNewContactListAdapter.AddNewContactViewHolder>(addNewContactDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddNewContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.create_new_chat_item, parent, false)
        return AddNewContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddNewContactViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun wipeData() {
        submitList(listOf())
        notifyDataSetChanged()
    }

    inner class AddNewContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(contact: IRainbowContact) {
            itemView.activity_create_new_chat_item_user_name.text = Utility.nameBuilder(contact)
            itemView.activity_create_new_chat_item_user_company.text = contact.companyName
            val contactAvatarUrl = RainbowSdk.instance().contacts().getAvatarUrl(contact.id)
            Glide.with(itemView.context)
                .load(contactAvatarUrl)
                .placeholder(R.drawable.ic_profile)
                .into(itemView.activity_create_new_chat_item_photo)
        }
    }

    companion object {
        val addNewContactDiffUtil = object : DiffUtil.ItemCallback<IRainbowContact>() {
            override fun areItemsTheSame(oldItem: IRainbowContact, newItem: IRainbowContact): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: IRainbowContact, newItem: IRainbowContact): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

}