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

class AddNewContactListAdapter(
    private val addContactCallback: (IRainbowContact, Int) -> Unit
) : ListAdapter<IRainbowContact, AddNewContactListAdapter.AddNewContactViewHolder>(addNewContactDiffUtil) {

    private var addContactLoadingStateMap = mutableMapOf<String, Boolean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddNewContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.add_new_contact_item, parent, false)
        return AddNewContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddNewContactViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: List<IRainbowContact>?) {
        addContactLoadingStateMap = mutableMapOf()
        super.submitList(list)
        notifyDataSetChanged()
    }

    fun wipeData() {
        submitList(listOf())
        addContactLoadingStateMap = mutableMapOf()
        notifyDataSetChanged()
    }

    fun changeAddContactLoadingState(adapterPosition: Int, isLoading: Boolean) {
        try {
            val contact = getItem(adapterPosition)
            val contactId = contact.id
            addContactLoadingStateMap[contactId] = isLoading
            notifyDataSetChanged()
        } catch (exception: IndexOutOfBoundsException) {

        }
    }

    inner class AddNewContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(contact: IRainbowContact) {

            val state = addContactLoadingStateMap[contact.id]

            if (state != null && state == true) {
                isOnAddContactLoadingState(true)
            } else {
                isOnAddContactLoadingState(false)
            }

            itemView.activity_add_new_contact_item_name_tv.text = Utility.nameBuilder(contact)
            itemView.activity_add_new_contact_item_company_tv.text = contact.companyName
            val contactAvatarUrl = RainbowSdk.instance().contacts().getAvatarUrl(contact.id)
            Glide.with(itemView.context)
                .load(contactAvatarUrl)
                .placeholder(R.drawable.ic_profile)
                .into(itemView.activity_add_new_contact_item_profile_picture)
            itemView.activity_add_new_contact_item_add_contact_button.setOnClickListener {
                addContactCallback(contact, adapterPosition)
            }
        }

        private fun isOnAddContactLoadingState(boolean: Boolean) {
            if (boolean) {
                itemView.activity_add_new_contact_item_add_contact_progress_bar.visibility = View.VISIBLE
                itemView.activity_add_new_contact_item_add_contact_button.visibility = View.GONE
            } else {
                itemView.activity_add_new_contact_item_add_contact_progress_bar.visibility = View.GONE
                itemView.activity_add_new_contact_item_add_contact_button.visibility = View.VISIBLE
            }
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