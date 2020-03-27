package com.nandra.myschool.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ale.infra.manager.channel.ChannelItem
import com.ale.rainbowsdk.RainbowSdk
import com.bumptech.glide.Glide
import com.nandra.myschool.R
import com.nandra.myschool.utils.Utility.ClassroomDetailPopupMenuCallback
import com.nandra.myschool.utils.Utility.convertToString
import com.nandra.myschool.utils.Utility.nameBuilder
import kotlinx.android.synthetic.main.classroom_detail_feed_item.view.*

class ClassroomFeedListAdapter(
private val onFailureCallback: () -> Unit,
private val hamburgerClickCallback : (ClassroomDetailPopupMenuCallback) -> Unit,
private val isTeacherAccount: Boolean
) : ListAdapter<ChannelItem, ClassroomFeedListAdapter.ChannelItemViewHolder>(homeDiffCallback) {

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
                val contactName = nameBuilder(RainbowSdk.instance().contacts().getContactFromId(channelItem.contact.id))
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
                if (isTeacherAccount) {
                    itemView.classroom_detail_feed_item_item_hamburger.visibility = View.VISIBLE
                    itemView.classroom_detail_feed_item_item_hamburger.setOnClickListener {
                        PopupMenu(itemView.context, it).apply {
                            this.menuInflater.inflate(R.menu.classroom_feed_popup_menu, this.menu)

                            this.setOnMenuItemClickListener {menuItem ->
                                return@setOnMenuItemClickListener when(menuItem.itemId) {
                                    R.id.classroom_feed_delete_menu_item -> {
                                        hamburgerClickCallback.invoke(ClassroomDetailPopupMenuCallback.OnDeleteClicked(channelItem))
                                        true
                                    }
                                    else -> {
                                        false
                                    }
                                }
                            }
                        }.show()
                    }
                } else {
                    itemView.classroom_detail_feed_item_item_hamburger.visibility = View.GONE
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