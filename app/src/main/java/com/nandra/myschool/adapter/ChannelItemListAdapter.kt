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
import kotlinx.android.synthetic.main.classroom_detail_feed_item.view.*

class ChannelItemListAdapter(
private val onFailureCallback: () -> Unit
) : ListAdapter<ChannelItem, ChannelItemListAdapter.ChannelItemViewHolder>(homeDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.classroom_detail_feed_item, parent, false)
        return ChannelItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChannelItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ChannelItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(channelItem: ChannelItem) {
            if (channelItem.contact != null) {
                val contactName = Utility.nameBuilder(RainbowSdk.instance().contacts().getContactFromId(channelItem.contact.id))
                val channel = RainbowSdk.instance().channels().getChannel(channelItem.channelId)
                itemView.classroom_detail_feed_item_publisher_name.text = contactName
                if (!channelItem.title.isNullOrEmpty()) {
                    itemView.classroom_detail_feed_item_content_title.visibility = View.VISIBLE
                    itemView.classroom_detail_feed_item_content_title.text = channelItem.title
                } else {
                    itemView.classroom_detail_feed_item_content_title.visibility = View.GONE
                }
                if (!channelItem.message.isNullOrEmpty()) {
                    itemView.classroom_detail_feed_item_content_body.visibility = View.VISIBLE
                    itemView.classroom_detail_feed_item_content_body.text = Html.fromHtml(channelItem.message).toString()
                } else {
                    itemView.classroom_detail_feed_item_content_body.visibility = View.GONE
                }
                itemView.classroom_detail_feed_item_publish_date.text = channelItem.creationDate.convertToString()

                if(channelItem.imageList.size > 0) {
                    itemView.classroom_detail_feed_item_content_photo.visibility = View.VISIBLE
                    //FIX THIS
                    Glide.with(itemView.context)
                        .load(channelItem.imageList[0].image)
                        .into(itemView.classroom_detail_feed_item_content_photo)
                } else {
                    itemView.classroom_detail_feed_item_content_photo.visibility = View.GONE
                }
                if (channelItem.contact.photo != null) {
                    Glide.with(itemView.context)
                        .load(channelItem.contact.photo)
                        .placeholder(R.drawable.ic_profile)
                        .into(itemView.classroom_detail_feed_item_photo)
                }
            } else {
                //RetryLoad
                onFailureCallback.invoke()
            }
        }
    }


    companion object {
        private val homeDiffCallback = object : DiffUtil.ItemCallback<ChannelItem>() {
            override fun areItemsTheSame(oldItem: ChannelItem, newItem: ChannelItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ChannelItem, newItem: ChannelItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}