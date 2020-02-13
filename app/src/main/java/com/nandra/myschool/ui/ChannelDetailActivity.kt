package com.nandra.myschool.ui

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ale.infra.http.adapter.concurrent.RainbowServiceException
import com.ale.infra.manager.channel.Channel
import com.ale.infra.manager.channel.ChannelItem
import com.ale.infra.proxy.channel.IChannelProxy
import com.ale.rainbowsdk.RainbowSdk
import com.bumptech.glide.Glide
import com.nandra.myschool.R
import com.nandra.myschool.adapter.ChannelItemListAdapter
import com.nandra.myschool.utils.Utility.EXTRA_JID
import com.nandra.myschool.utils.Utility.LOG_DEBUG_TAG
import kotlinx.android.synthetic.main.channel_detail_activity.*

class ChannelDetailActivity : AppCompatActivity(), IChannelProxy.IChannelFetchItemsListener {

    lateinit var channel: Channel
    lateinit var channelItemListAdapter: ChannelItemListAdapter
    private var channelItemList = listOf<ChannelItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.channel_detail_activity)

        setupToolbar()

        val channelId = intent.getStringExtra(EXTRA_JID)
        channel = RainbowSdk.instance().channels().getChannel(channelId)

        RainbowSdk.instance().channels().fetchItems(channel, 20, this)

        channel.channelItems.registerChangeListener(this::updateChannelItemList)

        setupView(channel)
    }

    override fun onDestroy() {
        super.onDestroy()
        channel.channelItems.unregisterChangeListener(this::updateChannelItemList)
    }

    override fun onFetchItemsFailed(exception: RainbowServiceException?) {
        Log.d(LOG_DEBUG_TAG, "Fetch Item Failed : ${exception?.message}")
    }

    override fun onFetchItemsSuccess() {
        updateChannelItemList()
    }

    private fun setupView(channel: Channel) {
        channelItemListAdapter = ChannelItemListAdapter()
        activity_channel_detail_recycler_view.apply {
            adapter = channelItemListAdapter
            layoutManager = LinearLayoutManager(this@ChannelDetailActivity)
        }

        activity_channel_detail_name.text = channel.name
        activity_channel_detail_description.text = channel.description

        Glide.with(this)
            .load(channel.channelAvatar)
            .into(activity_channel_detail_photo)
    }

    private fun setupToolbar() {
        setSupportActionBar(activity_channel_detail_toolbar)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
        activity_channel_detail_toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        activity_channel_detail_toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun updateChannelItemList() {
        val newChannelItemList = channel.channelItems.copyOfDataList
        channelItemList = newChannelItemList
        runOnUiThread {
            channelItemListAdapter.submitList(channelItemList)
            channelItemListAdapter.notifyDataSetChanged()
        }
    }
}