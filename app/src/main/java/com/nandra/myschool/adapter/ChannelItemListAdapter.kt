package com.nandra.myschool.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ale.infra.manager.channel.ChannelItem
import com.ale.rainbowsdk.RainbowSdk
import com.bumptech.glide.Glide
import com.nandra.myschool.R
import com.nandra.myschool.utils.Utility
import com.nandra.myschool.utils.Utility.convertToString
import kotlinx.android.synthetic.main.home_fragment_item.view.*

class ChannelItemListAdapter : ListAdapter<ChannelItem, ChannelItemListAdapter.HomeViewHolder>(homeDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_fragment_item, parent, false)
        return HomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(channelItem: ChannelItem) {
            val contactName = Utility.nameBuilder(RainbowSdk.instance().contacts().getContactFromId(channelItem.contact.id))
            val channel = RainbowSdk.instance().channels().getChannel(channelItem.channelId)
            itemView.fragment_home_item_channel_name.text = channel.name
            itemView.fragment_home_item_publisher_name.text = contactName
            if (!channelItem.title.isNullOrEmpty()) {
                itemView.fragment_home_item_content_title.visibility = View.VISIBLE
                itemView.fragment_home_item_content_title.text = channelItem.title
            } else {
                itemView.fragment_home_item_content_title.visibility = View.GONE
            }
            if (!channelItem.message.isNullOrEmpty()) {
                itemView.fragment_home_item_content_body.visibility = View.VISIBLE
                itemView.fragment_home_item_content_body.text = Html.fromHtml(channelItem.message).toString()
            } else {
                itemView.fragment_home_item_content_body.visibility = View.GONE
            }
            itemView.fragment_home_item_publish_date.text = channelItem.creationDate.convertToString()

            if(channelItem.imageList.size > 0) {
                itemView.fragment_home_item_content_photo.visibility = View.VISIBLE
                //TODO: FIX THIS
                Glide.with(itemView.context)
                    .load(channelItem.imageList[0].image)
                    .into(itemView.fragment_home_item_content_photo)
            } else {
                itemView.fragment_home_item_content_photo.visibility = View.GONE
            }

            Glide.with(itemView.context)
                .load(channel.channelAvatar)
                .into(itemView.fragment_home_item_photo)
        }
    }


    companion object {
        private val homeDiffCallback = object : DiffUtil.ItemCallback<ChannelItem>() {
            override fun areItemsTheSame(oldItem: ChannelItem, newItem: ChannelItem): Boolean {
                return oldItem == newItem
            }

            //TODO: Optimize?
            override fun areContentsTheSame(oldItem: ChannelItem, newItem: ChannelItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}