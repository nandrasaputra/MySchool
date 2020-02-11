package com.nandra.myschool.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ale.infra.manager.channel.Channel
import com.bumptech.glide.Glide
import com.nandra.myschool.R
import kotlinx.android.synthetic.main.channel_fragment_item.view.*

class ChannelListAdapter : ListAdapter<Channel, ChannelListAdapter.ChannelViewHolder>(channelDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.channel_fragment_item, parent, false)
        return ChannelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ChannelViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(channel: Channel) {
            itemView.fragment_channel_item_name.text = channel.name
            itemView.fragment_channel_item_description.text = channel.description
            Glide.with(itemView.context)
                .load(channel.channelAvatar)
                .into(itemView.fragment_channel_item_photo)
        }
    }

    companion object {
        val channelDiffCallback = object : DiffUtil.ItemCallback<Channel>() {
            override fun areItemsTheSame(oldItem: Channel, newItem: Channel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Channel, newItem: Channel): Boolean {
                return (oldItem.name == newItem.name) and (oldItem.description == oldItem.description)
            }
        }
    }

}